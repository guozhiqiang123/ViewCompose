package com.viewcompose.widget.core

enum class TextFieldVariant {
    Filled,
    Tonal,
    Outlined,
}

enum class TextFieldSize {
    Compact,
    Medium,
    Large,
}

object TextFieldDefaults {
    fun textStyle(
        size: TextFieldSize = TextFieldSize.Medium,
    ): UiTextStyle {
        return when (size) {
            TextFieldSize.Compact -> TextDefaults.labelSmallStyle()
            TextFieldSize.Medium -> TextDefaults.bodyMediumStyle()
            TextFieldSize.Large -> TextDefaults.bodyLargeStyle()
        }
    }

    fun textColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.colors.textPrimary
        } else {
            Theme.colors.textSecondary
        }
    }

    fun hintColor(
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int {
        return when {
            isError -> Theme.colors.error
            enabled -> Theme.colors.textSecondary
            else -> Theme.colors.divider
        }
    }

    fun labelColor(
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int = hintColor(enabled = enabled, isError = isError)

    fun supportingTextColor(
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int = hintColor(enabled = enabled, isError = isError)

    fun labelTextStyle(): UiTextStyle = TextDefaults.labelMediumStyle()

    fun supportingTextStyle(): UiTextStyle = TextDefaults.labelMediumStyle()

    fun containerColor(
        variant: TextFieldVariant = TextFieldVariant.Filled,
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int {
        val override = LocalContext.current(LocalTextFieldColors)
        return when {
            variant == TextFieldVariant.Outlined -> 0x00000000
            isError && variant == TextFieldVariant.Tonal ->
                override?.tonalErrorContainer ?: Theme.colors.error

            isError ->
                override?.filledErrorContainer ?: Theme.colors.error

            variant == TextFieldVariant.Tonal && enabled ->
                override?.tonalContainer ?: Theme.colors.surfaceVariant

            variant == TextFieldVariant.Tonal ->
                override?.tonalDisabledContainer ?: Theme.colors.surfaceVariant

            enabled ->
                override?.filledContainer ?: Theme.colors.surface

            else ->
                override?.filledDisabledContainer ?: Theme.colors.surfaceVariant
        }
    }

    fun borderColor(
        variant: TextFieldVariant = TextFieldVariant.Filled,
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int {
        val override = LocalContext.current(LocalTextFieldColors)
        return when {
            isError ->
                override?.outlinedErrorBorder ?: Theme.colors.error

            variant == TextFieldVariant.Outlined && enabled ->
                override?.outlinedBorder ?: Theme.colors.primary

            variant == TextFieldVariant.Outlined ->
                override?.outlinedDisabledBorder ?: Theme.colors.divider

            else -> 0x00000000
        }
    }

    fun borderWidth(
        variant: TextFieldVariant = TextFieldVariant.Filled,
    ): Int {
        return when (variant) {
            TextFieldVariant.Outlined -> 1.dp
            else -> 0
        }
    }

    fun cornerRadius(): Int = Theme.shapes.interactiveCornerRadius

    fun height(
        size: TextFieldSize = TextFieldSize.Medium,
    ): Int {
        return when (size) {
            TextFieldSize.Compact -> Theme.controls.textField.compactHeight
            TextFieldSize.Medium -> Theme.controls.textField.mediumHeight
            TextFieldSize.Large -> Theme.controls.textField.largeHeight
        }
    }

    fun horizontalPadding(
        size: TextFieldSize = TextFieldSize.Medium,
    ): Int {
        return when (size) {
            TextFieldSize.Compact -> Theme.controls.textField.compactHorizontalPadding
            TextFieldSize.Medium -> Theme.controls.textField.mediumHorizontalPadding
            TextFieldSize.Large -> Theme.controls.textField.largeHorizontalPadding
        }
    }

    fun verticalPadding(
        size: TextFieldSize = TextFieldSize.Medium,
    ): Int {
        return when (size) {
            TextFieldSize.Compact -> Theme.controls.textField.compactVerticalPadding
            TextFieldSize.Medium -> Theme.controls.textField.mediumVerticalPadding
            TextFieldSize.Large -> Theme.controls.textField.largeVerticalPadding
        }
    }

    fun pressedColor(): Int = Theme.colors.ripple

    fun cursorColor(): Int = Theme.colors.primary
}
