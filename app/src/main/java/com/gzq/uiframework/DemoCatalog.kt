package com.gzq.uiframework

internal data class DemoChapter(
    val key: String,
    val title: String,
)

internal val DEMO_CHAPTERS = listOf(
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

internal const val CHAPTER_FOUNDATIONS = 0
internal const val CHAPTER_STATE = 1
internal const val CHAPTER_LAYOUTS = 2
internal const val CHAPTER_INPUT = 3
internal const val CHAPTER_COLLECTIONS = 4
internal const val CHAPTER_GESTURES = 5
internal const val CHAPTER_ANIMATION = 6
internal const val CHAPTER_GRAPHICS = 7
internal const val CHAPTER_NAVIGATION = 8
internal const val CHAPTER_INTEROP = 9
internal const val CHAPTER_DIAGNOSTICS = 10

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
