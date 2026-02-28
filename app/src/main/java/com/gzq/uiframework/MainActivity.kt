package com.gzq.uiframework

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.image.coil.CoilRemoteImageLoader
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.Visibility
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
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.modifier.textColor
import com.gzq.uiframework.renderer.modifier.visibility
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.renderer.modifier.width
import com.gzq.uiframework.renderer.modifier.zIndex
import com.gzq.uiframework.runtime.MutableState
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AndroidView
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonSize
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.DisposableEffect
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.EmailField
import com.gzq.uiframework.widget.core.Environment
import com.gzq.uiframework.widget.core.FlexibleSpacer
import com.gzq.uiframework.widget.core.Checkbox
import com.gzq.uiframework.widget.core.CircularProgressIndicator
import com.gzq.uiframework.widget.core.Icon
import com.gzq.uiframework.widget.core.IconButton
import com.gzq.uiframework.widget.core.Image
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.LinearProgressIndicator
import com.gzq.uiframework.widget.core.NumberField
import com.gzq.uiframework.widget.core.PasswordField
import com.gzq.uiframework.widget.core.ProvideRemoteImageLoader
import com.gzq.uiframework.widget.core.RadioButton
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SegmentedControl
import com.gzq.uiframework.widget.core.SegmentedControlSize
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.Slider
import com.gzq.uiframework.widget.core.Surface
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.SurfaceVariant
import com.gzq.uiframework.widget.core.Switch
import com.gzq.uiframework.widget.core.TabPager
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextArea
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.TextField
import com.gzq.uiframework.widget.core.TextFieldSize
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.UiEnvironment
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTheme
import com.gzq.uiframework.widget.core.UiThemeOverride
import com.gzq.uiframework.widget.core.TextFieldVariant
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.key
import com.gzq.uiframework.widget.core.produceState
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.renderInto
import com.gzq.uiframework.widget.core.sp
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import java.util.Locale

private data class DemoChapter(
    val key: String,
    val title: String,
)

private val DEMO_CHAPTERS = listOf(
    DemoChapter(key = "foundations", title = "Foundations"),
    DemoChapter(key = "state", title = "State"),
    DemoChapter(key = "layouts", title = "Layouts"),
    DemoChapter(key = "input", title = "Input"),
    DemoChapter(key = "collections", title = "Collections"),
    DemoChapter(key = "gestures", title = "Gestures"),
    DemoChapter(key = "animation", title = "Animation"),
    DemoChapter(key = "graphics", title = "Graphics"),
    DemoChapter(key = "navigation", title = "Navigation"),
    DemoChapter(key = "interop", title = "Interop"),
    DemoChapter(key = "diagnostics", title = "Diagnostics"),
)

private const val CHAPTER_FOUNDATIONS = 0
private const val CHAPTER_STATE = 1
private const val CHAPTER_LAYOUTS = 2
private const val CHAPTER_INPUT = 3
private const val CHAPTER_COLLECTIONS = 4
private const val CHAPTER_GESTURES = 5
private const val CHAPTER_ANIMATION = 6
private const val CHAPTER_GRAPHICS = 7
private const val CHAPTER_NAVIGATION = 8
private const val CHAPTER_INTEROP = 9
private const val CHAPTER_DIAGNOSTICS = 10

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val root = findViewById<ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        renderInto(
            container = root,
            debug = true,
            debugTag = "UIFrameworkSample",
        ) {
            DemoRoot(root)
        }
    }
}

private fun UiTreeBuilder.DemoRoot(root: ViewGroup) {
    val selectedChapterState = remember { mutableStateOf(CHAPTER_FOUNDATIONS) }
    val themeModeState = remember { mutableStateOf(DemoThemeMode.System) }
    val remoteImageLoader = remember { CoilRemoteImageLoader(root.context.applicationContext) }
    val activity = root.context as? AppCompatActivity
    UiEnvironment(androidContext = root.context) {
        val resolvedTheme = DemoThemeTokens.resolve(
            mode = themeModeState.value,
            context = root.context,
        )
        ProvideRemoteImageLoader(remoteImageLoader) {
            UiTheme(tokens = resolvedTheme) {
            val environmentLabel = "Env: ${Environment.localeTags.firstOrNull() ?: "und"} · " +
                "${Environment.layoutDirection.name} · " +
                "${"%.2f".format(Locale.US, Environment.density.density)}x · " +
                DemoThemeTokens.modeLabel(themeModeState.value, root.context)
            SideEffect {
                activity?.title = "UIFramework - ${DEMO_CHAPTERS[selectedChapterState.value].title} - " +
                    DemoThemeTokens.modeLabel(themeModeState.value, root.context)
            }
            DisposableEffect {
                {
                    activity?.title = "UIFramework"
                }
            }
            Column(
                spacing = 8.dp,
                modifier = Modifier.Empty
                    .fillMaxSize()
                    .backgroundColor(Theme.colors.background)
                    .padding(24.dp),
            ) {
                Text(
                    text = "UIFramework Sample",
                    style = UiTextStyle(fontSizeSp = 30.sp),
                )
                Text(
                    text = "Declarative UI runtime on Android Views, regrouped into chapter-driven manual test paths.",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
                Text(
                    text = environmentLabel,
                    style = UiTextStyle(fontSizeSp = 12.sp),
                    modifier = Modifier.Empty
                        .textColor(TextDefaults.secondaryColor())
                        .padding(vertical = 4.dp),
                )
                Column(
                    spacing = 8.dp,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Demo Theme",
                        style = UiTextStyle(fontSizeSp = 14.sp),
                    )
                    SegmentedControl(
                        items = listOf("System", "Light", "Dark"),
                        selectedIndex = themeModeState.value.ordinal,
                        onSelectionChange = { index ->
                            themeModeState.value = DemoThemeMode.entries[index]
                        },
                        size = SegmentedControlSize.Medium,
                        modifier = Modifier.Empty.fillMaxWidth(),
                    )
                }
                TabPager(
                    selectedTabIndex = selectedChapterState.value,
                    onTabSelected = { index ->
                        selectedChapterState.value = index
                    },
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .weight(1f)
                        .margin(top = 12.dp),
                ) {
                    Page(title = DEMO_CHAPTERS[CHAPTER_FOUNDATIONS].title, key = DEMO_CHAPTERS[CHAPTER_FOUNDATIONS].key) {
                        OverviewPage(selectedChapterState)
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_STATE].title, key = DEMO_CHAPTERS[CHAPTER_STATE].key) {
                        StatePage()
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_LAYOUTS].title, key = DEMO_CHAPTERS[CHAPTER_LAYOUTS].key) {
                        LayoutPage()
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_INPUT].title, key = DEMO_CHAPTERS[CHAPTER_INPUT].key) {
                        InputPage()
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_COLLECTIONS].title, key = DEMO_CHAPTERS[CHAPTER_COLLECTIONS].key) {
                        CollectionPage()
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_GESTURES].title, key = DEMO_CHAPTERS[CHAPTER_GESTURES].key) {
                        ChapterPlaceholderPage(
                            title = "Gestures",
                            subtitle = "This chapter is reserved for pointer and gesture scenarios that the framework has not implemented yet.",
                            plannedPages = listOf(
                                "Click / Long press",
                                "Drag / Swipe",
                                "Nested scroll conflicts",
                            ),
                            currentGaps = listOf(
                                "No unified gesture DSL",
                                "No drag or swipe semantics",
                                "No nested scroll coordination",
                            ),
                        )
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_ANIMATION].title, key = DEMO_CHAPTERS[CHAPTER_ANIMATION].key) {
                        ChapterPlaceholderPage(
                            title = "Animation",
                            subtitle = "This chapter will track state-driven motion once the framework has a formal animation runtime.",
                            plannedPages = listOf(
                                "Animated visibility",
                                "Value interpolation",
                                "List item transitions",
                            ),
                            currentGaps = listOf(
                                "No animation runtime",
                                "No transition DSL",
                                "No enter/exit list animations",
                            ),
                        )
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_GRAPHICS].title, key = DEMO_CHAPTERS[CHAPTER_GRAPHICS].key) {
                        ChapterPlaceholderPage(
                            title = "Graphics",
                            subtitle = "This chapter is reserved for future drawing primitives and custom rendering surfaces.",
                            plannedPages = listOf(
                                "Canvas basics",
                                "Paths and gradients",
                                "Custom badges and charts",
                            ),
                            currentGaps = listOf(
                                "No canvas DSL",
                                "No draw modifier pipeline",
                                "No custom graphics node layer",
                            ),
                        )
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_NAVIGATION].title, key = DEMO_CHAPTERS[CHAPTER_NAVIGATION].key) {
                        ChapterPlaceholderPage(
                            title = "Navigation",
                            subtitle = "This chapter will host future navigation experiments once host integration and screen models are formalized.",
                            plannedPages = listOf(
                                "Host integration",
                                "Screen state switching",
                                "Page stack model",
                            ),
                            currentGaps = listOf(
                                "No framework router",
                                "No page stack abstraction",
                                "No navigation-level state restoration",
                            ),
                        )
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_INTEROP].title, key = DEMO_CHAPTERS[CHAPTER_INTEROP].key) {
                        InteropPage()
                    }
                    Page(title = DEMO_CHAPTERS[CHAPTER_DIAGNOSTICS].title, key = DEMO_CHAPTERS[CHAPTER_DIAGNOSTICS].key) {
                        DiagnosticsPage()
                    }
                }
            }
            }
        }
    }
}

private fun UiTreeBuilder.OverviewPage(
    selectedChapterState: MutableState<Int>,
) {
    val selectedPageState = remember { mutableStateOf(0) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("page", "page_filter", "intro", "jump", "surface", "verify")
        1 -> listOf("page", "page_filter", "theme", "overrides", "verify")
        else -> listOf("page", "page_filter", "progress", "media", "verify")
    }
    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
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

            "intro" -> DemoSection(
                title = "Why This Demo Changed",
                subtitle = "The sample is now split by category so the runtime surface is easier to read and extend.",
            ) {
                Text(
                    text = "Each tab isolates one concern: layout, text/input, state, and collection-level rendering.",
                )
                Text(
                    text = "The pager itself is also rendered through the framework as a mapped virtual control.",
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "theme" -> DemoSection(
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
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "overrides" -> DemoSection(
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
                        modifier = Modifier.Empty
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
                            modifier = Modifier.Empty.fillMaxWidth(),
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
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .cornerRadius(SurfaceDefaults.cardCornerRadius())
                            .padding(16.dp),
                    ) {
                        Text(text = "Shape Override")
                        Text(
                            text = "Local card=${Theme.shapes.cardCornerRadius}px, control=${Theme.shapes.controlCornerRadius}px",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                        )
                        Row(
                            spacing = 8.dp,
                            modifier = Modifier.Empty.fillMaxWidth(),
                        ) {
                            Box(
                                modifier = Modifier.Empty
                                    .weight(1f)
                                    .height(72.dp)
                                    .backgroundColor(Theme.colors.surfaceVariant)
                                    .cornerRadius(SurfaceDefaults.cardCornerRadius()),
                            ) {}
                            Button(
                                text = "Rounded Action",
                                variant = ButtonVariant.Tonal,
                                size = ButtonSize.Large,
                                modifier = Modifier.Empty.weight(1f),
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
                        modifier = Modifier.Empty
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
                            modifier = Modifier.Empty.fillMaxWidth(),
                        ) {
                            Button(
                                text = "Press Me",
                                variant = ButtonVariant.Primary,
                                modifier = Modifier.Empty.weight(1f),
                            )
                            Button(
                                text = "Outlined",
                                variant = ButtonVariant.Outlined,
                                modifier = Modifier.Empty.weight(1f),
                            )
                        }
                    }
                }
                UiThemeOverride(
                    components = {
                        copy(
                            button = button.copy(
                                primaryContainer = Theme.colors.textPrimary,
                                primaryContent = Theme.colors.background,
                                primaryDisabledContainer = Theme.colors.divider,
                                primaryDisabledContent = Theme.colors.textSecondary,
                                outlinedBorder = Theme.colors.accent,
                                outlinedDisabledBorder = Theme.colors.textSecondary,
                            ),
                            textField = textField.copy(
                                filledDisabledContainer = Theme.colors.surfaceVariant,
                                outlinedErrorBorder = Theme.colors.accent,
                            ),
                            segmentedControl = segmentedControl.copy(
                                indicator = Theme.colors.accent,
                                indicatorDisabled = Theme.colors.divider,
                                selectedText = Theme.colors.background,
                                selectedTextDisabled = Theme.colors.textSecondary,
                            ),
                        )
                    },
                ) {
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .cornerRadius(SurfaceDefaults.cardCornerRadius())
                            .padding(12.dp),
                    ) {
                        Text(text = "Component Defaults Override")
                        Text(
                            text = "This block changes button and segmented defaults without changing the base color palette.",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                        )
                        SegmentedControl(
                            items = listOf("Alpha", "Beta", "Gamma"),
                            selectedIndex = 1,
                            onSelectionChange = {},
                            modifier = Modifier.Empty.fillMaxWidth(),
                        )
                        SegmentedControl(
                            items = listOf("Disabled", "State"),
                            selectedIndex = 0,
                            enabled = false,
                            onSelectionChange = {},
                            modifier = Modifier.Empty.fillMaxWidth(),
                        )
                        Row(
                            spacing = 8.dp,
                            modifier = Modifier.Empty.fillMaxWidth(),
                        ) {
                            Button(
                                text = "Primary Token",
                                leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                                modifier = Modifier.Empty.weight(1f),
                            )
                            Button(
                                text = "Outlined Token",
                                trailingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                                variant = ButtonVariant.Outlined,
                                modifier = Modifier.Empty.weight(1f),
                            )
                        }
                        Row(
                            spacing = 8.dp,
                            modifier = Modifier.Empty.fillMaxWidth(),
                        ) {
                            Button(
                                text = "Disabled Primary",
                                enabled = false,
                                modifier = Modifier.Empty.weight(1f),
                            )
                            Button(
                                text = "Disabled Outline",
                                variant = ButtonVariant.Outlined,
                                enabled = false,
                                modifier = Modifier.Empty.weight(1f),
                            )
                        }
                        TextField(
                            value = "error@token.dev",
                            onValueChange = {},
                            variant = TextFieldVariant.Outlined,
                            isError = true,
                            modifier = Modifier.Empty.fillMaxWidth(),
                        )
                    }
                }
                Text(
                    text = "Outside these blocks, the parent demo theme stays unchanged.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "progress" -> DemoSection(
                title = "Progress Indicators",
                subtitle = "These are mapped virtual controls backed by Material View widgets, but styled through framework defaults.",
            ) {
                Text(
                    text = "The linear indicator follows the current component tokens, while circular can run determinate or indeterminate.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
                LinearProgressIndicator(
                    progress = 0.68f,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(top = 12.dp, bottom = 12.dp),
                )
                Row(
                    spacing = 16.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty.fillMaxWidth(),
                ) {
                    CircularProgressIndicator(progress = 0.42f)
                    CircularProgressIndicator()
                    Text(
                        text = "Progress family is now part of the P1 widget surface.",
                        modifier = Modifier.Empty.weight(1f),
                    )
                }
            }

            "media" -> DemoSection(
                title = "Image + Icon",
                subtitle = "Media primitives are now separate from remote loading. The widget defines semantics; loading stays pluggable.",
            ) {
                Row(
                    spacing = 16.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty.fillMaxWidth().margin(bottom = 12.dp),
                ) {
                    Image(
                        source = ImageSource.Resource(R.drawable.demo_media_image),
                        contentDescription = "Launcher image",
                        contentScale = ImageContentScale.Crop,
                        modifier = Modifier.Empty
                            .size(64.dp, 64.dp)
                            .cornerRadius(Theme.shapes.cardCornerRadius),
                    )
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier.Empty.weight(1f),
                    ) {
                        Text(text = "Image uses a typed source and content scale.")
                        Text(
                            text = "Remote loading is now provided by an optional Coil integration module without changing the Image API.",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
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
                    modifier = Modifier.Empty
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
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(88.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(Theme.shapes.cardCornerRadius)
                        .margin(bottom = 12.dp),
                )
                Row(
                    spacing = 12.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty.fillMaxWidth(),
                ) {
                        Surface(
                            modifier = Modifier.Empty.padding(8.dp),
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
                            modifier = Modifier.Empty.padding(8.dp),
                        ) {
                            Icon(
                                source = ImageSource.Resource(R.drawable.demo_media_icon),
                                contentDescription = "Accent icon",
                            )
                        }
                    }
                    Text(
                        text = "Icon defaults to ContentColor.current, so it naturally follows local surface/content scopes.",
                        modifier = Modifier.Empty.weight(1f),
                    )
                }
                Row(
                    spacing = 12.dp,
                    modifier = Modifier.Empty.fillMaxWidth().margin(top = 12.dp),
                ) {
                    IconButton(
                        icon = ImageSource.Resource(R.drawable.demo_media_icon),
                        contentDescription = "Primary icon button",
                        modifier = Modifier.Empty,
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

            "jump" -> DemoSection(
                title = "Jump To A Capability",
                subtitle = "These buttons now navigate between the top-level demo chapters, so manual testing follows stable paths.",
            ) {
                Button(
                    text = "Open Layouts",
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                    onClick = {
                        selectedChapterState.value = CHAPTER_LAYOUTS
                    },
                )
                Button(
                    text = "Open Input",
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                    onClick = {
                        selectedChapterState.value = CHAPTER_INPUT
                    },
                )
                Button(
                    text = "Open State",
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                    onClick = {
                        selectedChapterState.value = CHAPTER_STATE
                    },
                )
                Button(
                    text = "Open Collections",
                    onClick = {
                        selectedChapterState.value = CHAPTER_COLLECTIONS
                    },
                )
                Button(
                    text = "Open Interop",
                    modifier = Modifier.Empty.margin(top = 8.dp),
                    onClick = {
                        selectedChapterState.value = CHAPTER_INTEROP
                    },
                )
            }

            "surface" -> DemoSection(
                title = "Current Surface",
                subtitle = "The first vertical slice now includes the following framework controls.",
            ) {
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.Empty.fillMaxWidth().margin(bottom = 8.dp),
                ) {
                    Button(
                        text = "Primary",
                        variant = ButtonVariant.Primary,
                        size = ButtonSize.Compact,
                        modifier = Modifier.Empty.weight(1f),
                    )
                    Button(
                        text = "Tonal",
                        variant = ButtonVariant.Tonal,
                        size = ButtonSize.Medium,
                        modifier = Modifier.Empty.weight(1f),
                    )
                    Button(
                        text = "Outline",
                        variant = ButtonVariant.Outlined,
                        size = ButtonSize.Large,
                        modifier = Modifier.Empty.weight(1f),
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

private fun UiTreeBuilder.ThemeSwatchRow(
    label: String,
    swatches: List<ThemeSwatch>,
) {
    Column(
        spacing = 8.dp,
        modifier = Modifier.Empty.margin(bottom = 8.dp),
    ) {
        Text(
            text = label,
            style = UiTextStyle(fontSizeSp = 13.sp),
            modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
        )
        Row(
            spacing = 8.dp,
            modifier = Modifier.Empty.fillMaxWidth(),
        ) {
            swatches.forEach { swatch ->
                Column(
                    spacing = 6.dp,
                    modifier = Modifier.Empty.weight(1f),
                ) {
                    Box(
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .height(48.dp)
                            .backgroundColor(swatch.color)
                            .cornerRadius(SurfaceDefaults.cardCornerRadius()),
                    ) {}
                    Text(
                        text = swatch.label,
                        style = UiTextStyle(fontSizeSp = 12.sp),
                    )
                }
            }
        }
    }
}

private fun UiTreeBuilder.LayoutPage() {
    val boxTapState = remember { mutableStateOf(0) }
    LazyColumn(
        items = listOf("page", "row", "box", "column", "verify"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Layouts",
                goal = "Stress the custom linear and box containers so measurement, placement, spacing, and child overrides stay predictable.",
                modules = listOf("DeclarativeLinearLayout", "DeclarativeBoxLayout", "layout defaults", "modifiers"),
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

            else -> VerificationNotesSection(
                what = "Layouts should expose measurement bugs quickly, especially around wrap content defaults, fill, weight, and child alignment overrides.",
                howToVerify = listOf(
                    "反复点击 Box 区域，确认点击态、overlay 和 pinned tag 不会错位。",
                    "在不同宽度设备上观察 Row 中 top/bottom 对齐文本，确认不会被 Stretch 或异常留白撑开。",
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

private fun UiTreeBuilder.InputPage() {
    val nameState = remember { mutableStateOf("GZQ") }
    val emailState = remember { mutableStateOf("demo@uiframework.dev") }
    val passwordState = remember { mutableStateOf("") }
    val ageState = remember { mutableStateOf("3") }
    val bioState = remember { mutableStateOf("Built on virtual nodes, keyed diff, and Android View interop.") }
    val notificationsEnabledState = remember { mutableStateOf(true) }
    val analyticsEnabledState = remember { mutableStateOf(false) }
    val selectedTierState = remember { mutableStateOf("Alpha") }
    val intensityState = remember { mutableStateOf(32) }
    val summaryState = remember {
        derivedStateOf {
            "Preview: ${nameState.value.ifBlank { "Anonymous" }} · " +
                "${emailState.value.ifBlank { "no-email" }} · " +
                "${ageState.value.ifBlank { "-" }}y"
        }
    }
    val selectedPageState = remember { mutableStateOf(0) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("page", "page_filter", "intro", "form", "verify")
        1 -> listOf("page", "page_filter", "controls", "verify")
        else -> listOf("page", "page_filter", "summary", "verify")
    }

    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Input",
                goal = "Verify that text fields and selection controls stay declarative across value updates, error states, variants, and local theme overrides.",
                modules = listOf("TextField family", "selection widgets", "input defaults", "theme components"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Fields", "Selection", "Summary"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "intro" -> DemoSection(
                title = "Text And Input Family",
                subtitle = "The framework now maps multiple `EditText` variants: text, password, email, number, and multiline text.",
            ) {
                Text(
                    text = "Typography also uses the formal dp/sp DSL in sample code.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "form" -> DemoSection(
                title = "Form Controls",
                subtitle = "All fields are state-driven and update the same render session.",
            ) {
                TextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    hint = "Name",
                    label = "Display name",
                    supportingText = "Shown in your profile header",
                    imeAction = TextFieldImeAction.Next,
                    variant = TextFieldVariant.Filled,
                    size = TextFieldSize.Large,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                EmailField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    hint = "Email",
                    label = "Work email",
                    supportingText = "Used for notifications only",
                    imeAction = TextFieldImeAction.Next,
                    variant = TextFieldVariant.Tonal,
                    size = TextFieldSize.Medium,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                PasswordField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    hint = "Password",
                    label = "Access key",
                    supportingText = "Blank keeps the current password",
                    imeAction = TextFieldImeAction.Done,
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Medium,
                    isError = passwordState.value.isBlank(),
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                NumberField(
                    value = ageState.value,
                    onValueChange = { ageState.value = it },
                    hint = "Version age",
                    label = "Project age",
                    supportingText = "Semantic version generations",
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Compact,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                EmailField(
                    value = "disabled@uiframework.dev",
                    onValueChange = {},
                    hint = "Disabled email",
                    label = "Readonly contact",
                    supportingText = "Inherited from organization settings",
                    variant = TextFieldVariant.Tonal,
                    size = TextFieldSize.Medium,
                    enabled = false,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                TextArea(
                    value = bioState.value,
                    onValueChange = { bioState.value = it },
                    hint = "Short bio",
                    label = "Summary",
                    supportingText = "Supports multiline notes and local state updates",
                    maxLines = 6,
                    imeAction = TextFieldImeAction.Done,
                    variant = TextFieldVariant.Filled,
                    size = TextFieldSize.Large,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(120.dp)
                        .margin(bottom = 12.dp),
                )
                Button(
                    text = "Reset Form",
                    leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    trailingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    size = ButtonSize.Large,
                    onClick = {
                        nameState.value = "GZQ"
                        emailState.value = "demo@uiframework.dev"
                        passwordState.value = ""
                        ageState.value = "3"
                        bioState.value = "Built on virtual nodes, keyed diff, and Android View interop."
                    },
                )
            }

            "controls" -> DemoSection(
                title = "Selection + Slider Controls",
                subtitle = "Checkbox, switch, radio button, and slider now live in the same declarative input family.",
            ) {
                Checkbox(
                    text = "Notifications",
                    checked = notificationsEnabledState.value,
                    onCheckedChange = { notificationsEnabledState.value = it },
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                )
                Switch(
                    text = "Analytics",
                    checked = analyticsEnabledState.value,
                    onCheckedChange = { analyticsEnabledState.value = it },
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                )
                RadioButton(
                    text = "Alpha tier",
                    checked = selectedTierState.value == "Alpha",
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedTierState.value = "Alpha"
                        }
                    },
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                )
                RadioButton(
                    text = "Beta tier",
                    checked = selectedTierState.value == "Beta",
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedTierState.value = "Beta"
                        }
                    },
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                )
                Text(
                    text = "Intensity: ${intensityState.value}",
                    modifier = Modifier.Empty.padding(bottom = 6.dp),
                )
                Slider(
                    value = intensityState.value,
                    min = 0,
                    max = 100,
                    onValueChange = { intensityState.value = it },
                    modifier = Modifier.Empty.fillMaxWidth(),
                )
                UiThemeOverride(
                    components = {
                        copy(
                            checkbox = checkbox.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                                label = Theme.colors.textPrimary,
                                labelDisabled = Theme.colors.textSecondary,
                            ),
                            switchControl = switchControl.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                                label = Theme.colors.textPrimary,
                                labelDisabled = Theme.colors.textSecondary,
                            ),
                            radioButton = radioButton.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                                label = Theme.colors.textPrimary,
                                labelDisabled = Theme.colors.textSecondary,
                            ),
                            slider = slider.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                            ),
                        )
                    },
                ) {
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .cornerRadius(SurfaceDefaults.cardCornerRadius())
                            .padding(12.dp),
                    ) {
                        Text(text = "Input Control Override")
                        Checkbox(
                            text = "Local Accent Checkbox",
                            checked = true,
                            onCheckedChange = {},
                        )
                        Switch(
                            text = "Disabled Accent Switch",
                            checked = false,
                            enabled = false,
                            onCheckedChange = {},
                        )
                        RadioButton(
                            text = "Local Accent Radio",
                            checked = true,
                            onCheckedChange = {},
                        )
                        Slider(
                            value = 56,
                            min = 0,
                            max = 100,
                            enabled = false,
                            onValueChange = {},
                            modifier = Modifier.Empty.fillMaxWidth(),
                        )
                    }
                }
            }

            "summary" -> DemoSection(
                title = "Derived Summary",
                subtitle = "This section is driven by `derivedStateOf`, not duplicated imperative updates.",
            ) {
                Text(text = summaryState.value)
                Text(
                    text = "Notifications=${notificationsEnabledState.value}, " +
                        "Analytics=${analyticsEnabledState.value}, " +
                        "Tier=${selectedTierState.value}, " +
                        "Intensity=${intensityState.value}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                )
                Text(
                    text = bioState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            else -> VerificationNotesSection(
                what = "Input chapter should prove that value, enabled/error state, component variants, and local overrides stay in sync with the runtime.",
                howToVerify = listOf(
                    "输入文本并点击 Reset Form，确认所有字段一起回到初始值。",
                    "观察空密码时的错误态，并切换 theme mode，确认错误色和容器色同步变化。",
                    "切换 selection controls 和 slider，确认摘要区立即反映变化。",
                ),
                expected = listOf(
                    "TextField label、supportingText、placeholder 和内容布局稳定。",
                    "禁用态和错误态不会丢失主题样式。",
                    "derived summary 始终和输入状态保持一致。",
                ),
                relatedGaps = listOf(
                    "还没有 focus 管理、IME 回调链和表单状态抽象。",
                ),
            )
        }
    }
}

private fun UiTreeBuilder.StatePage() {
    val clickCountState = remember { mutableStateOf(0) }
    val panelVisibleState = remember { mutableStateOf(true) }
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

    LazyColumn(
        items = listOf("page", "counter", "panel", "verify"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "State & Effects",
                goal = "Exercise the runtime primitives directly so remember, derived state, produced values, and keyed identity can be inspected by hand.",
                modules = listOf("ui-runtime", "remember", "effects", "key scopes"),
            )

            "counter" -> DemoSection(
                title = "remember + derivedStateOf + produceState",
                subtitle = "This block shows local state, derived labels, and a small produced status string.",
            ) {
                Text(text = "Clicks: ${clickCountState.value}")
                Text(
                    text = summaryState.value,
                    modifier = Modifier.Empty
                        .textColor(TextDefaults.secondaryColor())
                        .padding(vertical = 4.dp),
                )
                Text(
                    text = timelineState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty.margin(top = 12.dp),
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
                    modifier = Modifier.Empty.margin(bottom = 12.dp),
                    onClick = {
                        panelVisibleState.value = !panelVisibleState.value
                    },
                )
                Text(
                    text = "Visibility sample: hidden when the panel is off",
                    modifier = Modifier.Empty
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
                            modifier = Modifier.Empty
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

private fun UiTreeBuilder.CollectionPage() {
    val reversedState = remember { mutableStateOf(false) }
    val alternateLabelsState = remember { mutableStateOf(false) }
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

    LazyColumn(
        items = listOf("page", "controls", "interop", "list", "verify"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Collections",
                goal = "Validate keyed list reuse, local item state preservation, and AndroidView coexistence inside lazy containers.",
                modules = listOf("LazyColumn", "diff", "lazy item sessions", "AndroidView"),
            )

            "controls" -> DemoSection(
                title = "Collection Controls",
                subtitle = "These buttons mutate the source list and labels while preserving keyed item state.",
            ) {
                Text(text = listOrderState.value)
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty.margin(top = 12.dp),
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

            "interop" -> DemoSection(
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
                    modifier = Modifier.Empty.padding(vertical = 4.dp),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        (view as TextView).text = summaryText
                    },
                )
            }

            "list" -> DemoSection(
                title = "LazyColumn",
                subtitle = "Each item keeps its own local state while keyed reorder and content updates pass through the diff layer.",
            ) {
                LazyColumn(
                    items = keyedItems,
                    key = { item -> item.id },
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(280.dp),
                ) { item ->
                    val itemCountState = remember { mutableStateOf(0) }
                    Column(
                        key = item.id,
                        spacing = 6.dp,
                        modifier = Modifier.Empty
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

            else -> VerificationNotesSection(
                what = "Collections should reveal list diff bugs, state leakage between items, and local propagation problems in nested sessions.",
                howToVerify = listOf(
                    "对单个 item 连续点击计数，再切换 A-B-C / C-B-A 顺序，确认同 key 的计数被保留。",
                    "切换 Alternate labels，确认标题变化但 item 本地状态不丢。",
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

private fun UiTreeBuilder.InteropPage() {
    val alternateLabelsState = remember { mutableStateOf(false) }
    LazyColumn(
        items = listOf("page", "basics", "theme_bridge", "why_it_matters", "verify"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Interop",
                goal = "Keep AndroidView as a first-class migration path and verify that native widgets still fit inside framework theme and state boundaries.",
                modules = listOf("AndroidView", "theme bridge", "local propagation"),
            )

            "basics" -> DemoSection(
                title = "AndroidView Basics",
                subtitle = "Legacy native views still mount inside the same declarative tree and can react to framework state.",
            ) {
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.Empty.margin(bottom = 12.dp),
                ) {
                    Button(
                        text = if (alternateLabelsState.value) "Show Primary Copy" else "Show Alternate Copy",
                        onClick = {
                            alternateLabelsState.value = !alternateLabelsState.value
                        },
                    )
                }
                AndroidView(
                    key = "interop_text_view",
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        val textView = view as TextView
                        textView.text = if (alternateLabelsState.value) {
                            "Native TextView mirror: alternate content enabled"
                        } else {
                            "Native TextView mirror: primary content enabled"
                        }
                    },
                )
            }

            "theme_bridge" -> DemoSection(
                title = "Theme Bridge",
                subtitle = "Interop pages should prove that framework theme locals and Android view defaults can coexist instead of fighting each other.",
            ) {
                Text(
                    text = "Current chapter keeps AndroidView inside the same theme container so text, spacing, and surrounding surfaces are still driven by UIFramework tokens.",
                )
                Text(
                    text = "Later this page should verify themed native widgets, custom view adapters, and fragment host containers.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "why_it_matters" -> DemoSection(
                title = "Why Interop Matters",
                subtitle = "Interop is one of the biggest practical advantages of this framework compared with a pure Compose rewrite path.",
            ) {
                Text(
                    text = "Use this chapter to validate gradual migration: existing custom views, business widgets, and SDK surfaces should still be mountable without rewriting them first.",
                )
            }

            else -> VerificationNotesSection(
                what = "Interop chapter should prove that old View widgets can stay mounted inside the declarative tree without losing state or theme alignment.",
                howToVerify = listOf(
                    "点击切换文案按钮，确认原生 TextView 文案实时更新。",
                    "切换全局 theme mode，确认原生 View 所在容器仍与框架主题协调。",
                    "反复切换章节并返回 Interop，确认 AndroidView 依旧可用。",
                ),
                expected = listOf(
                    "AndroidView update 会响应框架状态变化。",
                    "theme locals 和 Android theme bridge 可以共存。",
                    "Interop 章节不会因为延迟 session 丢失 local 上下文。",
                ),
                relatedGaps = listOf(
                    "还没有 custom view adapter catalog 和 Fragment host demo。",
                ),
            )
        }
    }
}

private fun UiTreeBuilder.DiagnosticsPage() {
    LazyColumn(
        items = listOf("page", "runtime", "renderer", "gaps", "verify"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Diagnostics",
                goal = "Turn the demo into a manual regression console for runtime locals, renderer patches, and visible framework gaps.",
                modules = listOf("debug logging", "theme locals", "renderer", "roadmap gaps"),
            )

            "runtime" -> DemoSection(
                title = "Runtime Snapshot",
                subtitle = "This chapter will become the manual inspection home for state invalidation, local propagation, and effect boundaries.",
            ) {
                Text(text = "Theme mode: ${Theme.colors.primary.toUInt().toString(16)} primary token active")
                Text(
                    text = "Environment: ${Environment.localeTags.firstOrNull() ?: "und"} · ${Environment.layoutDirection.name}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "renderer" -> DemoSection(
                title = "Renderer Hooks",
                subtitle = "The sample currently runs with debug logging enabled, but there is no visual inspector yet.",
            ) {
                Text(
                    text = "Near-term goal: expose render tree, patch summary, key warnings, and local snapshots in a more explicit diagnostics surface.",
                )
            }

            "gaps" -> DemoSection(
                title = "Known Gaps",
                subtitle = "These gaps are intentionally visible so the diagnostics chapter can guide future framework work.",
            ) {
                Text(text = "No visual render tree inspector")
                Text(text = "No patch timeline UI")
                Text(text = "No performance counters page")
            }

            else -> VerificationNotesSection(
                what = "Diagnostics should be the first place to check before assuming a visual bug belongs to a widget, layout, or runtime layer.",
                howToVerify = listOf(
                    "切换 theme mode 与章节，确认 runtime snapshot 始终反映当前 environment。",
                    "在出现渲染问题时，对照这里列出的 gaps 判断是已知缺口还是新回归。",
                    "结合日志观察 renderer 行为，并确认 diagnostics 页面描述与当前实现一致。",
                ),
                expected = listOf(
                    "该章节能快速告诉你当前框架还缺什么。",
                    "环境信息和主题信息不会在章节切换后失真。",
                    "Diagnostics 会持续作为后续 inspector 的落点。",
                ),
                relatedGaps = listOf(
                    "还没有 render tree、patch timeline 和性能面板可视化。",
                ),
            )
        }
    }
}

private fun UiTreeBuilder.ChapterPlaceholderPage(
    title: String,
    subtitle: String,
    plannedPages: List<String>,
    currentGaps: List<String>,
) {
    LazyColumn(
        items = listOf("intro", "planned", "gaps"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "intro" -> DemoSection(
                title = title,
                subtitle = subtitle,
            ) {
                Text(
                    text = "This chapter is intentionally present now so the demo structure stays stable while framework capability catches up.",
                )
            }

            "planned" -> DemoSection(
                title = "Planned Pages",
                subtitle = "These are the first page groups reserved for this chapter.",
            ) {
                plannedPages.forEach { page ->
                    Text(text = "• $page")
                }
            }

            else -> DemoSection(
                title = "Current Gaps",
                subtitle = "These gaps map directly to framework work that is still missing compared with Compose.",
            ) {
                currentGaps.forEach { gap ->
                    Text(text = "• $gap")
                }
            }
        }
    }
}

private fun UiTreeBuilder.DemoSection(
    title: String,
    subtitle: String,
    content: UiTreeBuilder.() -> Unit,
) {
    Surface(
        variant = SurfaceVariant.Default,
        modifier = Modifier.Empty
            .fillMaxWidth()
            .margin(bottom = 12.dp)
            .padding(16.dp),
    ) {
        Column(
            spacing = 8.dp,
            modifier = Modifier.Empty.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = UiTextStyle(fontSizeSp = 20.sp),
            )
            Text(
                text = subtitle,
                style = UiTextStyle(fontSizeSp = 13.sp),
                modifier = Modifier.Empty
                    .textColor(TextDefaults.secondaryColor())
                    .padding(bottom = 4.dp),
            )
            Divider()
            content()
        }
    }
}

private fun UiTreeBuilder.ChapterPageOverviewSection(
    title: String,
    goal: String,
    modules: List<String>,
) {
    DemoSection(
        title = title,
        subtitle = "This page defines the testing goal and the framework layers that should be touched during manual verification.",
    ) {
        Text(text = goal)
        ChecklistGroup(
            title = "Framework Modules",
            items = modules,
        )
    }
}

private fun UiTreeBuilder.ChapterPageFilterSection(
    pages: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
) {
    DemoSection(
        title = "Chapter Pages",
        subtitle = "Use the local page switcher to isolate one manual test path at a time within the current chapter.",
    ) {
        SegmentedControl(
            items = pages,
            selectedIndex = selectedIndex,
            onSelectionChange = onSelectionChange,
            modifier = Modifier.Empty.fillMaxWidth(),
        )
    }
}

private fun UiTreeBuilder.VerificationNotesSection(
    what: String,
    howToVerify: List<String>,
    expected: List<String>,
    relatedGaps: List<String> = emptyList(),
) {
    DemoSection(
        title = "Verification Notes",
        subtitle = "Use this block as the manual acceptance checklist for the current chapter page.",
    ) {
        Text(text = what)
        ChecklistGroup(
            title = "How To Verify",
            items = howToVerify,
        )
        ChecklistGroup(
            title = "Expected",
            items = expected,
        )
        if (relatedGaps.isNotEmpty()) {
            ChecklistGroup(
                title = "Related Gaps",
                items = relatedGaps,
            )
        }
    }
}

private fun UiTreeBuilder.ChecklistGroup(
    title: String,
    items: List<String>,
) {
    Column(
        spacing = 4.dp,
        modifier = Modifier.Empty.margin(top = 8.dp),
    ) {
        Text(
            text = title,
            style = UiTextStyle(fontSizeSp = 13.sp),
            modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
        )
        items.forEachIndexed { index, item ->
            Text(text = "${index + 1}. $item")
        }
    }
}

private data class DemoListItem(
    val id: String,
    val title: String,
)

private data class ThemeSwatch(
    val label: String,
    val color: Int,
)
