package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_LAYOUTS_PAGE_INDEX = "layouts_page_index"

class LayoutsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Layouts"

    override val demoSubtitle: String =
        "Custom containers, placement rules, spacing, alignment, and layout edge cases."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.LayoutPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_LAYOUTS_PAGE_INDEX, 0) ?: 0,
        )
    }
}
