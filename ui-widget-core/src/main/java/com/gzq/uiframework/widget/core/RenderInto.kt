package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.view.ViewTreeRenderer

fun renderInto(
    container: ViewGroup,
    content: UiTreeBuilder.() -> Unit,
): RenderSession {
    return RenderSession(container, content).also(RenderSession::render)
}
