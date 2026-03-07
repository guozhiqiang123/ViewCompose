package com.viewcompose.widget.core

object SearchBarDefaults {
    fun containerColor(): Int = Theme.colors.surfaceVariant

    fun contentColor(): Int = Theme.colors.textPrimary

    fun placeholderColor(): Int = Theme.colors.textSecondary

    fun iconColor(): Int = Theme.colors.textSecondary

    fun height(): Int = 56.dp

    fun cornerRadius(): Int = 28.dp

    fun horizontalPadding(): Int = 16.dp

    fun iconSize(): Int = 24.dp

    fun iconSpacing(): Int = 16.dp

    fun textStyle(): UiTextStyle = Theme.typography.body

    fun elevation(): Int = 2.dp
}
