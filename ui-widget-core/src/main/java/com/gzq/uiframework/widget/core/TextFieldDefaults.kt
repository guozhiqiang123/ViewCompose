package com.gzq.uiframework.widget.core

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
        enabled: Boolean = true,
        isError: Boolean = false,
    ): Int {
        return when {
            isError -> Theme.input.fieldError
            enabled -> Theme.input.fieldContainer
            else -> Theme.input.fieldContainerDisabled
        }
    }
}
