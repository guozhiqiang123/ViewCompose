package com.viewcompose.widget.core

val LocalTextStyle = uiLocalOf { Theme.typography.body }

object TextStyle {
    val current: UiTextStyle
        get() = UiLocals.current(LocalTextStyle)
}
