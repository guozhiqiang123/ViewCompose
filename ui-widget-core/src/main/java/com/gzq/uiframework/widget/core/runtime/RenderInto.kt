package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.view.tree.RenderStats

fun renderInto(
    container: ViewGroup,
    debug: Boolean = false,
    debugTag: String = "UIFramework",
    onRenderStats: ((RenderStats) -> Unit)? = null,
    content: UiTreeBuilder.() -> Unit,
): RenderSession {
    return RenderSession(
        container = container,
        content = content,
        debug = debug,
        debugTag = debugTag,
        onRenderStats = onRenderStats,
    ).also(RenderSession::render)
}
