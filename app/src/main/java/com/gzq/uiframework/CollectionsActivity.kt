package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class CollectionsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Collections"

    override val demoSubtitle: String =
        "LazyColumn, keyed reorder, item state, pager integration, and collection stress paths."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.CollectionPage()
    }
}
