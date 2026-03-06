package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class GesturesActivity : DemoRenderActivity() {
    override val demoTitle: String = "Gestures"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.ChapterPlaceholderPage(
            title = "Gestures",
            subtitle = "This chapter is reserved for gesture and pointer input work once the runtime and input layers are ready.",
            plannedPages = listOf(
                "Tap, long press, and double tap",
                "Drag and swipe state",
                "Nested scroll and gesture conflicts",
                "Gesture diagnostics",
            ),
            currentGaps = listOf(
                "No dedicated pointer input system yet",
                "No drag/swipe gesture abstractions",
                "No nested gesture conflict demos",
            ),
        )
    }
}
