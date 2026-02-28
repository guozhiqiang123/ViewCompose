package com.gzq.uiframework.widget.core

enum class TextFieldVariant {
    Filled,
    Tonal,
    Outlined,
}

object TextFieldDefaults {
    fun textStyle(): UiTextStyle = TextDefaults.bodyStyle()

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

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
