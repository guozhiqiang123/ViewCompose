package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_NAVIGATION_PAGE_INDEX = "navigation_page_index"

class NavigationActivity : DemoRenderActivity() {
    override val demoTitle: String = "Navigation"

    override val demoSubtitle: String =
        "TopAppBar, BottomAppBar, NavigationBar, Scaffold 导航组件。"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.NavigationPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_NAVIGATION_PAGE_INDEX, 0) ?: 0,
        )
    }
}
