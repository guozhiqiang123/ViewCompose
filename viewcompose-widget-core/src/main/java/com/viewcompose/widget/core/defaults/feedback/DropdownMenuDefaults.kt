package com.viewcompose.widget.core

object DropdownMenuDefaults {
    fun containerColor(): Int = Theme.colors.surface

    fun contentColor(): Int = Theme.colors.textPrimary

    fun textStyle(): UiTextStyle = Theme.typography.body

    fun cornerRadius(): Int = 4.dp

    fun elevation(): Int = 3.dp

    fun minWidth(): Int = 112.dp

    fun verticalPadding(): Int = 8.dp

    fun itemHeight(): Int = 48.dp

    fun itemHorizontalPadding(): Int = 12.dp

    fun iconSize(): Int = 24.dp

    fun iconToTextSpacing(): Int = 12.dp

    fun trailingTextColor(): Int = Theme.colors.textSecondary

    fun disabledAlpha(): Float = 0.38f
}
