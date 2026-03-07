package com.viewcompose

import com.viewcompose.renderer.layout.VerticalAlignment
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.backgroundColor
import com.viewcompose.renderer.modifier.cornerRadius
import com.viewcompose.renderer.modifier.fillMaxWidth
import com.viewcompose.renderer.modifier.height
import com.viewcompose.renderer.modifier.padding
import com.viewcompose.renderer.modifier.testTag
import com.viewcompose.renderer.node.ImageSource
import com.viewcompose.widget.core.Box
import com.viewcompose.widget.core.Button
import com.viewcompose.widget.core.ButtonColorOverride
import com.viewcompose.widget.core.ButtonSize
import com.viewcompose.widget.core.ButtonVariant
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.ProvideButtonColors
import com.viewcompose.widget.core.ProvideSegmentedControlColors
import com.viewcompose.widget.core.ProvideTextFieldColors
import com.viewcompose.widget.core.Row
import com.viewcompose.widget.core.SegmentedControl
import com.viewcompose.widget.core.SegmentedControlColorOverride
import com.viewcompose.widget.core.SurfaceDefaults
import com.viewcompose.widget.core.Text
import com.viewcompose.widget.core.TextDefaults
import com.viewcompose.widget.core.TextField
import com.viewcompose.widget.core.TextFieldColorOverride
import com.viewcompose.widget.core.TextFieldVariant
import com.viewcompose.widget.core.Theme
import com.viewcompose.widget.core.UiTextStyle
import com.viewcompose.widget.core.UiThemeOverride
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.dp
import com.viewcompose.widget.core.sp

internal fun UiTreeBuilder.FoundationsIntroSection() {
    ScenarioSection(
        kind = ScenarioKind.Guide,
        title = "Demo 架构说明",
        subtitle = "示例按类别拆分，让运行时界面更易于阅读和扩展。",
    ) {
        Text(text = "每个标签页隔离一个关注点：布局、文本/输入、状态和集合级渲染。")
        Text(
            text = "分页器本身也通过框架渲染为映射的虚拟控件。",
            color = TextDefaults.secondaryColor(),
        )
    }
}

internal fun UiTreeBuilder.FoundationsBenchmarkSection(
    enabled: Boolean,
    onToggle: () -> Unit,
    onReset: () -> Unit,
) {
    ScenarioSection(
        kind = ScenarioKind.Benchmark,
        title = "基础组件 Benchmark 锚点",
        subtitle = "停留在默认页面，使用简短稳定的标签，便于宏 benchmark 进入。",
    ) {
        Button(
            text = if (enabled) "Benchmark 已开启" else "Benchmark 已关闭",
            modifier = Modifier
                .fillMaxWidth()
                .testTag(DemoTestTags.FOUNDATIONS_BENCHMARK_TOGGLE),
            onClick = onToggle,
        )
        Button(
            text = "重置 Benchmark",
            variant = ButtonVariant.Outlined,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(DemoTestTags.FOUNDATIONS_BENCHMARK_RESET),
            onClick = onReset,
        )
        BenchmarkRouteCallout(
            route = "Launcher -> Foundations -> Benchmark Anchor",
            stableTargets = listOf("Benchmark On / Off", "Reset"),
        )
    }
}

internal fun UiTreeBuilder.FoundationsThemeSection() {
    ScenarioSection(
        kind = ScenarioKind.Core,
        title = "主题预览",
        subtitle = "显示当前 demo 主题 token，包括分页器调色板。",
    ) {
        ThemeSwatchRow(
            "核心色",
            listOf(
                ThemeSwatch("Bg", Theme.colors.background),
                ThemeSwatch("Surface", Theme.colors.surface),
                ThemeSwatch("Primary", Theme.colors.primary),
                ThemeSwatch("Secondary", Theme.colors.secondary),
            ),
        )
        ThemeSwatchRow(
            "输入色",
            listOf(
                ThemeSwatch("Field", Theme.colors.surface),
                ThemeSwatch("Control", Theme.colors.primary),
                ThemeSwatch("Error", 0xFFB3261E.toInt()),
                ThemeSwatch("Pressed", 0x22000000 or (Theme.colors.textPrimary and 0x00FFFFFF)),
            ),
        )
        Text(
            text = "Shapes: card=${Theme.shapes.cardCornerRadius}px, interactive=${Theme.shapes.interactiveCornerRadius}px",
            style = UiTextStyle(fontSizeSp = 13.sp),
            color = TextDefaults.secondaryColor(),
        )
    }
}

internal fun UiTreeBuilder.FoundationsOverridesSection() {
    ScenarioSection(
        kind = ScenarioKind.Core,
        title = "作用域主题覆盖",
        subtitle = "每个区块仅覆盖一个 token 域的局部子树。",
    ) {
        UiThemeOverride(
            colors = {
                copy(primary = secondary, surfaceVariant = primary)
            },
        ) {
            Column(
                spacing = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                    .cornerRadius(SurfaceDefaults.cardCornerRadius())
                    .padding(12.dp),
            ) {
                Text(text = "颜色覆盖")
                ThemeSwatchRow(
                    label = "局部颜色",
                    swatches = listOf(
                        ThemeSwatch("Primary", Theme.colors.primary),
                        ThemeSwatch("Surface", Theme.colors.surfaceVariant),
                    ),
                )
                Button(
                    text = "Accent 作为 Primary",
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(DemoTestTags.FOUNDATIONS_ACCENT_PRIMARY),
                )
            }
        }
        UiThemeOverride(
            shapes = {
                copy(cardCornerRadius = 32.dp, interactiveCornerRadius = 24.dp)
            },
        ) {
            Column(
                spacing = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .backgroundColor(SurfaceDefaults.backgroundColor())
                    .cornerRadius(SurfaceDefaults.cardCornerRadius())
                    .padding(16.dp),
            ) {
                Text(text = "形状覆盖")
                Text(
                    text = "局部 card=${Theme.shapes.cardCornerRadius}px, interactive=${Theme.shapes.interactiveCornerRadius}px",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .cornerRadius(SurfaceDefaults.cardCornerRadius()),
                    ) {}
                    Button(
                        text = "圆角按钮",
                        variant = ButtonVariant.Tonal,
                        size = ButtonSize.Large,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Column(
            spacing = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(SurfaceDefaults.backgroundColor())
                .cornerRadius(SurfaceDefaults.cardCornerRadius())
                .padding(12.dp),
        ) {
            Text(text = "按压覆盖层（派生）")
            ThemeSwatchRow(
                label = "按压覆盖",
                swatches = listOf(
                    ThemeSwatch("Base", 0x22000000 or (Theme.colors.textPrimary and 0x00FFFFFF)),
                    ThemeSwatch("Primary", Theme.colors.primary),
                ),
            )
            Row(
                spacing = 8.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(text = "按压我", variant = ButtonVariant.Primary, modifier = Modifier.weight(1f))
                Button(text = "Outlined", variant = ButtonVariant.Outlined, modifier = Modifier.weight(1f))
            }
        }
        ProvideButtonColors(
            ButtonColorOverride(
                primaryContainer = Theme.colors.textPrimary,
                primaryContent = Theme.colors.background,
                primaryDisabledContainer = Theme.colors.divider,
                primaryDisabledContent = Theme.colors.textSecondary,
                outlinedBorder = Theme.colors.secondary,
                outlinedDisabledBorder = Theme.colors.textSecondary,
            ),
        ) {
            ProvideTextFieldColors(
                TextFieldColorOverride(
                    filledDisabledContainer = Theme.colors.surfaceVariant,
                    outlinedErrorBorder = Theme.colors.secondary,
                ),
            ) {
                ProvideSegmentedControlColors(
                    SegmentedControlColorOverride(
                        indicator = Theme.colors.secondary,
                        indicatorDisabled = Theme.colors.divider,
                        selectedText = Theme.colors.background,
                        selectedTextDisabled = Theme.colors.textSecondary,
                    ),
                ) {
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .cornerRadius(SurfaceDefaults.cardCornerRadius())
                            .padding(12.dp),
                    ) {
                        Text(text = "组件默认值覆盖")
                        Text(
                            text = "此区块修改按钮和分段控件的默认值，不改变基础调色板。",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            color = TextDefaults.secondaryColor(),
                        )
                        SegmentedControl(
                            items = listOf("Alpha", "Beta", "Gamma"),
                            selectedIndex = 1,
                            onSelectionChange = {},
                            modifier = Modifier.fillMaxWidth(),
                        )
                        SegmentedControl(
                            items = listOf("禁用", "状态"),
                            selectedIndex = 0,
                            enabled = false,
                            onSelectionChange = {},
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(spacing = 8.dp, modifier = Modifier.fillMaxWidth()) {
                            Button(
                                text = "Primary Token",
                                leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag(DemoTestTags.FOUNDATIONS_PRIMARY_TOKEN),
                            )
                            Button(
                                text = "Outlined Token",
                                trailingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                                variant = ButtonVariant.Outlined,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(spacing = 8.dp, modifier = Modifier.fillMaxWidth()) {
                            Button(text = "禁用 Primary", enabled = false, modifier = Modifier.weight(1f))
                            Button(text = "禁用 Outline", variant = ButtonVariant.Outlined, enabled = false, modifier = Modifier.weight(1f))
                        }
                        TextField(
                            value = "error@token.dev",
                            onValueChange = {},
                            variant = TextFieldVariant.Outlined,
                            isError = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        Text(
            text = "在这些区块之外，父级 demo 主题保持不变。",
            style = UiTextStyle(fontSizeSp = 13.sp),
            color = TextDefaults.secondaryColor(),
        )
    }
}
