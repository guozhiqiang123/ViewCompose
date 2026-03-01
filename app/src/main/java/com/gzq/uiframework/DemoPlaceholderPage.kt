package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal fun UiTreeBuilder.ChapterPlaceholderPage(
    title: String,
    subtitle: String,
    plannedPages: List<String>,
    currentGaps: List<String>,
) {
    LazyColumn(
        items = listOf("intro", "planned", "gaps"),
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "intro" -> DemoSection(
                title = title,
                subtitle = subtitle,
            ) {
                Text(
                    text = "This chapter is intentionally present now so the demo structure stays stable while framework capability catches up.",
                )
            }

            "planned" -> DemoSection(
                title = "Planned Pages",
                subtitle = "These are the first page groups reserved for this chapter.",
            ) {
                plannedPages.forEach { page ->
                    Text(text = "• $page")
                }
            }

            else -> DemoSection(
                title = "Current Gaps",
                subtitle = "These gaps map directly to framework work that is still missing compared with Compose.",
            ) {
                currentGaps.forEach { gap ->
                    Text(text = "• $gap")
                }
            }
        }
    }
}
