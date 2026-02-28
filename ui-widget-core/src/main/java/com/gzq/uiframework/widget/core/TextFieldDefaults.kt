package com.gzq.uiframework.widget.core

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
            TextFieldSize.Compact -> Theme.typography.label
            TextFieldSize.Medium,
            TextFieldSize.Large,
            -> TextDefaults.bodyStyle()
        }
    }

    fun textColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.input.fieldText
        } else {
            Theme.input.fieldTextDisabled
        }
    }

    fun hintColor(
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int {
        return when {
            isError -> Theme.input.fieldError
            enabled -> Theme.input.fieldHint
            else -> Theme.input.fieldHintDisabled
        }
    }

    fun containerColor(
        variant: TextFieldVariant = TextFieldVariant.Filled,
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int {
        return when {
            variant == TextFieldVariant.Outlined -> 0x00000000
            isError -> Theme.input.fieldError
            variant == TextFieldVariant.Tonal && enabled -> Theme.colors.surfaceVariant
            variant == TextFieldVariant.Tonal -> Theme.input.fieldContainerDisabled
            enabled -> Theme.input.fieldContainer
            else -> Theme.input.fieldContainerDisabled
        }
    }

    fun borderColor(
        variant: TextFieldVariant = TextFieldVariant.Filled,
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int {
        return when {
            isError -> Theme.input.fieldError
            variant == TextFieldVariant.Outlined && enabled -> Theme.input.control
            variant == TextFieldVariant.Outlined -> Theme.input.controlDisabled
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

    fun cornerRadius(): Int = Theme.shapes.controlCornerRadius

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

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
