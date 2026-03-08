package com.viewcompose.widget.core

import android.util.Log
import android.view.ViewGroup
import com.viewcompose.runtime.composition.ComposerLite
import java.util.concurrent.atomic.AtomicInteger

class RenderSession(
    private val container: ViewGroup,
    private val content: UiTreeBuilder.() -> Unit,
    private val debug: Boolean = false,
    private val debugTag: String = "ViewCompose",
    private val overlayHost: OverlayHost = OverlayHostDefaults.noOp,
    private val onRenderStats: ((Any) -> Unit)? = null,
    private val onRenderResult: ((Any) -> Unit)? = null,
) {
    private val overlaySessionId = OverlaySessionId("render-session-${nextOverlaySessionId.incrementAndGet()}")
    private var mountedNodes: List<Any> = emptyList()
    private var disposed: Boolean = false
    private val overlayRequestStore = OverlayRequestStore()
    private val frameDispatcher = FrameAlignedRenderDispatcher(
        frameClock = AndroidChoreographerFrameClock(),
        onFrameRender = ::render,
    )
    private val composer = ComposerLite(
        warningLogger = { message -> Log.w(debugTag, message) },
        onInvalidated = ::scheduleRender,
    )

    fun render() {
        if (disposed) return
        frameDispatcher.cancelPending()
        try {
            var tree: List<com.viewcompose.ui.node.VNode> = emptyList()
            if (!composer.hasPendingInvalidations()) {
                // External render requests (e.g. lazy/pager sessionUpdater) must recompose root even
                // without runtime state invalidation signals.
                composer.requestRootRecompose()
            }
            LocalContext.provide(LocalOverlayHost.holder, overlayHost) {
                OverlayRequestContext.withStore(overlayRequestStore) {
                    ComposerContext.withComposer(composer) {
                        tree = composer.composeRoot {
                            buildVNodeTree(content)
                        }
                    }
                }
            }
            val frame = CoreRenderEngineProvider.engine.renderInto(
                container = container,
                previousMountedNodes = mountedNodes,
                nodes = tree,
            )
            mountedNodes = frame.mountedNodes
            overlayHost.commit(
                sessionId = overlaySessionId,
                requests = overlayRequestStore.currentRequests(),
            )
            if (debug && frame.renderResult == null) {
                Log.d(debugTag, "Rendered ${tree.size} root nodes")
            }
            onRenderStats?.invoke(frame.renderStats ?: Unit)
            onRenderResult?.invoke(frame.renderResult ?: Unit)
            composer.commitSideEffects()
        } catch (e: Exception) {
            Log.e(debugTag, "Render failed, keeping previous view tree", e)
        }
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        frameDispatcher.dispose()
        CoreRenderEngineProvider.engine.disposeMounted(
            container = container,
            mountedNodes = mountedNodes,
        )
        mountedNodes = emptyList()
        overlayHost.clear(overlaySessionId)
        composer.dispose()
    }

    private fun scheduleRender() {
        if (disposed) return
        frameDispatcher.requestFrame()
    }

    private companion object {
        val nextOverlaySessionId = AtomicInteger(0)
    }
}
