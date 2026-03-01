package com.gzq.uiframework.widget.core

import android.view.ViewGroup

fun renderInto(
    container: ViewGroup,
    debug: Boolean = false,
    debugTag: String = "UIFramework",
    content: UiTreeBuilder.() -> Unit,
): RenderSession {
    return RenderSession(
        container = container,
        content = content,
        debug = debug,
        debugTag = debugTag,
    ).also(RenderSession::render)
}
