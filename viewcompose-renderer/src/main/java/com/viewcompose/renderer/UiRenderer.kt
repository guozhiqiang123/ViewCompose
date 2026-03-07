package com.viewcompose.renderer

import com.viewcompose.runtime.UiRuntime

/**
 * Renderer entry marker for vnode, reconciler, and mount APIs.
 */
object UiRenderer {
    val dependencyChain: List<String> = listOf(
        UiRuntime.NAME,
        "ui-renderer",
    )
}
