package com.viewcompose.widget.core

object TopAppBarDefaults {
    fun containerColor(): Int = Theme.colors.surface

    fun titleColor(): Int = Theme.colors.textPrimary

    fun titleStyle(): UiTextStyle = TextDefaults.titleMediumStyle()

    fun height(): Int = Theme.controls.appBar.topHeight

    fun horizontalPadding(): Int = Theme.controls.appBar.topHorizontalPadding

    fun titleStartPadding(): Int = Theme.controls.appBar.topTitleStartPadding
}
