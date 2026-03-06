package com.gzq.uiframework

import android.graphics.Typeface
import android.widget.TextView
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.alpha
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.border
import com.gzq.uiframework.renderer.modifier.clip
import com.gzq.uiframework.renderer.modifier.contentDescription
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.elevation
import com.gzq.uiframework.renderer.modifier.fillMaxHeight
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.minHeight
import com.gzq.uiframework.renderer.modifier.minWidth
import com.gzq.uiframework.renderer.modifier.nativeView
import com.gzq.uiframework.renderer.modifier.offset
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.rippleColor
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.modifier.width
import com.gzq.uiframework.renderer.modifier.zIndex
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.Surface
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.SurfaceVariant
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.ModifiersPage(
    initialPageIndex: Int = 0,
) {
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 2)) }

    val pageItems = when (selectedPageState.value) {
        0 -> listOf("page", "page_filter", "elevation", "border_clip", "alpha_ripple", "corner", "verify")
        1 -> listOf("page", "page_filter", "size_constraints", "verify")
        else -> listOf("page", "page_filter", "accessibility", "native_view", "offset_zindex", "verify")
    }

    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Modifier 展示",
                goal = "覆盖全部 Modifier 函数：视觉效果、尺寸约束和辅助功能。",
                modules = listOf("elevation", "border", "clip", "alpha", "rippleColor", "cornerRadius", "minWidth", "minHeight", "fillMaxHeight", "contentDescription", "nativeView", "offset", "zIndex"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("视觉", "尺寸", "辅助"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "elevation" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "elevation 高程对比",
                subtitle = "不同 elevation 值的阴影效果对比。",
            ) {
                Row(
                    spacing = 12.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    listOf(0, 4, 8, 16).forEach { elev ->
                        Box(
                            contentAlignment = BoxAlignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                                .backgroundColor(SurfaceDefaults.backgroundColor())
                                .cornerRadius(12.dp)
                                .elevation(elev.dp),
                        ) {
                            Text(
                                text = "${elev}dp",
                                style = UiTextStyle(fontSizeSp = 13.sp),
                            )
                        }
                    }
                }
            }

            "border_clip" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "border 边框 + clip 裁切",
                subtitle = "border 设置边框宽度和颜色，clip 裁切溢出内容。",
            ) {
                Row(
                    spacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .border(1.dp, Theme.colors.divider)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "1dp 边框")
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .border(2.dp, Theme.colors.primary)
                            .cornerRadius(12.dp),
                    ) {
                        Text(text = "2dp 主色")
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .border(3.dp, Theme.colors.accent)
                            .cornerRadius(24.dp),
                    ) {
                        Text(text = "3dp 圆角")
                    }
                }
                Text(
                    text = "clip 裁切溢出内容",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .cornerRadius(16.dp)
                        .clip()
                        .backgroundColor(Theme.colors.surfaceVariant),
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp, 200.dp)
                            .backgroundColor(Theme.colors.primary)
                            .offset(x = (-20).dp.toFloat(), y = (-20).dp.toFloat()),
                    ) {}
                    Text(
                        text = "溢出的蓝色方块被裁切",
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }

            "alpha_ripple" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "alpha 透明度 + rippleColor 按压颜色",
                subtitle = "alpha 控制 View 透明度，rippleColor 自定义按压反馈颜色。",
            ) {
                Text(
                    text = "透明度梯度",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Row(
                    spacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 16.dp),
                ) {
                    listOf(1.0f, 0.5f, 0.3f, 0.0f).forEach { a ->
                        Box(
                            contentAlignment = BoxAlignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .backgroundColor(Theme.colors.primary)
                                .cornerRadius(8.dp)
                                .alpha(a),
                        ) {
                            Text(
                                text = "${(a * 100).toInt()}%",
                                style = UiTextStyle(fontSizeSp = 13.sp),
                            )
                        }
                    }
                }
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Text(
                    text = "自定义按压颜色（点击查看效果）",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Row(
                    spacing = 12.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        text = "红色波纹",
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .rippleColor(0xFFFF0000.toInt()),
                    )
                    Button(
                        text = "绿色波纹",
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .rippleColor(0xFF00FF00.toInt()),
                    )
                    Button(
                        text = "蓝色波纹",
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .rippleColor(0xFF0000FF.toInt()),
                    )
                }
            }

            "corner" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "cornerRadius 多级圆角",
                subtitle = "cornerRadius 支持统一、上下分组和四角独立设置。",
            ) {
                Row(
                    spacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(0.dp),
                    ) {
                        Text(text = "0dp", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "8dp", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(24.dp),
                    ) {
                        Text(text = "24dp", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                }
                Text(
                    text = "上下分组: top=16, bottom=0",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Box(
                    contentAlignment = BoxAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .backgroundColor(Theme.colors.surfaceVariant)
                        .cornerRadius(top = 16.dp, bottom = 0.dp)
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "顶部圆角")
                }
                Text(
                    text = "四角独立: topStart=24, topEnd=0, bottomEnd=24, bottomStart=0",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Box(
                    contentAlignment = BoxAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .backgroundColor(Theme.colors.surfaceVariant)
                        .cornerRadius(topStart = 24.dp, topEnd = 0.dp, bottomEnd = 24.dp, bottomStart = 0.dp),
                ) {
                    Text(text = "对角圆角")
                }
            }

            "size_constraints" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "尺寸约束",
                subtitle = "minWidth、minHeight、fillMaxHeight 和 size/width/height 的效果。",
            ) {
                Text(
                    text = "minWidth 最小宽度约束",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .minWidth(100.dp)
                            .height(40.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "min 100dp", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .minWidth(60.dp)
                            .height(40.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "min 60dp", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                }
                Text(
                    text = "minHeight 最小高度约束",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .minHeight(80.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "min 80dp 高", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .minHeight(40.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "min 40dp 高", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                }
                Text(
                    text = "fillMaxHeight 单独使用",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                ) {
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                            .backgroundColor(Theme.colors.primary)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "满高", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .width(80.dp)
                            .height(60.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "60dp", style = UiTextStyle(fontSizeSp = 12.sp))
                    }
                    Text(
                        text = "左侧 fillMaxHeight 撑满父容器高度，右侧固定 60dp。",
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                    )
                }
                Divider(modifier = Modifier.margin(vertical = 12.dp))
                Text(
                    text = "padding + margin 3 级级联",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(0x220000FF)
                        .padding(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(0x2200FF00)
                            .margin(8.dp)
                            .padding(12.dp),
                    ) {
                        Box(
                            contentAlignment = BoxAlignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .backgroundColor(0x22FF0000)
                                .margin(4.dp)
                                .padding(8.dp),
                        ) {
                            Text(text = "最内层内容")
                        }
                    }
                }
                Text(
                    text = "蓝色=外层 padding 16, 绿色=中层 margin 8 + padding 12, 红色=内层 margin 4 + padding 8",
                    style = UiTextStyle(fontSizeSp = 12.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 4.dp),
                )
            }

            "accessibility" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "contentDescription 无障碍描述",
                subtitle = "contentDescription 为 View 设置无障碍描述，可被 TalkBack 读取。",
            ) {
                Row(
                    spacing = 12.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp)
                            .contentDescription("这是一个示例区块，用于展示无障碍描述"),
                    ) {
                        Text(text = "有无障碍描述")
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp),
                    ) {
                        Text(text = "无描述")
                    }
                }
                Text(
                    text = "左侧 Box 设置了 contentDescription，可被 TalkBack 朗读。开启 TalkBack 验证。",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 8.dp),
                )
            }

            "native_view" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "nativeView 原生属性逃生通道",
                subtitle = "nativeView 直接配置原生 Android View 属性，突破框架 Modifier 覆盖范围。",
            ) {
                Box(
                    contentAlignment = BoxAlignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .backgroundColor(Theme.colors.surfaceVariant)
                        .cornerRadius(8.dp)
                        .nativeView("bold_text") { view ->
                            if (view is TextView) {
                                view.typeface = Typeface.DEFAULT_BOLD
                                view.letterSpacing = 0.1f
                            }
                        },
                ) {
                    Text(text = "nativeView 设置粗体+字间距")
                }
                Text(
                    text = "通过 nativeView 直接修改 TextView 的 typeface 和 letterSpacing。",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 8.dp),
                )
            }

            "offset_zindex" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "offset + zIndex",
                subtitle = "offset 平移 View 位置，zIndex 控制绘制顺序。",
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(12.dp)
                        .padding(12.dp),
                ) {
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .size(80.dp, 60.dp)
                            .backgroundColor(Theme.colors.primary)
                            .cornerRadius(8.dp)
                            .zIndex(1f),
                    ) {
                        Text(text = "zIndex=1")
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .size(80.dp, 60.dp)
                            .backgroundColor(Theme.colors.accent)
                            .cornerRadius(8.dp)
                            .offset(x = 40.dp.toFloat(), y = 20.dp.toFloat())
                            .zIndex(2f),
                    ) {
                        Text(text = "zIndex=2")
                    }
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier
                            .size(80.dp, 60.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(8.dp)
                            .offset(x = 80.dp.toFloat(), y = 40.dp.toFloat())
                            .zIndex(0f),
                    ) {
                        Text(text = "zIndex=0")
                    }
                }
                Text(
                    text = "三个方块分别设置不同的 offset 和 zIndex，高 zIndex 的方块绘制在上方。",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(top = 8.dp),
                )
            }

            else -> VerificationNotesSection(
                what = "Modifier 展示应覆盖全部未展示的 Modifier 函数的视觉效果和行为。",
                howToVerify = listOf(
                    "观察 elevation 对比行，确认阴影随值增大而明显。",
                    "确认 border 边框宽度和颜色正确显示。",
                    "确认 clip 裁切掉了溢出的蓝色方块。",
                    "确认 alpha 透明度梯度从 100% 到 0% 递减。",
                    "点击带颜色波纹的按钮，确认按压颜色正确。",
                    "确认 minWidth/minHeight 约束生效。",
                    "确认 fillMaxHeight 撑满父容器高度。",
                    "开启 TalkBack 验证 contentDescription。",
                    "确认 nativeView 的粗体效果。",
                    "确认 offset + zIndex 的叠放顺序正确。",
                ),
                expected = listOf(
                    "所有 Modifier 效果在亮色/暗色主题下均可见。",
                    "cornerRadius 三级（统一/上下/四角）均正确渲染。",
                    "padding/margin 三级级联视觉层次清晰。",
                ),
            )
        }
    }
}
