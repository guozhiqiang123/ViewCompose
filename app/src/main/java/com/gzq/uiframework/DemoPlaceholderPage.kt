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
        items = listOf("overview", "planned", "gaps", "verify"),
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "overview" -> ChapterPageOverviewSection(
                title = title,
                goal = "Keep this chapter visible now so the demo capability map stays stable while the underlying framework work is still missing.",
                modules = listOf("roadmap placeholder", "future scenario groups"),
            )

            "planned" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "Planned Pages",
                subtitle = subtitle,
            ) {
                Text(
                    text = "This chapter is intentionally present now so the demo structure stays stable while framework capability catches up.",
                )
                plannedPages.forEach { page ->
                    Text(text = "• $page")
                }
            }

            "gaps" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "Current Gaps",
                subtitle = "These gaps map directly to framework work that is still missing compared with Compose.",
            ) {
                currentGaps.forEach { gap ->
                    Text(text = "• $gap")
                }
            }

            else -> VerificationNotesSection(
                what = "Use this placeholder chapter as a stable roadmap surface instead of a dead-end catalog card.",
                howToVerify = listOf(
                    "从目录进入该章节，确认可以正常打开并返回。",
                    "检查 planned pages 与 current gaps 是否和当前 roadmap 一致。",
                    "切换全局主题，确认 placeholder 页面仍保持和正式模块一致的壳层表现。",
                ),
                expected = listOf(
                    "planned 模块已经进入统一的 Activity 壳。",
                    "后续补能力时只需要替换章节内容，不需要重做 demo 导航结构。",
                    "placeholder 页面本身不会影响现有 benchmark 路径。",
                ),
            )
        }
    }
}
