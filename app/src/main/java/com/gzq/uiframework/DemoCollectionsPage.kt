package com.gzq.uiframework

import android.widget.TextView
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AndroidView
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonSize
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.Surface
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.SurfaceVariant
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.produceState
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.CollectionPage() {
    val reversedState = remember { mutableStateOf(false) }
    val alternateLabelsState = remember { mutableStateOf(false) }
    val stressRotateState = remember { mutableStateOf(false) }
    val stressEdgeItemState = remember { mutableStateOf(false) }
    val selectedPageState = remember { mutableStateOf(0) }
    val listOrderState = produceState(
        initialValue = "List order: A-B-C",
        reversedState.value,
    ) {
        value = if (reversedState.value) "List order: C-B-A" else "List order: A-B-C"
        null
    }
    val keyedItems = if (reversedState.value) {
        listOf("C", "B", "A")
    } else {
        listOf("A", "B", "C")
    }.map { id ->
        DemoListItem(
            id = id,
            title = if (alternateLabelsState.value) {
                "Lazy item $id (alt)"
            } else {
                "Lazy item $id"
            },
        )
    }
    val stressItems = buildList {
        val baseIds = if (stressRotateState.value) {
            listOf("C", "D", "A", "B")
        } else {
            listOf("A", "B", "C", "D")
        }
        if (stressEdgeItemState.value) {
            add(
                DemoListItem(
                    id = "X",
                    title = "Inserted item X",
                ),
            )
        }
        baseIds.forEach { id ->
            add(
                DemoListItem(
                    id = id,
                    title = if (alternateLabelsState.value) {
                        "Stress item $id (alt)"
                    } else {
                        "Stress item $id"
                    },
                ),
            )
        }
    }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("page", "page_filter", "controls", "verify")
        1 -> listOf("page", "page_filter", "list", "verify")
        2 -> listOf("page", "page_filter", "stress", "verify")
        else -> listOf("page", "page_filter", "interop", "verify")
    }

    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Collections",
                goal = "Validate keyed list reuse, local item state preservation, and AndroidView coexistence inside lazy containers.",
                modules = listOf("LazyColumn", "diff", "lazy item sessions", "AndroidView"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Controls", "List", "Stress", "Interop"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "controls" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "Collection Controls",
                subtitle = "These buttons mutate the source list and labels while preserving keyed item state.",
            ) {
                Text(text = listOrderState.value)
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.margin(top = 12.dp),
                ) {
                    Button(
                        text = if (reversedState.value) "Show A-B-C" else "Show C-B-A",
                        onClick = {
                            reversedState.value = !reversedState.value
                        },
                    )
                    Button(
                        text = if (alternateLabelsState.value) "Primary labels" else "Alternate labels",
                        onClick = {
                            alternateLabelsState.value = !alternateLabelsState.value
                        },
                    )
                }
            }

            "list" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "LazyColumn",
                subtitle = "Each item keeps its own local state while keyed reorder and content updates pass through the diff layer.",
            ) {
                LazyColumn(
                    items = keyedItems,
                    key = { item -> item.id },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                ) { item ->
                    val itemCountState = remember { mutableStateOf(0) }
                    Column(
                        key = item.id,
                        spacing = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .padding(12.dp),
                    ) {
                        Text(text = item.title)
                        Button(
                            text = "Item ${item.id} taps: ${itemCountState.value}",
                            onClick = {
                                itemCountState.value = itemCountState.value + 1
                            },
                        )
                    }
                }
            }

            "stress" -> ScenarioSection(
                kind = ScenarioKind.Stress,
                title = "Lazy Stress Cases",
                subtitle = "This page compresses reorder, insertion, label mutation, and constrained height into one repeatable manual test path.",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Open Collections -> Stress page",
                    stableTargets = listOf(
                        "Linear Order / Rotate Order",
                        "Insert X / Remove X",
                        "Stable key: A/B/C/D/X",
                    ),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Button(
                        text = if (stressRotateState.value) "Linear Order" else "Rotate Order",
                        size = ButtonSize.Compact,
                        onClick = {
                            stressRotateState.value = !stressRotateState.value
                        },
                    )
                    Button(
                        text = if (stressEdgeItemState.value) "Remove X" else "Insert X",
                        size = ButtonSize.Compact,
                        onClick = {
                            stressEdgeItemState.value = !stressEdgeItemState.value
                        },
                    )
                }
                Text(
                    text = "Active ids: ${stressItems.joinToString(separator = " -> ") { it.id }}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier
                        .margin(bottom = 12.dp),
                )
                LazyColumn(
                    items = stressItems,
                    key = { item -> item.id },
                    spacing = 8.dp,
                    contentPadding = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius()),
                ) { item ->
                    val itemCountState = remember { mutableStateOf(0) }
                    Surface(
                        variant = SurfaceVariant.Default,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            key = item.id,
                            spacing = 6.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        ) {
                            Text(text = item.title)
                            Text(
                                text = "Stable key: ${item.id}",
                                style = UiTextStyle(fontSizeSp = 12.sp),
                                color = TextDefaults.secondaryColor(),
                            )
                            Button(
                                text = "Item ${item.id} taps: ${itemCountState.value}",
                                size = ButtonSize.Compact,
                                onClick = {
                                    itemCountState.value = itemCountState.value + 1
                                },
                            )
                        }
                    }
                }
            }

            "interop" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "AndroidView Interop",
                subtitle = "Legacy views still plug into the same declarative state flow.",
            ) {
                val summaryText = if (alternateLabelsState.value) {
                    "Legacy TextView mirror: alternate labels enabled"
                } else {
                    "Legacy TextView mirror: primary labels enabled"
                }
                AndroidView(
                    key = "legacy_summary",
                    modifier = Modifier.padding(vertical = 4.dp),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        (view as TextView).text = summaryText
                    },
                )
            }

            else -> VerificationNotesSection(
                what = "Collections should reveal list diff bugs, state leakage between items, and local propagation problems in nested sessions.",
                howToVerify = listOf(
                    "对单个 item 连续点击计数，再切换 A-B-C / C-B-A 顺序，确认同 key 的计数被保留。",
                    "切换 Alternate labels，确认标题变化但 item 本地状态不丢。",
                    "在 Stress 页先点某个 item，再切 Rotate Order / Insert X，确认同 id 的计数继续保留。",
                    "观察 AndroidView interop 区域，确认它能跟随列表外部状态同步更新。",
                ),
                expected = listOf(
                    "keyed reorder 只移动节点，不重建对应 item session。",
                    "lazy item remember 状态不会串位。",
                    "列表与原生 View 互操作不会丢 local 上下文。",
                ),
                relatedGaps = listOf(
                    "还没有 LazyRow、LazyGrid、sticky headers 和显式 list state。",
                ),
            )
        }
    }
}
