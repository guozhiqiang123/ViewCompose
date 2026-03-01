package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.view.tree.RenderStats
import com.gzq.uiframework.renderer.view.tree.RenderTreeResult

fun renderInto(
    container: ViewGroup,
    debug: Boolean = false,
    debugTag: String = "UIFramework",
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.() -> Unit,
): RenderSession {
    return RenderSession(
        container = container,
        content = content,
        debug = debug,
        debugTag = debugTag,
        onRenderStats = onRenderStats,
        onRenderResult = onRenderResult,
    ).also(RenderSession::render)
}
