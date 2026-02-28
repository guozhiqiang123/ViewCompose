package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.view.MountedNode
import com.gzq.uiframework.renderer.view.ViewTreeRenderer
import com.gzq.uiframework.runtime.Observation
import com.gzq.uiframework.runtime.RuntimeObservation

class RenderSession internal constructor(
    private val container: ViewGroup,
    private val content: UiTreeBuilder.() -> Unit,
) {
    private var mountedNodes: List<MountedNode> = emptyList()
    private var observation: Observation? = null
    private var renderScheduled: Boolean = false
    private val rememberStore = RememberStore()

    fun render() {
        renderScheduled = false
        observation?.dispose()
        val (tree, nextObservation) = RuntimeObservation.observeReads(
            onInvalidated = ::scheduleRender,
        ) {
            RememberContext.withStore(rememberStore) {
                buildVNodeTree(content)
            }
        }
        observation = nextObservation
        mountedNodes = ViewTreeRenderer.renderInto(
            container = container,
            previous = mountedNodes,
            nodes = tree,
        )
    }

    fun dispose() {
        observation?.dispose()
        observation = null
        mountedNodes = emptyList()
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
