package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_ACTIONS_PAGE_INDEX = "actions_page_index"

class ActionsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Actions"

    override val demoSubtitle: String =
        "Card, FAB, Chip, TextButton, ListItem, Badge 等 Action 类组件。"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.ActionsPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_ACTIONS_PAGE_INDEX, 0) ?: 0,
        )
    }
}
