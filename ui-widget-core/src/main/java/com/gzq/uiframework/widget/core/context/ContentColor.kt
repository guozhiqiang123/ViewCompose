package com.gzq.uiframework.widget.core

private val LocalContentColor = LocalValue { Theme.colors.textPrimary }

object ContentColor {
    val current: Int
        get() = LocalContext.current(LocalContentColor)
}

fun UiTreeBuilder.ProvideContentColor(
    color: Int,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalContentColor, color) {
        content()
    }
}
