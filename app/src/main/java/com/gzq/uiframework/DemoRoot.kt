package com.gzq.uiframework

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.gzq.uiframework.image.coil.CoilRemoteImageLoader
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.textColor
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.runtime.MutableState
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.ColumnScope
import com.gzq.uiframework.widget.core.DisposableEffect
import com.gzq.uiframework.widget.core.Environment
import com.gzq.uiframework.widget.core.ProvideRemoteImageLoader
import com.gzq.uiframework.widget.core.SegmentedControl
import com.gzq.uiframework.widget.core.SegmentedControlSize
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.TabPager
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiEnvironment
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTheme
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp
import java.util.Locale

internal fun UiTreeBuilder.DemoRoot(root: ViewGroup) {
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
                    modifier = Modifier
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
                        color = TextDefaults.secondaryColor(),
                    )
                    Text(
                        text = environmentLabel,
                        style = UiTextStyle(fontSizeSp = 12.sp),
                        color = TextDefaults.secondaryColor(),
                        modifier = Modifier
                            .padding(vertical = 4.dp),
                    )
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier
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
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    DemoChapterPager(selectedChapterState = selectedChapterState)
                }
            }
        }
    }
}

private fun ColumnScope.DemoChapterPager(
    selectedChapterState: MutableState<Int>,
) {
    TabPager(
        selectedTabIndex = selectedChapterState.value,
        onTabSelected = { index ->
            selectedChapterState.value = index
        },
        modifier = Modifier
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
