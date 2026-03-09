package com.viewcompose

import android.view.ViewGroup
import com.viewcompose.widget.core.UiTreeBuilder

internal const val EXTRA_GESTURES_PAGE_INDEX = "gestures_page_index"

class GesturesActivity : DemoRenderActivity() {
    override val demoTitle: String = "Gestures"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.GesturePage(
            initialPageIndex = intent?.getIntExtra(EXTRA_GESTURES_PAGE_INDEX, 0) ?: 0,
        )
    }
}
