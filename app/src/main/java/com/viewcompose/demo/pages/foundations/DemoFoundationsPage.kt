package com.viewcompose

import androidx.appcompat.app.AppCompatActivity
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.fillMaxSize
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.LazyColumn
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.remember

internal fun UiTreeBuilder.OverviewPage(
    initialPageIndex: Int = 0,
    onOpenCapability: (Class<out AppCompatActivity>) -> Unit,
) {
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 3)) }
    val benchmarkState = remember { mutableStateOf(false) }
    val pageItems = foundationsPageItems(selectedPageState.value)
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "基础组件",
                goal = "验证核心视觉原语、主题作用域、组件默认值和媒体控件，然后再进入更高级的运行时场景。",
                modules = listOf("ui-widget-core", "ui-renderer", "theme locals", "component defaults"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("指南", "主题", "媒体", "排版"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "intro" -> FoundationsIntroSection()
            "benchmark" -> FoundationsBenchmarkSection(
                enabled = benchmarkState.value,
                onToggle = { benchmarkState.value = !benchmarkState.value },
                onReset = { benchmarkState.value = false },
            )
            "theme" -> FoundationsThemeSection()
            "overrides" -> FoundationsOverridesSection()
            "progress" -> FoundationsProgressSection()
            "media" -> FoundationsMediaSection()
            "typography" -> FoundationsTypographySection()
            "jump" -> FoundationsJumpSection(onOpenCapability)
            "surface" -> FoundationsSurfaceSection()
            else -> FoundationsVerificationSection()
        }
    }
}

private fun foundationsPageItems(
    selectedPageIndex: Int,
): List<String> {
    return when (selectedPageIndex) {
        0 -> listOf("benchmark", "page", "page_filter", "intro", "jump", "surface", "verify")
        1 -> listOf("overrides", "page", "page_filter", "theme", "verify")
        2 -> listOf("page", "page_filter", "progress", "media", "verify")
        else -> listOf("page", "page_filter", "typography", "verify")
    }
}
