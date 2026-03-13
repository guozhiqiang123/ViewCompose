package com.viewcompose.widget.core

object TooltipDefaults {
    fun containerColor(): Int = Theme.colors.inverseSurface

    fun contentColor(): Int = Theme.colors.inverseOnSurface

    fun textStyle(): UiTextStyle = TextDefaults.labelMediumStyle()

    fun cornerRadius(): Int = Theme.shapes.smallCornerRadius

    fun horizontalPadding(): Int = Theme.controls.tooltip.horizontalPadding

    fun verticalPadding(): Int = Theme.controls.tooltip.verticalPadding
}
