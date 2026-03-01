package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class FeedbackActivity : DemoRenderActivity() {
    override val demoTitle: String = "Feedback"

    override val demoSubtitle: String =
        "Transient feedback overlays, host-driven presentation, and manual verification paths."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.FeedbackPage()
    }
}
