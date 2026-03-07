package com.viewcompose.widget.core

object BadgeDefaults {
    fun containerColor(): Int = Theme.colors.error

    fun contentColor(): Int = contentColorFor(Theme.colors.error)

    fun dotSize(): Int = 8.dp

    fun pillHeight(): Int = 16.dp

    fun pillMinWidth(): Int = 16.dp

    fun pillHorizontalPadding(): Int = 4.dp

    fun textStyle(): UiTextStyle = UiTextStyle(fontSizeSp = 11.sp)
}
