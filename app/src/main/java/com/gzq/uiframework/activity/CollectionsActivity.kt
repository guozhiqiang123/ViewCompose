package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_COLLECTIONS_PAGE_INDEX = "collections_page_index"

class CollectionsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Collections"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.CollectionPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_COLLECTIONS_PAGE_INDEX, 0) ?: 0,
        )
    }
}
