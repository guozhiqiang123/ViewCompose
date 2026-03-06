package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_MODIFIERS_PAGE_INDEX = "modifiers_page_index"

class ModifiersActivity : DemoRenderActivity() {
    override val demoTitle: String = "Modifiers"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.ModifiersPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_MODIFIERS_PAGE_INDEX, 0) ?: 0,
        )
    }
}
