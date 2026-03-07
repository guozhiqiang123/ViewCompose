package com.viewcompose.widget.core

import com.viewcompose.renderer.UiRenderer

/**
 * Widget layer marker for future declarative core widgets.
 */
object UiWidgetCore {
    val dependencyChain: List<String> = UiRenderer.dependencyChain + "ui-widget-core"
}
