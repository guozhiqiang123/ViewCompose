package com.viewcompose

import com.viewcompose.renderer.layout.BoxAlignment
import com.viewcompose.renderer.layout.HorizontalAlignment
import com.viewcompose.renderer.layout.MainAxisArrangement
import com.viewcompose.renderer.layout.VerticalAlignment
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.backgroundColor
import com.viewcompose.renderer.modifier.clickable
import com.viewcompose.renderer.modifier.cornerRadius
import com.viewcompose.renderer.modifier.fillMaxSize
import com.viewcompose.renderer.modifier.fillMaxWidth
import com.viewcompose.renderer.modifier.height
import com.viewcompose.renderer.modifier.margin
import com.viewcompose.renderer.modifier.offset
import com.viewcompose.renderer.modifier.padding
import com.viewcompose.renderer.modifier.testTag
import com.viewcompose.renderer.modifier.zIndex
import com.viewcompose.renderer.node.ImageSource
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.Box
import com.viewcompose.widget.core.Button
import com.viewcompose.widget.core.ButtonSize
import com.viewcompose.widget.core.ButtonVariant
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.Divider
import com.viewcompose.widget.core.FlowColumn
import com.viewcompose.widget.core.FlowRow
import com.viewcompose.widget.core.Icon
import com.viewcompose.widget.core.LazyColumn
import com.viewcompose.widget.core.Row
import com.viewcompose.widget.core.ScrollableColumn
import com.viewcompose.widget.core.ScrollableRow
import com.viewcompose.widget.core.Surface
import com.viewcompose.widget.core.SurfaceDefaults
import com.viewcompose.widget.core.SurfaceVariant
import com.viewcompose.widget.core.Text
import com.viewcompose.widget.core.TextDefaults
import com.viewcompose.widget.core.Theme
import com.viewcompose.widget.core.UiTextStyle
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.dp
import com.viewcompose.widget.core.remember
import com.viewcompose.widget.core.sp

internal fun UiTreeBuilder.LayoutPage(
    initialPageIndex: Int = 0,
) {
    val boxTapState = remember { mutableStateOf(0) }
    val benchmarkState = remember { mutableStateOf(false) }
    val useLongLabelsState = remember { mutableStateOf(false) }
    val flowItemCountState = remember { mutableStateOf(8) }
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 5)) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "row", "column", "verify")
        1 -> listOf("page", "page_filter", "box", "verify")
        2 -> listOf("page", "page_filter", "edge", "verify")
        3 -> listOf("page", "page_filter", "flow", "verify")
        4 -> listOf("page", "page_filter", "scrollable", "verify")
        else -> listOf("page", "page_filter", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "布局组件",
                goal = "验证线性容器、Box 叠加、流式布局和滚动容器的测量、摆放、间距和子项覆盖的稳定性。",
                modules = listOf("DeclarativeLinearLayout", "DeclarativeBoxLayout", "FlowRow", "FlowColumn", "ScrollableColumn", "ScrollableRow"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("线性", "叠加", "边界", "流式", "滚动", "清单"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "布局 Benchmark 锚点",
                subtitle = "线性布局 benchmark 路径，不依赖分页过滤器。",
            ) {
                BenchmarkRouteCallout(
                    route = "Launcher -> Layouts -> 线性页 -> Benchmark 锚点",
                    stableTargets = listOf(
                        "Layouts Benchmark Compact / Expanded",
                        "Reset Layouts Benchmark",
                    ),
                )
                Button(
                    text = if (benchmarkState.value) "布局 Benchmark 已展开" else "布局 Benchmark 已收起",
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(DemoTestTags.LAYOUTS_BENCHMARK_TOGGLE),
                    onClick = { benchmarkState.value = !benchmarkState.value },
                )
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp),
                ) {
                    Text(text = "前导")
                    Button(
                        text = if (benchmarkState.value) {
                            "展开的 benchmark 标签，应该保持兄弟布局稳定"
                        } else {
                            "紧凑"
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        text = "重置",
                        variant = ButtonVariant.Outlined,
                        modifier = Modifier.testTag(DemoTestTags.LAYOUTS_BENCHMARK_RESET),
                        onClick = { benchmarkState.value = false },
                    )
                }
            }

            "row" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Row + Spacer + 交叉轴对齐",
                subtitle = "自定义线性布局支持 spacing、arrangement 和子级交叉轴覆盖。",
            ) {
                Row(
                    arrangement = MainAxisArrangement.Start,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .padding(12.dp),
                ) {
                    Text(
                        text = "顶部",
                        modifier = Modifier
                            .align(VerticalAlignment.Top)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                    FlexibleSpacer()
                    Text(
                        text = "底部",
                        modifier = Modifier
                            .align(VerticalAlignment.Bottom)
                            .backgroundColor(Theme.colors.secondary)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            "box" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "Box 叠加",
                subtitle = "默认对齐、子级覆盖、offset 和 zIndex 在同一容器中协同工作。",
            ) {
                Box(
                    contentAlignment = BoxAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .clickable { boxTapState.value = boxTapState.value + 1 }
                        .padding(12.dp),
                ) {
                    Text(
                        text = "居中内容 · 点击 ${boxTapState.value}",
                        modifier = Modifier
                            .backgroundColor(Theme.colors.primary)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                    Text(
                        text = "固定标签",
                        modifier = Modifier
                            .align(BoxAlignment.BottomEnd)
                            .offset(x = (-8).dp.toFloat(), y = (-8).dp.toFloat())
                            .zIndex(1f)
                            .backgroundColor(Theme.colors.secondary)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            "column" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Column 排列",
                subtitle = "主轴 arrangement 和 Divider 在自定义线性容器中的稳定性。",
            ) {
                Column(
                    arrangement = MainAxisArrangement.SpaceEvenly,
                    horizontalAlignment = HorizontalAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .padding(12.dp),
                ) {
                    Text(text = "一")
                    Divider()
                    Text(text = "二")
                    Divider()
                    Text(text = "三")
                }
            }

            "edge" -> ScenarioSection(
                kind = ScenarioKind.Stress,
                title = "布局边界用例",
                subtitle = "wrap、weight 和嵌套容器 sizing 的极端组合。",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Layouts -> 边界页",
                    stableTargets = listOf("长标签 / 短标签", "Weighted", "Action", "Wrap"),
                )
                Button(
                    text = if (useLongLabelsState.value) "使用短标签" else "使用长标签",
                    modifier = Modifier
                        .margin(bottom = 12.dp)
                        .testTag(DemoTestTags.LAYOUTS_EDGE_TOGGLE),
                    onClick = { useLongLabelsState.value = !useLongLabelsState.value },
                )
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp)
                        .margin(bottom = 12.dp),
                ) {
                    Surface(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            source = ImageSource.Resource(R.drawable.demo_media_icon),
                            contentDescription = "布局探测图标",
                            modifier = Modifier.testTag(DemoTestTags.LAYOUTS_EDGE_PROBE_ICON),
                        )
                    }
                    Button(
                        text = if (useLongLabelsState.value) {
                            "一个很长的 weighted 标签，应该在不破坏兄弟布局的情况下换行"
                        } else {
                            "Weighted"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.LAYOUTS_EDGE_WEIGHTED),
                    )
                    Button(
                        text = "操作",
                        variant = ButtonVariant.Outlined,
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.LAYOUTS_EDGE_ACTION),
                    )
                }
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp),
                ) {
                    Surface(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Wrap")
                    }
                    Surface(
                        variant = SurfaceVariant.Variant,
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(text = "仍然 Wrap")
                    }
                    Text(
                        text = "嵌套的 Surface 应该紧贴内容，把剩余宽度留给这段文本。",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            "flow" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "流式布局",
                subtitle = "FlowRow 实现标签云，FlowColumn 实现竖向流。支持 spacing 和 maxItemsInEachRow/Column。",
            ) {
                Text(
                    text = "FlowRow 标签云（${flowItemCountState.value} 个标签）",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                ) {
                    Button(
                        text = "增加标签",
                        size = ButtonSize.Compact,
                        onClick = { flowItemCountState.value = (flowItemCountState.value + 2).coerceAtMost(20) },
                    )
                    Button(
                        text = "减少标签",
                        size = ButtonSize.Compact,
                        variant = ButtonVariant.Outlined,
                        onClick = { flowItemCountState.value = (flowItemCountState.value - 2).coerceAtLeast(2) },
                    )
                }
                FlowRow(
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 16.dp)
                        .testTag(DemoTestTags.LAYOUTS_FLOW_ROW),
                ) {
                    (1..flowItemCountState.value).forEach { i ->
                        Surface(
                            variant = SurfaceVariant.Variant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(text = "标签 $i")
                        }
                    }
                }
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Text(
                    text = "FlowColumn 竖向流（maxItemsInEachColumn = 3）",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                FlowColumn(
                    horizontalSpacing = 12.dp,
                    verticalSpacing = 8.dp,
                    maxItemsInEachColumn = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                ) {
                    (1..9).forEach { i ->
                        Surface(
                            variant = SurfaceVariant.Default,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(text = "竖向项 $i")
                        }
                    }
                }
            }

            "scrollable" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "滚动容器",
                subtitle = "ScrollableColumn 和 ScrollableRow 提供可滚动的线性容器。",
            ) {
                Text(
                    text = "ScrollableColumn（超长内容可滚动）",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                ScrollableColumn(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp)
                        .testTag(DemoTestTags.LAYOUTS_SCROLLABLE_COLUMN),
                ) {
                    (1..15).forEach { i ->
                        Surface(
                            variant = SurfaceVariant.Default,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        ) {
                            Text(text = "滚动内容行 $i")
                        }
                    }
                    Text(
                        text = "更多内容在下方 ↓",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
                Divider(modifier = Modifier.margin(vertical = 12.dp))
                Text(
                    text = "ScrollableRow（横向可滚动标签行）",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                ScrollableRow(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(8.dp)
                        .testTag(DemoTestTags.LAYOUTS_SCROLLABLE_ROW),
                ) {
                    (1..20).forEach { i ->
                        Surface(
                            variant = SurfaceVariant.Default,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(text = "横向标签 $i")
                        }
                    }
                }
            }

            else -> VerificationNotesSection(
                what = "布局组件应验证线性容器、Box 叠加、流式布局和滚动容器的稳定性。",
                howToVerify = listOf(
                    "反复点击 Box 区域，确认点击态和固定标签不会错位。",
                    "在不同宽度设备上观察 Row 中顶部/底部对齐文本。",
                    "切换长短标签，确认 weighted button 与嵌套 surface 布局稳定。",
                    "增减 FlowRow 标签数量，确认自动换行正确。",
                    "上下滑动 ScrollableColumn，确认滚动流畅。",
                    "左右滑动 ScrollableRow，确认横向滚动正常。",
                ),
                expected = listOf(
                    "线性容器默认子项不会意外扩展成整行。",
                    "FlowRow/FlowColumn 自动换行/换列，spacing 均匀。",
                    "ScrollableColumn/ScrollableRow 滚动流畅无卡顿。",
                ),
            )
        }
    }
}
