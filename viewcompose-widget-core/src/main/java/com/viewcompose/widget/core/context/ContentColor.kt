package com.viewcompose.widget.core

val LocalContentColor = uiLocalOf { Theme.colors.onSurface }

object ContentColor {
    val current: Int
        get() = UiLocals.current(LocalContentColor)
}
