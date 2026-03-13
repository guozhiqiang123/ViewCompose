package com.viewcompose.widget.core

object DropdownMenuDefaults {
    fun containerColor(): Int = Theme.colors.surface

    fun contentColor(): Int = Theme.colors.textPrimary

    fun textStyle(): UiTextStyle = Theme.typography.body

    fun cornerRadius(): Int = 4.dp

    fun elevation(): Int = Theme.controls.menu.elevation

    fun minWidth(): Int = Theme.controls.menu.minWidth

    fun verticalPadding(): Int = Theme.controls.menu.verticalPadding

    fun itemHeight(): Int = Theme.controls.menu.itemHeight

    fun itemHorizontalPadding(): Int = Theme.controls.menu.itemHorizontalPadding

    fun iconSize(): Int = Theme.controls.menu.iconSize

    fun iconToTextSpacing(): Int = Theme.controls.menu.iconToTextSpacing

    fun trailingTextColor(): Int = Theme.colors.textSecondary

    fun disabledAlpha(): Float = 0.38f
}
