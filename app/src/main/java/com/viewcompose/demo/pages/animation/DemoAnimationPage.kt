package com.viewcompose

import com.viewcompose.animation.AnimatedContent
import com.viewcompose.animation.AnimatedVisibility
import com.viewcompose.animation.animateFloatAsState
import com.viewcompose.animation.spring
import com.viewcompose.animation.tween
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.fillMaxSize
import com.viewcompose.ui.modifier.fillMaxWidth
import com.viewcompose.ui.modifier.graphicsLayer
import com.viewcompose.ui.modifier.height
import com.viewcompose.ui.modifier.lazyContainerMotion
import com.viewcompose.ui.modifier.margin
import com.viewcompose.ui.modifier.padding
import com.viewcompose.ui.modifier.testTag
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.Button
import com.viewcompose.widget.core.ButtonVariant
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.LazyColumn
import com.viewcompose.widget.core.Row
import com.viewcompose.widget.core.Surface
import com.viewcompose.widget.core.SurfaceVariant
import com.viewcompose.widget.core.Text
import com.viewcompose.widget.core.TextDefaults
import com.viewcompose.widget.core.UiTextStyle
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.dp
import com.viewcompose.widget.core.remember
import com.viewcompose.widget.core.sp

internal fun UiTreeBuilder.AnimationPage(
    initialPageIndex: Int = 0,
) {
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 2)) }
    val visibleState = remember { mutableStateOf(true) }
    val contentState = remember { mutableStateOf(false) }
    val pulseState = remember { mutableStateOf(false) }
    val listItemsState = remember { mutableStateOf(listOf("Item A", "Item B", "Item C")) }
    val listSeedState = remember { mutableStateOf(0) }

    val sections = when (selectedPageState.value) {
        0 -> listOf("page", "filter", "core", "verify")
        1 -> listOf("page", "filter", "transition", "verify")
        else -> listOf("page", "filter", "list", "verify")
    }

    LazyColumn(
        items = sections,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Animation",
                goal = "验证状态驱动动画、内容过渡和列表位移动画在 ViewCompose 中可用且默认行为可控。",
                modules = listOf("viewcompose-animation", "graphicsLayer", "LazyContainer motion policy"),
            )

            "filter" -> ChapterPageFilterSection(
                pages = listOf("Core", "Transition", "List Motion"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "core" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "基础属性动画",
                subtitle = "animateFloatAsState + AnimatedVisibility 组合，验证显隐与缩放状态更新。",
            ) {
                val scale = animateFloatAsState(
                    targetValue = if (pulseState.value) 1.08f else 0.92f,
                    animationSpec = spring(),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                ) {
                    Button(
                        text = if (visibleState.value) "隐藏块" else "显示块",
                        onClick = { visibleState.value = !visibleState.value },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.ANIMATION_VISIBILITY_TOGGLE),
                    )
                    Button(
                        text = if (pulseState.value) "缩放 0.92x" else "缩放 1.08x",
                        variant = ButtonVariant.Outlined,
                        onClick = { pulseState.value = !pulseState.value },
                        modifier = Modifier.weight(1f),
                    )
                }
                AnimatedVisibility(
                    visible = visibleState.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(DemoTestTags.ANIMATION_VISIBILITY_TARGET),
                ) {
                    Surface(
                        variant = SurfaceVariant.Variant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(
                                scaleX = scale.value,
                                scaleY = scale.value,
                            )
                            .padding(12.dp),
                    ) {
                        Text(text = "Animation Core Surface")
                    }
                }
                Text(
                    text = "Visibility footer anchor",
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier
                        .margin(top = 6.dp)
                        .testTag(DemoTestTags.ANIMATION_VISIBILITY_FOOTER),
                )
            }

            "transition" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "内容过渡动画",
                subtitle = "AnimatedContent 在同一容器中切换文案，验证内容切换过程无闪烁。",
            ) {
                Button(
                    text = if (contentState.value) "切到主文案" else "切到替代文案",
                    onClick = { contentState.value = !contentState.value },
                    modifier = Modifier
                        .margin(bottom = 8.dp)
                        .testTag(DemoTestTags.ANIMATION_CONTENT_TOGGLE),
                )
                AnimatedContent(
                    targetState = contentState.value,
                    transitionSpec = { tween(260) },
                    modifier = Modifier.fillMaxWidth(),
                ) { alt ->
                    Surface(
                        variant = SurfaceVariant.Variant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Text(
                            text = if (alt) {
                                "替代文案：内容过渡已生效"
                            } else {
                                "主文案：内容过渡待切换"
                            },
                            modifier = Modifier.testTag(DemoTestTags.ANIMATION_CONTENT_LABEL),
                        )
                    }
                }
            }

            "list" -> ScenarioSection(
                kind = ScenarioKind.Stress,
                title = "列表位移动画",
                subtitle = "LazyColumn 通过 lazyContainerMotion 启用 item add/move/change 的动画策略。",
            ) {
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp),
                ) {
                    Button(
                        text = "头部插入",
                        onClick = {
                            val nextSeed = listSeedState.value + 1
                            listSeedState.value = nextSeed
                            listItemsState.value = listOf("New $nextSeed") + listItemsState.value
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.ANIMATION_LIST_ADD),
                    )
                    Button(
                        text = "顺序轮换",
                        variant = ButtonVariant.Outlined,
                        onClick = {
                            val current = listItemsState.value
                            if (current.size > 1) {
                                listItemsState.value = current.drop(1) + current.first()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(DemoTestTags.ANIMATION_LIST_REORDER),
                    )
                }
                LazyColumn(
                    items = listItemsState.value,
                    key = { it },
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .lazyContainerMotion(
                            animateInsert = true,
                            animateRemove = true,
                            animateMove = true,
                            animateChange = true,
                        ),
                ) { item ->
                    Surface(
                        variant = SurfaceVariant.Variant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                    ) {
                        Text(
                            text = item,
                            style = UiTextStyle(fontSizeSp = 14.sp),
                            modifier = if (item == listItemsState.value.firstOrNull()) {
                                Modifier.testTag(DemoTestTags.ANIMATION_LIST_FIRST)
                            } else {
                                Modifier
                            },
                        )
                    }
                }
                Text(
                    text = "First item: ${listItemsState.value.firstOrNull().orEmpty()}",
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 8.dp),
                )
            }

            else -> VerificationNotesSection(
                what = "Animation 页覆盖了属性动画、内容过渡和集合位移动画三条主链路。",
                howToVerify = listOf(
                    "Core 页切换显隐与缩放，确认块体出现/消失平滑。",
                    "Transition 页切换文案，确认内容过渡没有闪烁。",
                    "List Motion 页执行头部插入/顺序轮换，确认列表更新仍保持可交互。",
                ),
                expected = listOf(
                    "动画 API 在状态更新后持续稳定触发。",
                    "graphicsLayer 与动画值组合不会破坏布局。",
                    "列表 motion 默认 opt-in，不影响未启用容器。",
                ),
            )
        }
    }
}
