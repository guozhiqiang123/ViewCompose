package com.gzq.uiframework.widget.core

import android.util.Log
import android.view.ViewGroup
import com.gzq.uiframework.renderer.debug.debugSummary
import com.gzq.uiframework.renderer.debug.debugTree
import com.gzq.uiframework.renderer.view.tree.MountedNode
import com.gzq.uiframework.renderer.view.tree.RenderStats
import com.gzq.uiframework.renderer.view.tree.RenderTreeResult
import com.gzq.uiframework.renderer.view.tree.ViewTreeRenderer
import com.gzq.uiframework.runtime.observation.Observation
import com.gzq.uiframework.runtime.observation.RuntimeObservation
import java.util.concurrent.atomic.AtomicInteger

class RenderSession internal constructor(
    private val container: ViewGroup,
    private val content: UiTreeBuilder.() -> Unit,
    private val debug: Boolean = false,
    private val debugTag: String = "UIFramework",
    private val overlayHost: OverlayHost = OverlayHostDefaults.noOp,
    private val onRenderStats: ((RenderStats) -> Unit)? = null,
    private val onRenderResult: ((RenderTreeResult) -> Unit)? = null,
) {
    private val overlaySessionId = OverlaySessionId("render-session-${nextOverlaySessionId.incrementAndGet()}")
    private var mountedNodes: List<MountedNode> = emptyList()
    private var observation: Observation? = null
    private var renderScheduled: Boolean = false
    private val renderRunnable = Runnable { render() }
    private val rememberStore = RememberStore()
    private val effectStore = EffectStore()
    private val sideEffectStore = SideEffectStore()
    private val overlayRequestStore = OverlayRequestStore()

    fun render() {
        renderScheduled = false
        observation?.dispose()
        val (tree, nextObservation) = RuntimeObservation.observeReads(
            onInvalidated = ::scheduleRender,
        ) {
            var builtTree: List<com.gzq.uiframework.renderer.node.VNode> = emptyList()
            LocalContext.provide(LocalOverlayHost, overlayHost) {
                OverlayRequestContext.withStore(overlayRequestStore) {
                    SideEffectContext.withStore(sideEffectStore) {
                        EffectContext.withStore(effectStore) {
                            RememberContext.withStore(rememberStore) {
                                builtTree = buildVNodeTree(content)
                            }
                        }
                    }
                }
            }
            builtTree
        }
        observation = nextObservation
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
        effectStore.commit()
        sideEffectStore.commit()
    }

    fun dispose() {
        observation?.dispose()
        observation = null
        container.removeCallbacks(renderRunnable)
        ViewTreeRenderer.disposeMounted(
            container = container,
            mountedNodes = mountedNodes,
        )
        mountedNodes = emptyList()
        overlayHost.clear(overlaySessionId)
        effectStore.disposeAll()
        sideEffectStore.disposeAll()
        renderScheduled = false
    }

    private fun scheduleRender() {
        if (renderScheduled) {
            return
        }
        renderScheduled = true
        container.post(renderRunnable)
    }

    companion object {
        private val nextOverlaySessionId = AtomicInteger(0)
    }
}
