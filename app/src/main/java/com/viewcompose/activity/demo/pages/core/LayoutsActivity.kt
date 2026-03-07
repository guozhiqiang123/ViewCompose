package com.viewcompose

import android.view.ViewGroup
import com.viewcompose.widget.core.UiTreeBuilder

internal const val EXTRA_LAYOUTS_PAGE_INDEX = "layouts_page_index"

class LayoutsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Layouts"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.LayoutPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_LAYOUTS_PAGE_INDEX, 0) ?: 0,
        )
    }
}
