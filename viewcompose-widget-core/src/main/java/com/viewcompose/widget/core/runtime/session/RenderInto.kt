package com.viewcompose.widget.core

import android.view.ViewGroup
import com.viewcompose.renderer.view.tree.RenderStats
import com.viewcompose.renderer.view.tree.RenderTreeResult

fun renderInto(
    container: ViewGroup,
    debug: Boolean = false,
    debugTag: String = "ViewCompose",
    overlayHost: OverlayHost = OverlayHostDefaults.noOp,
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.() -> Unit,
): RenderSession {
    return RenderSession(
        container = container,
        content = content,
        debug = debug,
        debugTag = debugTag,
        overlayHost = overlayHost,
        onRenderStats = onRenderStats,
        onRenderResult = onRenderResult,
    ).also(RenderSession::render)
}
