package com.gzq.uiframework

import android.graphics.Typeface
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
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.TextDecoration
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonColorOverride
import com.gzq.uiframework.widget.core.ButtonSize
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.CircularProgressIndicator
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Divider
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

internal fun UiTreeBuilder.OverviewPage(
    initialPageIndex: Int = 0,
    onOpenCapability: (Class<out androidx.appcompat.app.AppCompatActivity>) -> Unit,
) {
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 3)) }
    val benchmarkState = remember { mutableStateOf(false) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "intro", "jump", "surface", "verify")
        1 -> listOf("overrides", "page", "page_filter", "theme", "verify")
        2 -> listOf("page", "page_filter", "progress", "media", "verify")
        else -> listOf("page", "page_filter", "typography", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "基础组件",
                goal = "验证核心视觉原语、主题作用域、组件默认值和媒体控件，然后再进入更高级的运行时场景。",
                modules = listOf("ui-widget-core", "ui-renderer", "theme locals", "component defaults"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("指南", "主题", "媒体", "排版"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "intro" -> ScenarioSection(
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

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "基础组件 Benchmark 锚点",
                subtitle = "停留在默认页面，使用简短稳定的标签，便于宏 benchmark 进入。",
            ) {
                Button(
                    text = if (benchmarkState.value) "Benchmark 已开启" else "Benchmark 已关闭",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { benchmarkState.value = !benchmarkState.value },
                )
                Button(
                    text = "重置 Benchmark",
                    variant = ButtonVariant.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { benchmarkState.value = false },
                )
                BenchmarkRouteCallout(
                    route = "Launcher -> Foundations -> Benchmark Anchor",
                    stableTargets = listOf("Benchmark On / Off", "Reset"),
                )
            }

            "theme" -> ScenarioSection(
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
                        ThemeSwatch("Accent", Theme.colors.accent),
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
                    text = "Shapes: card=${Theme.shapes.cardCornerRadius}px, control=${Theme.shapes.controlCornerRadius}px",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "overrides" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "作用域主题覆盖",
                subtitle = "每个区块仅覆盖一个 token 域的局部子树。",
            ) {
                UiThemeOverride(
                    colors = {
                        copy(primary = accent, surfaceVariant = primary)
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
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                UiThemeOverride(
                    shapes = {
                        copy(cardCornerRadius = 32.dp, controlCornerRadius = 24.dp)
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
                            text = "局部 card=${Theme.shapes.cardCornerRadius}px, control=${Theme.shapes.controlCornerRadius}px",
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
                        Text(text = "组件默认值覆盖")
                        Text(
                            text = "此区块修改按钮和分段控件的默认值，不改变基础调色板。",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            color = TextDefaults.secondaryColor(),
                        )
                        SegmentedControl(items = listOf("Alpha", "Beta", "Gamma"), selectedIndex = 1, onSelectionChange = {}, modifier = Modifier.fillMaxWidth())
                        SegmentedControl(items = listOf("禁用", "状态"), selectedIndex = 0, enabled = false, onSelectionChange = {}, modifier = Modifier.fillMaxWidth())
                        Row(spacing = 8.dp, modifier = Modifier.fillMaxWidth()) {
                            Button(text = "Primary Token", leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon), modifier = Modifier.weight(1f))
                            Button(text = "Outlined Token", trailingIcon = ImageSource.Resource(R.drawable.demo_media_icon), variant = ButtonVariant.Outlined, modifier = Modifier.weight(1f))
                        }
                        Row(spacing = 8.dp, modifier = Modifier.fillMaxWidth()) {
                            Button(text = "禁用 Primary", enabled = false, modifier = Modifier.weight(1f))
                            Button(text = "禁用 Outline", variant = ButtonVariant.Outlined, enabled = false, modifier = Modifier.weight(1f))
                        }
                        TextField(value = "error@token.dev", onValueChange = {}, variant = TextFieldVariant.Outlined, isError = true, modifier = Modifier.fillMaxWidth())
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

            "progress" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "进度指示器",
                subtitle = "线性和圆形进度指示器，通过框架默认值设置样式。",
            ) {
                Text(
                    text = "线性指示器跟随当前组件 token，圆形可运行确定/不确定模式。",
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
                        text = "进度指示器已包含在 P1 控件面中。",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            "media" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "Image + Icon",
                subtitle = "媒体原语与远程加载分离。控件定义语义，加载保持可插拔。",
            ) {
                Row(
                    spacing = 16.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.fillMaxWidth().margin(bottom = 12.dp),
                ) {
                    Image(
                        source = ImageSource.Resource(R.drawable.demo_media_image),
                        contentDescription = "启动图标",
                        contentScale = ImageContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp, 64.dp)
                            .cornerRadius(Theme.shapes.cardCornerRadius),
                    )
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Image 使用类型化 source 和 contentScale。")
                        Text(
                            text = "远程加载由可选的 Coil 集成模块提供，不改变 Image API。",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            color = TextDefaults.secondaryColor(),
                        )
                    }
                }
                Image(
                    source = ImageSource.Remote("https://picsum.photos/seed/uiframework-demo/640/360"),
                    contentDescription = "远程图片",
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
                    contentDescription = "回退图片",
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
                    Surface(modifier = Modifier.padding(8.dp)) {
                        Icon(source = ImageSource.Resource(R.drawable.demo_media_icon), contentDescription = "前景图标")
                    }
                    UiThemeOverride(colors = { copy(textPrimary = accent) }) {
                        Surface(variant = SurfaceVariant.Variant, modifier = Modifier.padding(8.dp)) {
                            Icon(source = ImageSource.Resource(R.drawable.demo_media_icon), contentDescription = "Accent 图标")
                        }
                    }
                    Text(
                        text = "Icon 默认跟随 ContentColor.current，自然适配局部 surface/content 作用域。",
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    spacing = 12.dp,
                    modifier = Modifier.fillMaxWidth().margin(top = 12.dp),
                ) {
                    IconButton(icon = ImageSource.Resource(R.drawable.demo_media_icon), contentDescription = "Primary 图标按钮")
                    IconButton(icon = ImageSource.Resource(R.drawable.demo_media_icon), contentDescription = "Tonal 图标按钮", variant = ButtonVariant.Tonal)
                    IconButton(icon = ImageSource.Resource(R.drawable.demo_media_icon), contentDescription = "Outlined 图标按钮", variant = ButtonVariant.Outlined)
                    IconButton(icon = ImageSource.Resource(R.drawable.demo_media_icon), contentDescription = "禁用图标按钮", enabled = false)
                }
            }

            "typography" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "Text 排版属性",
                subtitle = "展示 fontWeight、fontFamily、letterSpacing、lineHeight、textDecoration、maxLines + overflow。",
            ) {
                Text(
                    text = "fontWeight 字重",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Column(
                    spacing = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "Normal (400)", style = UiTextStyle(fontSizeSp = 15.sp, fontWeight = 400))
                    Text(text = "Medium (500)", style = UiTextStyle(fontSizeSp = 15.sp, fontWeight = 500))
                    Text(text = "Bold (700)", style = UiTextStyle(fontSizeSp = 15.sp, fontWeight = 700))
                    Text(text = "Black (900)", style = UiTextStyle(fontSizeSp = 15.sp, fontWeight = 900))
                }
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Text(
                    text = "fontFamily 字体族",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Column(
                    spacing = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "默认字体 (Default)", style = UiTextStyle(fontSizeSp = 15.sp))
                    Text(text = "等宽字体 (Monospace)", style = UiTextStyle(fontSizeSp = 15.sp, fontFamily = Typeface.MONOSPACE))
                    Text(text = "衬线字体 (Serif)", style = UiTextStyle(fontSizeSp = 15.sp, fontFamily = Typeface.SERIF))
                    Text(text = "无衬线字体 (Sans-Serif)", style = UiTextStyle(fontSizeSp = 15.sp, fontFamily = Typeface.SANS_SERIF))
                }
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Text(
                    text = "letterSpacing 字间距",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Column(
                    spacing = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "默认字间距", style = UiTextStyle(fontSizeSp = 15.sp))
                    Text(text = "字间距 0.05em", style = UiTextStyle(fontSizeSp = 15.sp, letterSpacingEm = 0.05f))
                    Text(text = "字间距 0.15em", style = UiTextStyle(fontSizeSp = 15.sp, letterSpacingEm = 0.15f))
                }
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Text(
                    text = "lineHeight 行高",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Column(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Text(
                        text = "默认行高的多行文本。这段文字的行高是框架默认值。可以对比下方设置了明确行高的文本。",
                        style = UiTextStyle(fontSizeSp = 14.sp),
                    )
                    Text(
                        text = "行高 24sp 的多行文本。这段文字明确设置了 lineHeightSp = 24，行间距更大更宽松。",
                        style = UiTextStyle(fontSizeSp = 14.sp, lineHeightSp = 24.sp),
                    )
                }
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Text(
                    text = "textDecoration 文本装饰",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Column(
                    spacing = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                ) {
                    Text(text = "无装饰 (None)", textDecoration = TextDecoration.None)
                    Text(text = "下划线 (Underline)", textDecoration = TextDecoration.Underline)
                    Text(text = "删除线 (LineThrough)", textDecoration = TextDecoration.LineThrough)
                    Text(text = "下划线+删除线", textDecoration = TextDecoration.UnderlineLineThrough)
                }
                Divider(modifier = Modifier.margin(bottom = 12.dp))
                Text(
                    text = "maxLines + overflow 截断",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Surface(
                    variant = SurfaceVariant.Variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .margin(bottom = 8.dp),
                ) {
                    Text(
                        text = "这是一段很长的文本，设置了 maxLines=1 和 overflow=Ellipsis。当文本超出一行时会在末尾显示省略号…来表示内容被截断。",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Surface(
                    variant = SurfaceVariant.Variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                ) {
                    Text(
                        text = "这是一段很长的文本，设置了 maxLines=2 和 overflow=Ellipsis。当文本超出两行时会在末尾显示省略号。这段文字故意写得很长以触发截断效果，观察第二行末尾的省略号。",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            "jump" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "跳转到其他章节",
                subtitle = "这些按钮在顶层 demo 章节间导航。",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Foundations -> 指南页 -> 跳转",
                    stableTargets = listOf("打开布局", "打开输入", "打开状态", "打开集合", "打开互操作"),
                )
                Button(text = "打开布局", modifier = Modifier.margin(bottom = 8.dp), onClick = { onOpenCapability(LayoutsActivity::class.java) })
                Button(text = "打开输入", modifier = Modifier.margin(bottom = 8.dp), onClick = { onOpenCapability(InputActivity::class.java) })
                Button(text = "打开状态", modifier = Modifier.margin(bottom = 8.dp), onClick = { onOpenCapability(StateActivity::class.java) })
                Button(text = "打开集合", onClick = { onOpenCapability(CollectionsActivity::class.java) })
                Button(text = "打开互操作", modifier = Modifier.margin(top = 8.dp), onClick = { onOpenCapability(InteropActivity::class.java) })
            }

            "surface" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "当前控件面",
                subtitle = "第一个垂直切片包含以下框架控件。",
            ) {
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
                ) {
                    Button(text = "Primary", variant = ButtonVariant.Primary, size = ButtonSize.Compact, modifier = Modifier.weight(1f))
                    Button(text = "Tonal", variant = ButtonVariant.Tonal, size = ButtonSize.Medium, modifier = Modifier.weight(1f))
                    Button(text = "Outline", variant = ButtonVariant.Outlined, size = ButtonSize.Large, modifier = Modifier.weight(1f))
                }
                Text(text = "Text, TextField, EmailField, PasswordField, NumberField, TextArea")
                Text(text = "Row, Column, Box, Divider, Spacer, FlexibleSpacer, LazyColumn")
                Text(text = "进度指示器, AndroidView 互操作, TabRow + HorizontalPager, 状态运行时, 副作用运行时")
            }

            else -> VerificationNotesSection(
                what = "基础组件应验证当前主题、媒体和按钮家族在章节导航和主题切换下的一致性渲染。",
                howToVerify = listOf(
                    "切换顶部 theme mode，确认所有 section 颜色、圆角和点击态一起更新。",
                    "观察远程图片、本地图标和 IconButton，确认不会出现大面积空白或错误布局。",
                    "观察排版页面的 fontWeight/fontFamily/letterSpacing/lineHeight/textDecoration 效果。",
                    "确认 maxLines + overflow=Ellipsis 截断效果正确显示省略号。",
                ),
                expected = listOf(
                    "所有基础控件在亮色、暗色和系统模式下都保持可读。",
                    "Image 的 placeholder / fallback 场景可见，Icon 跟随 ContentColor 变化。",
                    "Text 排版属性（字重、字体族、字间距、行高、装饰线）均正确渲染。",
                ),
            )
        }
    }
}
