package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class NavigationActivity : DemoRenderActivity() {
    override val demoTitle: String = "Navigation"

    override val demoSubtitle: String =
        "Planned host integration, stack modeling, and state preservation scenarios."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.ChapterPlaceholderPage(
            title = "Navigation",
            subtitle = "This chapter holds the future host integration and navigation model work while the framework still relies on Activity-based shells.",
            plannedPages = listOf(
                "Host integration and screen switching",
                "Back stack experiments",
                "State restoration across screens",
                "Deep link simulation",
            ),
            currentGaps = listOf(
                "No framework-level navigation model yet",
                "No page stack or deep link abstraction",
                "No navigation benchmark or diagnostics flow",
            ),
        )
    }
}
