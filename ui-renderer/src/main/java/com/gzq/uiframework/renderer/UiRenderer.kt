package com.gzq.uiframework.renderer

import com.gzq.uiframework.runtime.UiRuntime

/**
 * Renderer entry marker for future vnode, reconciler, and mount APIs.
 */
object UiRenderer {
    val dependencyChain: List<String> = listOf(
        UiRuntime.NAME,
        "ui-renderer",
    )
}
