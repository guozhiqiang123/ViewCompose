package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.UiRenderer

/**
 * Widget layer marker for future declarative core widgets.
 */
object UiWidgetCore {
    val dependencyChain: List<String> = UiRenderer.dependencyChain + "ui-widget-core"
}
