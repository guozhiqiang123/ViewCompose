package com.gzq.uiframework

import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonColorOverride
import com.gzq.uiframework.widget.core.ButtonSize
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.CircularProgressIndicator
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Icon
import com.gzq.uiframework.widget.core.IconButton
import com.gzq.uiframework.widget.core.Image
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.LinearProgressIndicator
import com.gzq.uiframework.widget.core.ProvideButtonColors
import com.gzq.uiframework.widget.core.ProvideSegmentedControlColors
import com.gzq.uiframework.widget.core.ProvideTextFieldColors
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SegmentedControl
import com.gzq.uiframework.widget.core.SegmentedControlColorOverride
import com.gzq.uiframework.widget.core.Surface
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.SurfaceVariant
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.TextField
import com.gzq.uiframework.widget.core.TextFieldColorOverride
import com.gzq.uiframework.widget.core.TextFieldVariant
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiThemeOverride
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource

internal fun UiTreeBuilder.OverviewPage(
    initialPageIndex: Int = 0,
    onOpenCapability: (Class<out androidx.appcompat.app.AppCompatActivity>) -> Unit,
) {
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 2)) }
    val benchmarkState = remember { mutableStateOf(false) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "intro", "jump", "surface", "verify")
        1 -> listOf("overrides", "page", "page_filter", "theme", "verify")
        else -> listOf("page", "page_filter", "progress", "media", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Foundations",
                goal = "Verify the core visual primitives, theme scopes, component defaults, and media widgets before moving into more advanced runtime scenarios.",
                modules = listOf("ui-widget-core", "ui-renderer", "theme locals", "component defaults"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Guide", "Theme", "Media"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "intro" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "Why This Demo Changed",
                subtitle = "The sample is now split by category so the runtime surface is easier to read and extend.",
            ) {
                Text(
                    text = "Each tab isolates one concern: layout, text/input, state, and collection-level rendering.",
                )
                Text(
                    text = "The pager itself is also rendered through the framework as a mapped virtual control.",
                    color = TextDefaults.secondaryColor(),
                )
            }

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "Foundations Benchmark Anchor",
                subtitle = "This block stays on the default page and uses short, stable labels so macrobenchmark can enter Foundations without segmented-page drift.",
            ) {
                Button(
                    text = if (benchmarkState.value) {
                        "Foundations Benchmark On"
                    } else {
                        "Foundations Benchmark Off"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        benchmarkState.value = !benchmarkState.value
                    },
                )
                Button(
                    text = "Reset Foundations Benchmark",
                    variant = ButtonVariant.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        benchmarkState.value = false
                    },
                )
                BenchmarkRouteCallout(
                    route = "Launcher -> MainActivity(extra=foundations) -> Foundations -> Foundations Benchmark Anchor",
                    stableTargets = listOf(
                        "Foundations Benchmark Off / Foundations Benchmark On",
                        "Reset Foundations Benchmark",
                    ),
                )
            }

            "theme" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Theme Preview",
                subtitle = "This block shows the active demo theme tokens, including the tab pager palette.",
            ) {
                ThemeSwatchRow(
                    "Core",
                    listOf(
                        ThemeSwatch("Bg", Theme.colors.background),
                        ThemeSwatch("Surface", Theme.colors.surface),
                        ThemeSwatch("Primary", Theme.colors.primary),
                        ThemeSwatch("Accent", Theme.colors.accent),
                    ),
                )
                ThemeSwatchRow(
                    "Input",
                    listOf(
                        ThemeSwatch("Field", Theme.input.fieldContainer),
                        ThemeSwatch("Control", Theme.input.control),
                        ThemeSwatch("Error", Theme.input.fieldError),
                        ThemeSwatch("Pressed", Theme.interactions.pressedOverlay),
                    ),
                )
                Text(
                    text = "Shapes: card=${Theme.shapes.cardCornerRadius}px, control=${Theme.shapes.controlCornerRadius}px",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "overrides" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Scoped Theme Overrides",
                subtitle = "Each block below overrides only one token domain for its local subtree.",
            ) {
                UiThemeOverride(
                    colors = {
                        copy(
                            primary = accent,
                            surfaceVariant = primary,
                        )
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
                        Text(text = "Color Override")
                        ThemeSwatchRow(
                            label = "Local Colors",
                            swatches = listOf(
                                ThemeSwatch("Primary", Theme.colors.primary),
                                ThemeSwatch("Surface", Theme.colors.surfaceVariant),
                            ),
                        )
                        Button(
                            text = "Accent As Primary",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                UiThemeOverride(
                    shapes = {
                        copy(
                            cardCornerRadius = 32.dp,
                            controlCornerRadius = 24.dp,
                        )
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
                        Text(text = "Shape Override")
                        Text(
                            text = "Local card=${Theme.shapes.cardCornerRadius}px, control=${Theme.shapes.controlCornerRadius}px",
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
                                text = "Rounded Action",
                                variant = ButtonVariant.Tonal,
                                size = ButtonSize.Large,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
                UiThemeOverride(
                    interactions = {
                        copy(
                            pressedOverlay = (Theme.colors.primary and 0x00FFFFFF) or 0x44000000,
                        )
                    },
                ) {
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .cornerRadius(SurfaceDefaults.cardCornerRadius())
                            .padding(12.dp),
                    ) {
                        Text(text = "Interaction Override")
                        ThemeSwatchRow(
                            label = "Pressed Overlay",
                            swatches = listOf(
                                ThemeSwatch("Base", Theme.interactions.pressedOverlay),
                                ThemeSwatch("Primary", Theme.colors.primary),
                            ),
                        )
                        Row(
                            spacing = 8.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Button(
                                text = "Press Me",
                                variant = ButtonVariant.Primary,
                                modifier = Modifier.weight(1f),
                            )
                            Button(
                                text = "Outlined",
                                variant = ButtonVariant.Outlined,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
                ProvideButtonColors(
                    ButtonColorOverride(
                        primaryContainer = Theme.colors.textPrimary,
                        primaryContent = Theme.colors.background,
                        primaryDisabledContainer = Theme.colors.divider,
                        primaryDisabledContent = Theme.colors.textSecondary,
                        outlinedBorder = Theme.colors.accent,
                        outlinedDisabledBorder = Theme.colors.textSecondary,
                    ),
                ) {
                ProvideTextFieldColors(
                    TextFieldColorOverride(
                        filledDisabledContainer = Theme.colors.surfaceVariant,
                        outlinedErrorBorder = Theme.colors.accent,
                    ),
                ) {
                ProvideSegmentedControlColors(
                    SegmentedControlColorOverride(
                        indicator = Theme.colors.accent,
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
                        Text(text = "Component Defaults Override")
                        Text(
                            text = "This block changes button and segmented defaults without changing the base color palette.",
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
                            items = listOf("Disabled", "State"),
                            selectedIndex = 0,
                            enabled = false,
                            onSelectionChange = {},
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(
                            spacing = 8.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Button(
                                text = "Primary Token",
                                leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                                modifier = Modifier.weight(1f),
                            )
                            Button(
                                text = "Outlined Token",
                                trailingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                                variant = ButtonVariant.Outlined,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(
                            spacing = 8.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Button(
                                text = "Disabled Primary",
                                enabled = false,
                                modifier = Modifier.weight(1f),
                            )
                            Button(
                                text = "Disabled Outline",
                                variant = ButtonVariant.Outlined,
                                enabled = false,
                                modifier = Modifier.weight(1f),
                            )
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
                    text = "Outside these blocks, the parent demo theme stays unchanged.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "progress" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Progress Indicators",
                subtitle = "These are mapped virtual controls backed by Material View widgets, but styled through framework defaults.",
            ) {
                Text(
                    text = "The linear indicator follows the current component tokens, while circular can run determinate or indeterminate.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
                LinearProgressIndicator(
                    progress = 0.68f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(top = 12.dp, bottom = 12.dp),
                )
                Row(
                    spacing = 16.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CircularProgressIndicator(progress = 0.42f)
                    CircularProgressIndicator()
                    Text(
                        text = "Progress family is now part of the P1 widget surface.",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            "media" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "Image + Icon",
                subtitle = "Media primitives are now separate from remote loading. The widget defines semantics; loading stays pluggable.",
            ) {
                Row(
                    spacing = 16.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.fillMaxWidth().margin(bottom = 12.dp),
                ) {
                    Image(
                        source = ImageSource.Resource(R.drawable.demo_media_image),
                        contentDescription = "Launcher image",
                        contentScale = ImageContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp, 64.dp)
                            .cornerRadius(Theme.shapes.cardCornerRadius),
                    )
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Image uses a typed source and content scale.")
                        Text(
                            text = "Remote loading is now provided by an optional Coil integration module without changing the Image API.",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            color = TextDefaults.secondaryColor(),
                        )
                    }
                }
                Image(
                    source = ImageSource.Remote("https://picsum.photos/seed/uiframework-demo/640/360"),
                    contentDescription = "Remote image",
                    contentScale = ImageContentScale.Crop,
                    placeholder = ImageSource.Resource(R.drawable.demo_media_image),
                    error = ImageSource.Resource(R.drawable.demo_media_image),
                    fallback = ImageSource.Resource(R.drawable.demo_media_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(Theme.shapes.cardCornerRadius)
                        .margin(bottom = 12.dp),
                )
                Image(
                    source = ImageSource.Remote(null),
                    contentDescription = "Fallback image",
                    contentScale = ImageContentScale.Crop,
                    fallback = ImageSource.Resource(R.drawable.demo_media_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(Theme.shapes.cardCornerRadius)
                        .margin(bottom = 12.dp),
                )
                Row(
                    spacing = 12.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Surface(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Icon(
                            source = ImageSource.Resource(R.drawable.demo_media_icon),
                            contentDescription = "Foreground icon",
                        )
                    }
                    UiThemeOverride(
                        colors = {
                            copy(textPrimary = accent)
                        },
                    ) {
                        Surface(
                            variant = SurfaceVariant.Variant,
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Icon(
                                source = ImageSource.Resource(R.drawable.demo_media_icon),
                                contentDescription = "Accent icon",
                            )
                        }
                    }
                    Text(
                        text = "Icon defaults to ContentColor.current, so it naturally follows local surface/content scopes.",
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    spacing = 12.dp,
                    modifier = Modifier.fillMaxWidth().margin(top = 12.dp),
                ) {
                    IconButton(
                        icon = ImageSource.Resource(R.drawable.demo_media_icon),
                        contentDescription = "Primary icon button",
                        modifier = Modifier,
                    )
                    IconButton(
                        icon = ImageSource.Resource(R.drawable.demo_media_icon),
                        contentDescription = "Tonal icon button",
                        variant = ButtonVariant.Tonal,
                    )
                    IconButton(
                        icon = ImageSource.Resource(R.drawable.demo_media_icon),
                        contentDescription = "Outlined icon button",
                        variant = ButtonVariant.Outlined,
                    )
                    IconButton(
                        icon = ImageSource.Resource(R.drawable.demo_media_icon),
                        contentDescription = "Disabled icon button",
                        enabled = false,
                    )
                }
            }

            "jump" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "Jump To A Capability",
                subtitle = "These buttons now navigate between the top-level demo chapters, so manual testing follows stable paths.",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Open Foundations -> Guide page -> Jump To A Capability",
                    stableTargets = listOf(
                        "Open Layouts",
                        "Open Input",
                        "Open State",
                        "Open Collections",
                        "Open Interop",
                    ),
                )
                Button(
                    text = "Open Layouts",
                    modifier = Modifier.margin(bottom = 8.dp),
                    onClick = {
                        onOpenCapability(LayoutsActivity::class.java)
                    },
                )
                Button(
                    text = "Open Input",
                    modifier = Modifier.margin(bottom = 8.dp),
                    onClick = {
                        onOpenCapability(InputActivity::class.java)
                    },
                )
                Button(
                    text = "Open State",
                    modifier = Modifier.margin(bottom = 8.dp),
                    onClick = {
                        onOpenCapability(StateActivity::class.java)
                    },
                )
                Button(
                    text = "Open Collections",
                    onClick = {
                        onOpenCapability(CollectionsActivity::class.java)
                    },
                )
                Button(
                    text = "Open Interop",
                    modifier = Modifier.margin(top = 8.dp),
                    onClick = {
                        onOpenCapability(InteropActivity::class.java)
                    },
                )
            }

            "surface" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Current Surface",
                subtitle = "The first vertical slice now includes the following framework controls.",
            ) {
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
                ) {
                    Button(
                        text = "Primary",
                        variant = ButtonVariant.Primary,
                        size = ButtonSize.Compact,
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        text = "Tonal",
                        variant = ButtonVariant.Tonal,
                        size = ButtonSize.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        text = "Outline",
                        variant = ButtonVariant.Outlined,
                        size = ButtonSize.Large,
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(text = "Text, TextField, EmailField, PasswordField, NumberField, TextArea")
                Text(text = "Row, Column, Box, Divider, Spacer, FlexibleSpacer, LazyColumn")
                Text(text = "Progress indicators, AndroidView interop, TabPager, state runtime, effect runtime")
            }

            else -> VerificationNotesSection(
                what = "Foundations should prove that the current theme, media, and button families render consistently under chapter navigation and theme switching.",
                howToVerify = listOf(
                    "切换顶部 theme mode，确认 Foundations 章内所有 section 颜色、圆角和点击态一起更新。",
                    "观察远程图片、本地图标和 IconButton，确认不会出现大面积空白或错误布局。",
                    "点击 Jump 区域按钮，确认章节切换稳定且返回 Foundations 后状态仍然正常。",
                ),
                expected = listOf(
                    "所有基础控件在亮色、暗色和系统模式下都保持可读。",
                    "Image 的 placeholder / fallback 场景可见，Icon 跟随 ContentColor 变化。",
                    "TabPager 顶部导航可滚动，不会因为章节增多而截断。",
                ),
                relatedGaps = listOf(
                    "还没有更细的 page 内二级导航。",
                    "还没有图形和动画类基础能力。",
                ),
            )
        }
    }
}
