package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.view.ViewTreeRenderer
import com.gzq.uiframework.runtime.Observation
import com.gzq.uiframework.runtime.RuntimeObservation

class RenderSession internal constructor(
    private val container: ViewGroup,
    private val content: UiTreeBuilder.() -> Unit,
) {
    private var observation: Observation? = null
    private var renderScheduled: Boolean = false

    fun render() {
        renderScheduled = false
        observation?.dispose()
        val (tree, nextObservation) = RuntimeObservation.observeReads(
            onInvalidated = ::scheduleRender,
        ) {
            buildVNodeTree(content)
        }
        observation = nextObservation
        ViewTreeRenderer.renderInto(container, tree)
    }

    fun dispose() {
        observation?.dispose()
        observation = null
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
