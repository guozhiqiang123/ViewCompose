package com.viewcompose.widget.core

object ListItemDefaults {
    fun headlineColor(): Int = Theme.colors.textPrimary

    fun supportingColor(): Int = Theme.colors.textSecondary

    fun overlineColor(): Int = Theme.colors.textSecondary

    fun headlineStyle(): UiTextStyle = TextDefaults.bodyMediumStyle()

    fun supportingStyle(): UiTextStyle = TextDefaults.labelMediumStyle()

    fun overlineStyle(): UiTextStyle = UiTextStyle(
        fontSizeSp = 12.sp,
        letterSpacingEm = 0.04f,
    )

    fun minHeight(): Int = 56.dp

    fun horizontalPadding(): Int = 16.dp

    fun verticalPadding(): Int = 8.dp

    fun leadingTrailingSpacing(): Int = 16.dp

    fun textSpacing(): Int = 2.dp
}
