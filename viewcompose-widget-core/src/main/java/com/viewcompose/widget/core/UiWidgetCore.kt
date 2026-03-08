package com.viewcompose.widget.core

/**
 * Widget layer marker for future declarative core widgets.
 */
object UiWidgetCore {
    val dependencyChain: List<String> = listOf(
        "viewcompose-runtime",
        "viewcompose-ui-contract",
        "viewcompose-widget-core",
    )
}
