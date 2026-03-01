package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class LayoutsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Layouts"

    override val demoSubtitle: String =
        "Custom containers, placement rules, spacing, alignment, and layout edge cases."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.LayoutPage()
    }
}
