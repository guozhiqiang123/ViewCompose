package com.gzq.uiframework

import androidx.appcompat.app.AppCompatActivity

internal enum class DemoModuleStatus {
    Available,
    Planned,
}

internal data class DemoModule(
    val key: String,
    val title: String,
    val subtitle: String,
    val status: DemoModuleStatus,
    val activityClass: Class<out AppCompatActivity>? = null,
)

internal val DEMO_MODULES = listOf(
    DemoModule(
        key = "foundations",
        title = "Foundations",
        subtitle = "Text, surface, theme, media, buttons, and feedback primitives.",
        status = DemoModuleStatus.Available,
        activityClass = FoundationsActivity::class.java,
    ),
    DemoModule(
        key = "state",
        title = "State",
        subtitle = "remember, derived state, effects, key identity, and patch stress.",
        status = DemoModuleStatus.Available,
        activityClass = StateActivity::class.java,
    ),
    DemoModule(
        key = "layouts",
        title = "Layouts",
        subtitle = "Row, Column, Box, spacing, alignment, and layout edge cases.",
        status = DemoModuleStatus.Available,
        activityClass = LayoutsActivity::class.java,
    ),
    DemoModule(
        key = "input",
        title = "Input",
        subtitle = "Text fields, selection controls, disabled states, and form stress.",
        status = DemoModuleStatus.Available,
        activityClass = InputActivity::class.java,
    ),
    DemoModule(
        key = "collections",
        title = "Collections",
        subtitle = "LazyColumn, keyed reorder, item state, and collection stress paths.",
        status = DemoModuleStatus.Available,
        activityClass = CollectionsActivity::class.java,
    ),
    DemoModule(
        key = "interop",
        title = "Interop",
        subtitle = "AndroidView, themed native views, and framework interop boundaries.",
        status = DemoModuleStatus.Available,
        activityClass = InteropActivity::class.java,
    ),
    DemoModule(
        key = "diagnostics",
        title = "Diagnostics",
        subtitle = "Renderer snapshots, structure stats, warnings, and layout pass counters.",
        status = DemoModuleStatus.Available,
        activityClass = DiagnosticsActivity::class.java,
    ),
    DemoModule(
        key = "gestures",
        title = "Gestures",
        subtitle = "Click, drag, swipe, and nested gesture scenarios planned after input/runtime work.",
        status = DemoModuleStatus.Planned,
    ),
    DemoModule(
        key = "animation",
        title = "Animation",
        subtitle = "State-driven motion, transitions, and list animations planned after runtime stabilizes.",
        status = DemoModuleStatus.Planned,
    ),
    DemoModule(
        key = "graphics",
        title = "Graphics",
        subtitle = "Canvas, draw pipeline, gradients, and custom graphics primitives are still missing.",
        status = DemoModuleStatus.Planned,
    ),
    DemoModule(
        key = "navigation",
        title = "Navigation",
        subtitle = "Host integration and navigation model experiments will start after demo shell settles.",
        status = DemoModuleStatus.Planned,
    ),
)

internal val AVAILABLE_DEMO_MODULES = DEMO_MODULES.filter { it.status == DemoModuleStatus.Available }
internal val PLANNED_DEMO_MODULES = DEMO_MODULES.filter { it.status == DemoModuleStatus.Planned }

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
