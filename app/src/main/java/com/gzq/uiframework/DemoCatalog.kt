package com.gzq.uiframework

import androidx.appcompat.app.AppCompatActivity

internal enum class DemoModuleStatus {
    Available,
    Planned,
}

internal const val EXTRA_DEMO_MODULE_KEY = "demo_module_key"

internal data class DemoModule(
    val key: String,
    val title: String,
    val subtitle: String,
    val status: DemoModuleStatus,
    val manualFocus: String,
    val benchmarkPath: String,
    val activityClass: Class<out AppCompatActivity>? = null,
)

internal val DEMO_MODULES = listOf(
    DemoModule(
        key = "widget_showcase",
        title = "控件展示",
        subtitle = "所有基础控件的 Props 样式展示，按类别分组，逐项演示每个属性的视觉效果。",
        status = DemoModuleStatus.Available,
        manualFocus = "控件 Props 全覆盖、变体对比、启用/禁用态",
        benchmarkPath = "Catalog -> Open 控件展示 -> 选择控件",
        activityClass = WidgetShowcaseActivity::class.java,
    ),
    DemoModule(
        key = "foundations",
        title = "Foundations",
        subtitle = "Text, surface, theme, media, buttons, and feedback primitives.",
        status = DemoModuleStatus.Available,
        manualFocus = "theme tokens, surface/content color, media fallback, visual defaults",
        benchmarkPath = "Catalog -> Open Foundations -> Guide/Theme/Media pages",
        activityClass = FoundationsActivity::class.java,
    ),
    DemoModule(
        key = "state",
        title = "State",
        subtitle = "remember, derived state, effects, key identity, and patch stress.",
        status = DemoModuleStatus.Available,
        manualFocus = "state invalidation, key identity, patch-active updates",
        benchmarkPath = "Catalog -> Open State -> State Benchmark Anchor / Patch page",
        activityClass = StateActivity::class.java,
    ),
    DemoModule(
        key = "layouts",
        title = "Layouts",
        subtitle = "Row, Column, Box, spacing, alignment, and layout edge cases.",
        status = DemoModuleStatus.Available,
        manualFocus = "measure/layout stability, wrap vs weight, child alignment",
        benchmarkPath = "Catalog -> Open Layouts -> Linear/Edges pages",
        activityClass = LayoutsActivity::class.java,
    ),
    DemoModule(
        key = "input",
        title = "Input",
        subtitle = "Text fields, selection controls, disabled states, and form stress.",
        status = DemoModuleStatus.Available,
        manualFocus = "field chrome, multiline/error states, control theme defaults",
        benchmarkPath = "Catalog -> Open Input -> Fields/Stress pages",
        activityClass = InputActivity::class.java,
    ),
    DemoModule(
        key = "feedback",
        title = "Feedback",
        subtitle = "Snackbar, toast, and overlay-host-driven transient feedback paths.",
        status = DemoModuleStatus.Available,
        manualFocus = "transient overlay lifecycle, dismiss semantics, host presentation",
        benchmarkPath = "Catalog -> Open Feedback -> Transient Feedback Anchor",
        activityClass = FeedbackActivity::class.java,
    ),
    DemoModule(
        key = "collections",
        title = "Collections",
        subtitle = "LazyColumn, keyed reorder, item state, and collection stress paths.",
        status = DemoModuleStatus.Available,
        manualFocus = "key retention, lazy item session stability, interop in lists",
        benchmarkPath = "Catalog -> Open Collections -> Stress page",
        activityClass = CollectionsActivity::class.java,
    ),
    DemoModule(
        key = "interop",
        title = "Interop",
        subtitle = "AndroidView, themed native views, and framework interop boundaries.",
        status = DemoModuleStatus.Available,
        manualFocus = "local propagation, native view updates, theme bridge behavior",
        benchmarkPath = "Catalog -> Open Interop -> Interop Benchmark Anchor",
        activityClass = InteropActivity::class.java,
    ),
    DemoModule(
        key = "diagnostics",
        title = "Diagnostics",
        subtitle = "Renderer snapshots, structure stats, warnings, and layout pass counters.",
        status = DemoModuleStatus.Available,
        manualFocus = "render stats, patch snapshots, layout hot spots, warnings",
        benchmarkPath = "Catalog -> Open Diagnostics -> Diagnostics Benchmark Anchor / Renderer page",
        activityClass = DiagnosticsActivity::class.java,
    ),
    DemoModule(
        key = "actions",
        title = "Actions",
        subtitle = "Card, FAB, Chip, TextButton, ListItem, Badge 等 Action 类组件。",
        status = DemoModuleStatus.Available,
        manualFocus = "card variants, fab sizes, chip states, list item slots, badge display",
        benchmarkPath = "Catalog -> Open Actions -> Card/Chip benchmark anchor",
        activityClass = ActionsActivity::class.java,
    ),
    DemoModule(
        key = "modifiers",
        title = "Modifiers",
        subtitle = "elevation, border, clip, alpha, rippleColor, cornerRadius, 尺寸约束, 无障碍, nativeView。",
        status = DemoModuleStatus.Available,
        manualFocus = "elevation shadow, border stroke, clip overflow, alpha gradient, ripple color, corner radius cascade, size constraints, contentDescription, nativeView",
        benchmarkPath = "Catalog -> Open Modifiers -> Visual / Size pages",
        activityClass = ModifiersActivity::class.java,
    ),
    DemoModule(
        key = "gestures",
        title = "Gestures",
        subtitle = "Click, drag, swipe, and nested gesture scenarios planned after input/runtime work.",
        status = DemoModuleStatus.Planned,
        manualFocus = "pointer input and nested gesture conflicts",
        benchmarkPath = "Preview placeholder",
        activityClass = GesturesActivity::class.java,
    ),
    DemoModule(
        key = "animation",
        title = "Animation",
        subtitle = "State-driven motion, transitions, and list animations planned after runtime stabilizes.",
        status = DemoModuleStatus.Planned,
        manualFocus = "state transitions, content animation, list motion",
        benchmarkPath = "Preview placeholder",
        activityClass = AnimationActivity::class.java,
    ),
    DemoModule(
        key = "graphics",
        title = "Graphics",
        subtitle = "Canvas, draw pipeline, gradients, and custom graphics primitives are still missing.",
        status = DemoModuleStatus.Planned,
        manualFocus = "draw modifiers, canvas primitives, custom graphics",
        benchmarkPath = "Preview placeholder",
        activityClass = GraphicsActivity::class.java,
    ),
    DemoModule(
        key = "navigation",
        title = "Navigation",
        subtitle = "TopAppBar, BottomAppBar, NavigationBar, Scaffold 导航组件。",
        status = DemoModuleStatus.Available,
        manualFocus = "app bar slots, navigation bar selection, scaffold composition",
        benchmarkPath = "Catalog -> Open Navigation -> NavigationBar selection / Scaffold content",
        activityClass = NavigationActivity::class.java,
    ),
)

internal val AVAILABLE_DEMO_MODULES = DEMO_MODULES.filter { it.status == DemoModuleStatus.Available }
internal val PLANNED_DEMO_MODULES = DEMO_MODULES.filter { it.status == DemoModuleStatus.Planned }

internal fun findDemoModuleByKey(key: String): DemoModule? =
    DEMO_MODULES.firstOrNull { it.key == key }

internal fun findAvailableDemoModuleByKey(key: String): DemoModule? =
    AVAILABLE_DEMO_MODULES.firstOrNull { it.key == key }

internal data class DemoListItem(
    val id: String,
    val title: String,
)

internal data class ThemeSwatch(
    val label: String,
    val color: Int,
)

internal data class DiagnosticFact(
    val label: String,
    val value: String,
)
