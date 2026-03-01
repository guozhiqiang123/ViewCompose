package com.gzq.uiframework

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.align
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.offset
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.renderer.modifier.zIndex
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.FlexibleSpacer
import com.gzq.uiframework.widget.core.Icon
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.Surface
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.SurfaceVariant
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.renderer.node.ImageSource

internal fun UiTreeBuilder.LayoutPage() {
    val boxTapState = remember { mutableStateOf(0) }
    val useLongLabelsState = remember { mutableStateOf(false) }
    val selectedPageState = remember { mutableStateOf(0) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("page", "page_filter", "row", "column", "verify")
        1 -> listOf("page", "page_filter", "box", "verify")
        2 -> listOf("page", "page_filter", "edge", "verify")
        else -> listOf("page", "page_filter", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Layouts",
                goal = "Stress the custom linear and box containers so measurement, placement, spacing, and child overrides stay predictable.",
                modules = listOf("DeclarativeLinearLayout", "DeclarativeBoxLayout", "layout defaults", "modifiers"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Linear", "Overlay", "Edges", "Checklist"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "row" -> DemoSection(
                title = "Row + Spacer + Cross Axis Alignment",
                subtitle = "Custom linear layout now supports spacing, arrangement, and child-level cross-axis override.",
            ) {
                Row(
                    arrangement = MainAxisArrangement.Start,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Top",
                        modifier = Modifier.Empty
                            .align(VerticalAlignment.Top)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                    FlexibleSpacer()
                    Text(
                        text = "Bottom",
                        modifier = Modifier.Empty
                            .align(VerticalAlignment.Bottom)
                            .backgroundColor(Theme.colors.accent)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            "box" -> DemoSection(
                title = "Box Overlay",
                subtitle = "Default alignment, child override, offset, and zIndex work together in a single container.",
            ) {
                Box(
                    contentAlignment = BoxAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(140.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .clickable {
                            boxTapState.value = boxTapState.value + 1
                        }
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Centered surface · taps ${boxTapState.value}",
                        modifier = Modifier.Empty
                            .backgroundColor(Theme.colors.primary)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                    Text(
                        text = "Pinned tag",
                        modifier = Modifier.Empty
                            .align(BoxAlignment.BottomEnd)
                            .offset(x = (-8).dp.toFloat(), y = (-8).dp.toFloat())
                            .zIndex(1f)
                            .backgroundColor(Theme.colors.accent)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            "column" -> DemoSection(
                title = "Column Arrangement",
                subtitle = "Main-axis arrangement and dividers are now stable inside the custom linear container.",
            ) {
                Column(
                    arrangement = MainAxisArrangement.SpaceEvenly,
                    horizontalAlignment = HorizontalAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(180.dp)
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .padding(12.dp),
                ) {
                    Text(text = "One")
                    Divider()
                    Text(text = "Two")
                    Divider()
                    Text(text = "Three")
                }
            }

            "edge" -> DemoSection(
                title = "Layout Edge Cases",
                subtitle = "This page compresses the combinations that previously exposed wrong defaults for wrap, weight, and nested container sizing.",
            ) {
                Button(
                    text = if (useLongLabelsState.value) "Use Short Labels" else "Use Long Labels",
                    modifier = Modifier.Empty.margin(bottom = 12.dp),
                    onClick = {
                        useLongLabelsState.value = !useLongLabelsState.value
                    },
                )
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp)
                        .margin(bottom = 12.dp),
                ) {
                    Surface(
                        modifier = Modifier.Empty.padding(8.dp),
                    ) {
                        Icon(
                            source = ImageSource.Resource(R.drawable.demo_media_icon),
                            contentDescription = "Layout probe icon",
                        )
                    }
                    Button(
                        text = if (useLongLabelsState.value) {
                            "A very long weighted label that should wrap without breaking sibling layout"
                        } else {
                            "Weighted"
                        },
                        modifier = Modifier.Empty.weight(1f),
                    )
                    Button(
                        text = "Action",
                        variant = ButtonVariant.Outlined,
                        modifier = Modifier.Empty.weight(1f),
                    )
                }
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp),
                ) {
                    Surface(
                        modifier = Modifier.Empty.padding(8.dp),
                    ) {
                        Text(text = "Wrap")
                    }
                    Surface(
                        variant = SurfaceVariant.Variant,
                        modifier = Modifier.Empty.padding(8.dp),
                    ) {
                        Text(text = "Still Wrap")
                    }
                    Text(
                        text = "Nested surfaces should hug content and leave the remaining width to this text block.",
                        modifier = Modifier.Empty.weight(1f),
                    )
                }
            }

            else -> VerificationNotesSection(
                what = "Layouts should expose measurement bugs quickly, especially around wrap content defaults, fill, weight, and child alignment overrides.",
                howToVerify = listOf(
                    "反复点击 Box 区域，确认点击态、overlay 和 pinned tag 不会错位。",
                    "在不同宽度设备上观察 Row 中 top/bottom 对齐文本，确认不会被 Stretch 或异常留白撑开。",
                    "切换 Edge Cases 里的长短标签，确认 weighted button 与嵌套 surface 不会把兄弟节点挤没或撑出空白。",
                    "检查 Column 的 SpaceEvenly 摆放，确认 divider 和文本间距稳定。",
                ),
                expected = listOf(
                    "线性容器默认子项不会意外扩展成整行。",
                    "Box 的 align / offset / zIndex 组合稳定。",
                    "Spacing、alignment 和 child override 不会互相覆盖成异常布局。",
                ),
                relatedGaps = listOf(
                    "还没有自定义布局协议和布局调试可视化。",
                ),
            )
        }
    }
}
