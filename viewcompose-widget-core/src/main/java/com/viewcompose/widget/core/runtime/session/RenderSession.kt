package com.viewcompose.widget.core

import android.util.Log
import android.view.ViewGroup
import com.viewcompose.renderer.debug.debugSummary
import com.viewcompose.renderer.debug.debugTree
import com.viewcompose.renderer.view.tree.MountedNode
import com.viewcompose.renderer.view.tree.RenderStats
import com.viewcompose.renderer.view.tree.RenderTreeResult
import com.viewcompose.renderer.view.tree.ViewTreeRenderer
import com.viewcompose.runtime.composition.ComposerLite
import java.util.concurrent.atomic.AtomicInteger

class RenderSession internal constructor(
    private val container: ViewGroup,
    private val content: UiTreeBuilder.() -> Unit,
    private val debug: Boolean = false,
    private val debugTag: String = "ViewCompose",
    private val overlayHost: OverlayHost = OverlayHostDefaults.noOp,
    private val onRenderStats: ((RenderStats) -> Unit)? = null,
    private val onRenderResult: ((RenderTreeResult) -> Unit)? = null,
) {
    private val overlaySessionId = OverlaySessionId("render-session-${nextOverlaySessionId.incrementAndGet()}")
    private var mountedNodes: List<MountedNode> = emptyList()
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
            var tree: List<com.viewcompose.renderer.node.VNode> = emptyList()
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
            val renderResult = ViewTreeRenderer.renderInto(
                container = container,
                previous = mountedNodes,
                nodes = tree,
                onReconcile = { renderResult ->
                    if (debug) {
                        Log.d(debugTag, "VNode tree\n${tree.debugTree()}")
                        Log.d(debugTag, "Reconcile\n${renderResult.debugSummary()}")
                    }
                },
            )
            mountedNodes = renderResult.mountedNodes
            overlayHost.commit(
                sessionId = overlaySessionId,
                requests = overlayRequestStore.currentRequests(),
            )
            onRenderStats?.invoke(renderResult.stats)
            onRenderResult?.invoke(renderResult)
            composer.commitSideEffects()
        } catch (e: Exception) {
            Log.e(debugTag, "Render failed, keeping previous view tree", e)
        }
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        frameDispatcher.dispose()
        ViewTreeRenderer.disposeMounted(
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

    companion object {
        private val nextOverlaySessionId = AtomicInteger(0)
    }
}
