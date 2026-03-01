package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.textColor
import com.gzq.uiframework.renderer.modifier.visibility
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.key
import com.gzq.uiframework.widget.core.produceState
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.StatePage() {
    val clickCountState = remember { mutableStateOf(0) }
    val panelVisibleState = remember { mutableStateOf(true) }
    val selectedPageState = remember { mutableStateOf(0) }
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
        0 -> listOf("page", "page_filter", "counter", "verify")
        1 -> listOf("page", "page_filter", "panel", "verify")
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
                pages = listOf("Core", "Identity", "Checklist"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "counter" -> DemoSection(
                title = "remember + derivedStateOf + produceState",
                subtitle = "This block shows local state, derived labels, and a small produced status string.",
            ) {
                Text(text = "Clicks: ${clickCountState.value}")
                Text(
                    text = summaryState.value,
                    modifier = Modifier
                        .textColor(TextDefaults.secondaryColor())
                        .padding(vertical = 4.dp),
                )
                Text(
                    text = timelineState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.textColor(TextDefaults.secondaryColor()),
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

            "panel" -> DemoSection(
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

            else -> VerificationNotesSection(
                what = "State chapter should reveal whether root rerendering, local identity, and conditional recreation behave predictably under repeated interaction.",
                howToVerify = listOf(
                    "连续点击 Increment 和 Reset，确认派生文案与 timeline 一起更新。",
                    "隐藏再显示 transient panel，确认 panel 内点击计数会被重建。",
                    "切换 theme mode 后再继续点击，确认状态值不受主题刷新影响。",
                ),
                expected = listOf(
                    "remember 状态在同一 identity 下保留，在 key 变化后重建。",
                    "derivedStateOf 和 produceState 不会落后于源状态。",
                    "条件 UI 显隐不会留下脏状态。",
                ),
                relatedGaps = listOf(
                    "还没有更细粒度的通用 subtree recomposition。",
                ),
            )
        }
    }
}
