package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.visibility
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Image
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SegmentedControl
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.TabPager
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextField
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.key
import com.gzq.uiframework.widget.core.produceState
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.StatePage(
    initialPageIndex: Int = 0,
    onOpenDiagnostics: () -> Unit,
) {
    val benchmarkStepState = remember { mutableStateOf(0) }
    val clickCountState = remember { mutableStateOf(0) }
    val panelVisibleState = remember { mutableStateOf(true) }
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 3)) }
    val patchStepState = remember { mutableStateOf(0) }
    val patchFieldValueState = remember { mutableStateOf("value-0") }
    val patchSegmentIndexState = remember { mutableStateOf(0) }
    val patchTabIndexState = remember { mutableStateOf(0) }
    val stableTabIndexState = remember { mutableStateOf(0) }
    val summaryState = remember {
        derivedStateOf {
            val value = clickCountState.value
            when {
                value == 0 -> "No clicks yet"
                value % 2 == 0 -> "Even clicks: $value"
                else -> "Odd clicks: $value"
            }
        }
    }
    val timelineState = produceState(
        initialValue = "Last update: waiting",
        clickCountState.value,
    ) {
        value = "Last update: ${clickCountState.value} tap(s) committed"
        null
    }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "counter", "verify")
        1 -> listOf("page", "page_filter", "panel", "verify")
        2 -> listOf("page", "page_filter", "patch", "verify")
        else -> listOf("page", "page_filter", "verify")
    }

    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "State & Effects",
                goal = "Exercise the runtime primitives directly so remember, derived state, produced values, and keyed identity can be inspected by hand.",
                modules = listOf("ui-runtime", "remember", "effects", "key scopes"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Core", "Identity", "Patch", "Checklist"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "State Benchmark Anchor",
                subtitle = "This block stays on the default Core page and keeps the benchmark controls inside the first viewport.",
            ) {
                BenchmarkRouteCallout(
                    route = "Launcher -> MainActivity(extra=state) -> State -> State Benchmark Anchor",
                    stableTargets = listOf(
                        "Advance State Benchmark 0",
                        "Reset State Benchmark",
                    ),
                )
                Text(
                    text = "Stable route: launcher -> state module -> benchmark anchor",
                    style = UiTextStyle(fontSizeSp = 12.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Text(
                    text = "Benchmark step ${benchmarkStepState.value}",
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Button(
                    text = "Advance State Benchmark ${benchmarkStepState.value}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                    onClick = {
                        benchmarkStepState.value = benchmarkStepState.value + 1
                    },
                )
                Button(
                    text = "Reset State Benchmark",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        benchmarkStepState.value = 0
                    },
                )
            }

            "counter" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "remember + derivedStateOf + produceState",
                subtitle = "This block shows local state, derived labels, and a small produced status string.",
            ) {
                Text(text = "Clicks: ${clickCountState.value}")
                Text(
                    text = summaryState.value,
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                )
                Text(
                    text = timelineState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
                Row(
                    spacing = 8.dp,
                    verticalAlignment = com.gzq.uiframework.renderer.layout.VerticalAlignment.Center,
                    modifier = Modifier.margin(top = 12.dp),
                ) {
                    Button(
                        text = "Increment",
                        onClick = {
                            clickCountState.value = clickCountState.value + 1
                        },
                    )
                    Button(
                        text = "Reset",
                        onClick = {
                            clickCountState.value = 0
                        },
                    )
                }
            }

            "panel" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "key Scope + Conditional UI",
                subtitle = "The transient panel keeps its own state while visible, and is fully recreated when toggled back in.",
            ) {
                Button(
                    text = if (panelVisibleState.value) "Hide panel" else "Show panel",
                    modifier = Modifier.margin(bottom = 12.dp),
                    onClick = {
                        panelVisibleState.value = !panelVisibleState.value
                    },
                )
                Text(
                    text = "Visibility sample: hidden when the panel is off",
                    modifier = Modifier
                        .visibility(
                            if (panelVisibleState.value) {
                                Visibility.Visible
                            } else {
                                Visibility.Gone
                            },
                        )
                        .padding(bottom = 8.dp),
                )
                if (panelVisibleState.value) {
                    key("transient-panel") {
                        val panelTapState = remember { mutableStateOf(0) }
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .padding(12.dp),
                        ) {
                            Text(text = "Keyed transient panel")
                            Button(
                                text = "Panel taps: ${panelTapState.value}",
                                onClick = {
                                    panelTapState.value = panelTapState.value + 1
                                },
                            )
                        }
                    }
                }
            }

            "patch" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "Patch Stress",
                subtitle = "Drive the first batch of node-level patch targets together so manual testing and benchmark runs hit the same update path.",
            ) {
                val step = patchStepState.value
                Text(text = "Patch headline $step")
                Text(
                    text = "Patched nodes: Text, Button, TextField, SegmentedControl, TabPager, Row, Column, Box, Image",
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Column(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(top = 8.dp, bottom = 12.dp),
                ) {
                    Button(
                        text = "Advance patch state $step",
                        onClick = {
                            val nextStep = patchStepState.value + 1
                            patchStepState.value = nextStep
                            patchFieldValueState.value = "value-$nextStep"
                            patchSegmentIndexState.value = nextStep % 3
                            patchTabIndexState.value = nextStep % 2
                        },
                    )
                    Button(
                        text = "Reset patch state",
                        onClick = {
                            patchStepState.value = 0
                            patchFieldValueState.value = "value-0"
                            patchSegmentIndexState.value = 0
                            patchTabIndexState.value = 0
                        },
                    )
                }
                Button(
                    text = "Patch action $step",
                    onClick = {},
                    modifier = Modifier.margin(bottom = 12.dp),
                )
                Button(
                    text = "Open diagnostics renderer",
                    onClick = onOpenDiagnostics,
                    modifier = Modifier.margin(bottom = 12.dp),
                )
                TextField(
                    value = patchFieldValueState.value,
                    onValueChange = { patchFieldValueState.value = it },
                    label = "Patched field",
                    supportingText = "Current patch step: $step",
                    modifier = Modifier.margin(bottom = 12.dp),
                )
                SegmentedControl(
                    items = listOf("Alpha", "Beta", "Gamma"),
                    selectedIndex = patchSegmentIndexState.value,
                    onSelectionChange = { patchSegmentIndexState.value = it },
                    modifier = Modifier.margin(bottom = 12.dp),
                )
                Row(
                    spacing = if (step % 2 == 0) 8.dp else 16.dp,
                    arrangement = if (step % 2 == 0) MainAxisArrangement.Start else MainAxisArrangement.SpaceEvenly,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "Row A")
                    Text(text = "Row B")
                    Text(text = "Row C")
                }
                Column(
                    spacing = if (step % 2 == 0) 4.dp else 12.dp,
                    arrangement = if (step % 2 == 0) MainAxisArrangement.Start else MainAxisArrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "Col item 1")
                    Text(text = "Col item 2")
                }
                Box(
                    contentAlignment = if (step % 2 == 0) BoxAlignment.TopStart else BoxAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .padding(12.dp)
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "Box content $step")
                }
                Image(
                    source = ImageSource.Resource(android.R.drawable.ic_menu_gallery),
                    tint = if (step % 2 == 0) 0xFF000000.toInt() else 0xFFFF0000.toInt(),
                    modifier = Modifier.margin(bottom = 12.dp),
                )
                TabPager(
                    selectedTabIndex = patchTabIndexState.value,
                    onTabSelected = { patchTabIndexState.value = it },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Page(title = "Summary", key = "summary", contentToken = "summary-$step") {
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .padding(12.dp),
                        ) {
                            Text(text = "Tab summary $step")
                            Text(
                                text = "Patch scenario keeps tab host stable while page metadata changes.",
                                color = TextDefaults.secondaryColor(),
                            )
                        }
                    }
                    Page(title = "Details", key = "details", contentToken = "details-$step") {
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .padding(12.dp),
                        ) {
                            Text(text = "Tab details $step")
                            Text(
                                text = "Use this page when checking tab selection patching under repeated updates.",
                                color = TextDefaults.secondaryColor(),
                            )
                        }
                    }
                }
                Text(
                    text = "Stable token tab pager keeps page identity fixed while the page closure still reflects the latest outer state.",
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 4.dp),
                )
                TabPager(
                    selectedTabIndex = stableTabIndexState.value,
                    onTabSelected = { stableTabIndexState.value = it },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Page(title = "Stable Summary", key = "stable-summary", contentToken = "stable-summary") {
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .padding(12.dp),
                        ) {
                            Text(text = "Stable summary $step")
                            Text(
                                text = "This page should still refresh after Advance patch state even though the tab page token stays stable.",
                                color = TextDefaults.secondaryColor(),
                            )
                        }
                    }
                    Page(title = "Stable Details", key = "stable-details", contentToken = "stable-details") {
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .padding(12.dp),
                        ) {
                            Text(text = "Stable details $step")
                            Text(
                                text = "Use this page to catch stale tab page closures when keyed page metadata does not change.",
                                color = TextDefaults.secondaryColor(),
                            )
                        }
                    }
                }
            }

            else -> VerificationNotesSection(
                what = "State chapter should reveal whether root rerendering, local identity, and conditional recreation behave predictably under repeated interaction.",
                howToVerify = listOf(
                    "连续点击 Increment 和 Reset，确认派生文案与 timeline 一起更新。",
                    "隐藏再显示 transient panel，确认 panel 内点击计数会被重建。",
                    "进入 Patch 页面，连续点击 Advance patch state，确认 Text、Button、TextField、SegmentedControl 和 TabPager 都同步更新。",
                    "切换 theme mode 后再继续点击，确认状态值不受主题刷新影响。",
                ),
                expected = listOf(
                    "remember 状态在同一 identity 下保留，在 key 变化后重建。",
                    "derivedStateOf 和 produceState 不会落后于源状态。",
                    "Patch 页面里的第一批节点会优先走 patch，而不是退回全量重绑。",
                    "条件 UI 显隐不会留下脏状态。",
                ),
                relatedGaps = listOf(
                    "还没有更细粒度的通用 subtree recomposition。",
                    "还没有把 patch/rebind/skipped 统计直接可视化到页面上。",
                ),
            )
        }
    }
}
