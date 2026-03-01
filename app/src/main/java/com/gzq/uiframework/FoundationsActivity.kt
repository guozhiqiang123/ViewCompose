package com.gzq.uiframework

import android.content.Intent
import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class FoundationsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Foundations"

    override val demoSubtitle: String =
        "Core visual primitives, theme scopes, component defaults, and media widgets."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.OverviewPage { target ->
            startActivity(Intent(this, target))
        }
    }
}
