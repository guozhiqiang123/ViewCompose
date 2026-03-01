package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class AnimationActivity : DemoRenderActivity() {
    override val demoTitle: String = "Animation"

    override val demoSubtitle: String =
        "Planned state-driven motion, transitions, and list animation scenarios."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.ChapterPlaceholderPage(
            title = "Animation",
            subtitle = "This chapter tracks the future animation system and keeps the demo shell aligned with the Compose capability map.",
            plannedPages = listOf(
                "State-driven alpha and size",
                "Visibility and content transitions",
                "List item motion",
                "Animation diagnostics",
            ),
            currentGaps = listOf(
                "No formal animation runtime yet",
                "No transition API for container/content changes",
                "No list motion or gesture-coupled animation demos",
            ),
        )
    }
}
