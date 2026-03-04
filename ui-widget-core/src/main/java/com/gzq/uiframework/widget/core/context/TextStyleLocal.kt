package com.gzq.uiframework.widget.core

private val LocalTextStyle = LocalValue { Theme.typography.body }

object TextStyle {
    val current: UiTextStyle
        get() = LocalContext.current(LocalTextStyle)
}

fun UiTreeBuilder.ProvideTextStyle(
    style: UiTextStyle,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalTextStyle, style) {
        content()
    }
}
