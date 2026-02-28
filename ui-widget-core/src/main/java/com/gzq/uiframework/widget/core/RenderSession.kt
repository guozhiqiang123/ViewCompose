package com.gzq.uiframework.widget.core

import android.util.Log
import android.view.ViewGroup
import com.gzq.uiframework.renderer.debug.debugSummary
import com.gzq.uiframework.renderer.debug.debugTree
import com.gzq.uiframework.renderer.view.MountedNode
import com.gzq.uiframework.renderer.view.ViewTreeRenderer
import com.gzq.uiframework.runtime.Observation
import com.gzq.uiframework.runtime.RuntimeObservation

class RenderSession internal constructor(
    private val container: ViewGroup,
    private val content: UiTreeBuilder.() -> Unit,
    private val debug: Boolean = false,
    private val debugTag: String = "UIFramework",
) {
    private var mountedNodes: List<MountedNode> = emptyList()
    private var observation: Observation? = null
    private var renderScheduled: Boolean = false
    private val rememberStore = RememberStore()
    private val effectStore = EffectStore()

    fun render() {
        renderScheduled = false
        observation?.dispose()
        val (tree, nextObservation) = RuntimeObservation.observeReads(
            onInvalidated = ::scheduleRender,
        ) {
            EffectContext.withStore(effectStore) {
                RememberContext.withStore(rememberStore) {
                    buildVNodeTree(content)
                }
            }
        }
        observation = nextObservation
        mountedNodes = ViewTreeRenderer.renderInto(
            container = container,
            previous = mountedNodes,
            nodes = tree,
            onReconcile = { reconcileResult ->
                if (debug) {
                    Log.d(debugTag, "VNode tree\n${tree.debugTree()}")
                    Log.d(debugTag, "Reconcile\n${reconcileResult.debugSummary()}")
                }
            },
        )
        effectStore.commit()
    }

    fun dispose() {
        observation?.dispose()
        observation = null
        mountedNodes = emptyList()
        effectStore.disposeAll()
        renderScheduled = false
    }

    private fun scheduleRender() {
        if (renderScheduled) {
            return
        }
        renderScheduled = true
        container.post(::render)
    }
}
