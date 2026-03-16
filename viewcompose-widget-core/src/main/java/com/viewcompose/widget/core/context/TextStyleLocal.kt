package com.viewcompose.widget.core

val LocalTextStyle = uiLocalOf { Theme.typography.bodyMedium }

object TextStyle {
    val current: UiTextStyle
        get() = UiLocals.current(LocalTextStyle)
}
