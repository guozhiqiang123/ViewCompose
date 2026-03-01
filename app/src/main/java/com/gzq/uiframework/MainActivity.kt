package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class MainActivity : DemoRenderActivity() {
    override val showBackButton: Boolean = false

    override val demoTitle: String = "UIFramework Demo"

    override val demoSubtitle: String =
        "Module catalog aligned with Compose Tutorials categories and optimized for manual testing plus benchmark entry."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.DemoCatalogPage(root)
    }
}
