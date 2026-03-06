package com.gzq.uiframework

import android.content.Intent
import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_FOUNDATIONS_PAGE_INDEX = "foundations_page_index"

class FoundationsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Foundations"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.OverviewPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_FOUNDATIONS_PAGE_INDEX, 0) ?: 0,
        ) { target ->
            startActivity(Intent(this, target))
        }
    }
}
