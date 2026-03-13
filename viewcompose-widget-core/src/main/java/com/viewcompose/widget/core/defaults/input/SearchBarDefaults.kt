package com.viewcompose.widget.core

object SearchBarDefaults {
    fun containerColor(): Int = Theme.colors.surfaceVariant

    fun contentColor(): Int = Theme.colors.textPrimary

    fun placeholderColor(): Int = Theme.colors.textSecondary

    fun iconColor(): Int = Theme.colors.textSecondary

    fun height(): Int = Theme.controls.searchBar.height

    fun cornerRadius(): Int = 28.dp

    fun horizontalPadding(): Int = Theme.controls.searchBar.horizontalPadding

    fun iconSize(): Int = Theme.controls.searchBar.iconSize

    fun iconSpacing(): Int = Theme.controls.searchBar.iconSpacing

    fun textStyle(): UiTextStyle = Theme.typography.body

    fun elevation(): Int = Theme.controls.searchBar.elevation
}
