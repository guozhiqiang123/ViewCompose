package com.viewcompose.widget.core

object AlertDialogDefaults {
    fun containerColor(): Int = Theme.colors.surface

    fun titleColor(): Int = Theme.colors.textPrimary

    fun textColor(): Int = Theme.colors.textSecondary

    fun iconTint(): Int = Theme.colors.primary

    fun titleStyle(): UiTextStyle = Theme.typography.title

    fun textStyle(): UiTextStyle = Theme.typography.body

    fun cornerRadius(): Int = Theme.shapes.cardCornerRadius

    fun contentPadding(): Int = 24.dp

    fun titleToTextSpacing(): Int = 16.dp

    fun textToButtonsSpacing(): Int = 24.dp

    fun buttonSpacing(): Int = 8.dp

    fun iconBottomSpacing(): Int = 16.dp

    fun iconSize(): Int = 24.dp

    fun minWidth(): Int = 280.dp
}
