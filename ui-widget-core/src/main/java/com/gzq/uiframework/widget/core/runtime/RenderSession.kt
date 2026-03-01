package com.gzq.uiframework.widget.core

import android.util.Log
import android.view.ViewGroup
import com.gzq.uiframework.renderer.debug.debugSummary
import com.gzq.uiframework.renderer.debug.debugTree
import com.gzq.uiframework.renderer.view.tree.MountedNode
import com.gzq.uiframework.renderer.view.tree.ViewTreeRenderer
import com.gzq.uiframework.runtime.observation.Observation
import com.gzq.uiframework.runtime.observation.RuntimeObservation

class RenderSession internal constructor(
    private val container: ViewGroup,
    private val content: UiTreeBuilder.() -> Unit,
    private val debug: Boolean = false,
    private val debugTag: String = "UIFramework",
) {
    private var mountedNodes: List<MountedNode> = emptyList()
    private var observation: Observation? = null
    private var renderScheduled: Boolean = false
    private val renderRunnable = Runnable { render() }
    private val rememberStore = RememberStore()
    private val effectStore = EffectStore()
    private val sideEffectStore = SideEffectStore()

    fun render() {
        renderScheduled = false
        observation?.dispose()
        val (tree, nextObservation) = RuntimeObservation.observeReads(
            onInvalidated = ::scheduleRender,
        ) {
            SideEffectContext.withStore(sideEffectStore) {
                EffectContext.withStore(effectStore) {
                    RememberContext.withStore(rememberStore) {
                        buildVNodeTree(content)
                    }
                }
            }
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
}
