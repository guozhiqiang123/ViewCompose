package com.viewcompose.widget.core

object BadgeDefaults {
    fun containerColor(): Int = Theme.colors.error

    fun contentColor(): Int = contentColorFor(Theme.colors.error)

    fun dotSize(): Int = Theme.controls.badge.dotSize

    fun pillHeight(): Int = Theme.controls.badge.pillHeight

    fun pillMinWidth(): Int = Theme.controls.badge.pillMinWidth

    fun pillHorizontalPadding(): Int = Theme.controls.badge.pillHorizontalPadding

    fun textStyle(): UiTextStyle = UiTextStyle(fontSizeSp = 11.sp)
}
