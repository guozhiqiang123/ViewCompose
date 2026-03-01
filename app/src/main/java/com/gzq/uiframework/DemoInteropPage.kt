package com.gzq.uiframework

import android.widget.TextView
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AndroidView
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.InteropPage() {
    val alternateLabelsState = remember { mutableStateOf(false) }
    val benchmarkToggleState = remember { mutableStateOf(false) }
    val selectedPageState = remember { mutableStateOf(0) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "basics", "verify")
        1 -> listOf("page", "page_filter", "theme_bridge", "verify")
        else -> listOf("page", "page_filter", "why_it_matters", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Interop",
                goal = "Keep AndroidView as a first-class migration path and verify that native widgets still fit inside framework theme and state boundaries.",
                modules = listOf("AndroidView", "theme bridge", "local propagation"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Basics", "Theme", "Why"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "Interop Benchmark Anchor",
                subtitle = "Keep a native TextView interop update path visible in the first viewport so benchmark runs do not depend on the longer guide content below.",
            ) {
                Text(
                    text = if (benchmarkToggleState.value) {
                        "Interop benchmark native state: alternate"
                    } else {
                        "Interop benchmark native state: primary"
                    },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Button(
                    text = if (benchmarkToggleState.value) {
                        "Interop Benchmark Alternate"
                    } else {
                        "Interop Benchmark Primary"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                    onClick = {
                        benchmarkToggleState.value = !benchmarkToggleState.value
                    },
                )
                Button(
                    text = "Reset Interop Benchmark",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        benchmarkToggleState.value = false
                    },
                )
                AndroidView(
                    key = "interop_benchmark_text_view",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        val textView = view as TextView
                        textView.text = if (benchmarkToggleState.value) {
                            "Native benchmark TextView: alternate"
                        } else {
                            "Native benchmark TextView: primary"
                        }
                    },
                )
                BenchmarkRouteCallout(
                    route = "Launcher -> MainActivity(extra=interop) -> Interop -> Interop Benchmark Anchor",
                    stableTargets = listOf(
                        "Interop Benchmark Primary",
                        "Reset Interop Benchmark",
                    ),
                )
                Text(
                    text = "Stable route: launcher -> interop module -> benchmark anchor",
                    style = UiTextStyle(fontSizeSp = 12.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 8.dp),
                )
            }

            "basics" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "AndroidView Basics",
                subtitle = "Legacy native views still mount inside the same declarative tree and can react to framework state.",
            ) {
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.margin(bottom = 12.dp),
                ) {
                    Button(
                        text = if (alternateLabelsState.value) "Show Primary Copy" else "Show Alternate Copy",
                        onClick = {
                            alternateLabelsState.value = !alternateLabelsState.value
                        },
                    )
                }
                AndroidView(
                    key = "interop_text_view",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        val textView = view as TextView
                        textView.text = if (alternateLabelsState.value) {
                            "Native TextView mirror: alternate content enabled"
                        } else {
                            "Native TextView mirror: primary content enabled"
                        }
                    },
                )
            }

            "theme_bridge" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "Theme Bridge",
                subtitle = "Interop pages should prove that framework theme locals and Android view defaults can coexist instead of fighting each other.",
            ) {
                Text(
                    text = "Current chapter keeps AndroidView inside the same theme container so text, spacing, and surrounding surfaces are still driven by UIFramework tokens.",
                )
                Text(
                    text = "Later this page should verify themed native widgets, custom view adapters, and fragment host containers.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "why_it_matters" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "Why Interop Matters",
                subtitle = "Interop is one of the biggest practical advantages of this framework compared with a pure Compose rewrite path.",
            ) {
                Text(
                    text = "Use this chapter to validate gradual migration: existing custom views, business widgets, and SDK surfaces should still be mountable without rewriting them first.",
                )
            }

            else -> VerificationNotesSection(
                what = "Interop chapter should prove that old View widgets can stay mounted inside the declarative tree without losing state or theme alignment.",
                howToVerify = listOf(
                    "点击切换文案按钮，确认原生 TextView 文案实时更新。",
                    "切换全局 theme mode，确认原生 View 所在容器仍与框架主题协调。",
                    "反复切换章节并返回 Interop，确认 AndroidView 依旧可用。",
                ),
                expected = listOf(
                    "AndroidView update 会响应框架状态变化。",
                    "theme locals 和 Android theme bridge 可以共存。",
                    "Interop 章节不会因为延迟 session 丢失 local 上下文。",
                ),
                relatedGaps = listOf(
                    "还没有 custom view adapter catalog 和 Fragment host demo。",
                ),
            )
        }
    }
}
