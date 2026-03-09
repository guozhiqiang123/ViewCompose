package com.viewcompose

import android.view.ViewGroup
import com.viewcompose.widget.core.UiTreeBuilder

internal const val EXTRA_PREVIEW_PAGE_INDEX = "preview_page_index"

class PreviewActivity : DemoRenderActivity() {
    override val demoTitle: String = "Preview"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.PreviewPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_PREVIEW_PAGE_INDEX, 0) ?: 0,
        )
    }
}
