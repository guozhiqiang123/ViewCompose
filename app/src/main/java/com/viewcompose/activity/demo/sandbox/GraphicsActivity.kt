package com.viewcompose

import android.view.ViewGroup
import com.viewcompose.widget.core.UiTreeBuilder

class GraphicsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Graphics"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.ChapterPlaceholderPage(
            title = "Graphics",
            subtitle = "This chapter reserves the future draw and canvas surface so demo structure does not lag behind framework capability planning.",
            plannedPages = listOf(
                "Basic shapes and draw modifiers",
                "Paths and gradients",
                "Badges and indicators",
                "Chart primitives",
            ),
            currentGaps = listOf(
                "No draw modifier pipeline yet",
                "No canvas-backed widget primitives",
                "No graphics-specific diagnostics or benchmarks",
            ),
        )
    }
}
