package com.viewcompose.widget.core

object TooltipDefaults {
    fun containerColor(): Int = 0xFF2C2621.toInt()

    fun contentColor(): Int = 0xFFF4EFE8.toInt()

    fun textStyle(): UiTextStyle = Theme.typography.label

    fun cornerRadius(): Int = 4.dp

    fun horizontalPadding(): Int = Theme.controls.tooltip.horizontalPadding

    fun verticalPadding(): Int = Theme.controls.tooltip.verticalPadding
}
