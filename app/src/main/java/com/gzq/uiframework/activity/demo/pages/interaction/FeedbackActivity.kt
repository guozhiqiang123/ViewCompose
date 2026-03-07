package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_FEEDBACK_PAGE_INDEX = "feedback_page_index"

class FeedbackActivity : DemoRenderActivity() {
    override val demoTitle: String = "Feedback"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.FeedbackPage(
            initialPageIndex = intent?.getIntExtra(EXTRA_FEEDBACK_PAGE_INDEX, 0) ?: 0,
        )
    }
}
