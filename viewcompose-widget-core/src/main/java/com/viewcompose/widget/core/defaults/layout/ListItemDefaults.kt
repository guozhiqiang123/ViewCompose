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

    fun minHeight(): Int = Theme.controls.listItem.minHeight

    fun horizontalPadding(): Int = Theme.controls.listItem.horizontalPadding

    fun verticalPadding(): Int = Theme.controls.listItem.verticalPadding

    fun leadingTrailingSpacing(): Int = Theme.controls.listItem.leadingTrailingSpacing

    fun textSpacing(): Int = Theme.controls.listItem.textSpacing
}
