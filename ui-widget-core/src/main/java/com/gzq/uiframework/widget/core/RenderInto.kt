package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.view.ViewTreeRenderer

fun renderInto(
    container: ViewGroup,
    content: UiTreeBuilder.() -> Unit,
) {
    val tree = buildVNodeTree(content)
    ViewTreeRenderer.renderInto(container, tree)
}
