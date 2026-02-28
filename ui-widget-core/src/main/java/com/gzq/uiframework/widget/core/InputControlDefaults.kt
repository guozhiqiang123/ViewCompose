package com.gzq.uiframework.widget.core

object InputControlDefaults {
    fun labelStyle(): UiTextStyle = TextDefaults.bodyStyle()

    private fun labelColor(
        enabled: Boolean,
        label: Int,
        labelDisabled: Int,
    ): Int {
        return if (enabled) {
            label
        } else {
            labelDisabled
        }
    }

    private fun controlColor(
        enabled: Boolean,
        control: Int,
        controlDisabled: Int,
    ): Int {
        return if (enabled) {
            control
        } else {
            controlDisabled
        }
    }

    fun checkboxLabelColor(enabled: Boolean = true): Int {
        return labelColor(
            enabled = enabled,
            label = Theme.components.checkbox.label,
            labelDisabled = Theme.components.checkbox.labelDisabled,
        )
    }

    fun checkboxControlColor(enabled: Boolean = true): Int {
        return controlColor(
            enabled = enabled,
            control = Theme.components.checkbox.control,
            controlDisabled = Theme.components.checkbox.controlDisabled,
        )
    }

    fun switchLabelColor(enabled: Boolean = true): Int {
        return labelColor(
            enabled = enabled,
            label = Theme.components.switchControl.label,
            labelDisabled = Theme.components.switchControl.labelDisabled,
        )
    }

    fun switchControlColor(enabled: Boolean = true): Int {
        return controlColor(
            enabled = enabled,
            control = Theme.components.switchControl.control,
            controlDisabled = Theme.components.switchControl.controlDisabled,
        )
    }

    fun radioButtonLabelColor(enabled: Boolean = true): Int {
        return labelColor(
            enabled = enabled,
            label = Theme.components.radioButton.label,
            labelDisabled = Theme.components.radioButton.labelDisabled,
        )
    }

    fun radioButtonControlColor(enabled: Boolean = true): Int {
        return controlColor(
            enabled = enabled,
            control = Theme.components.radioButton.control,
            controlDisabled = Theme.components.radioButton.controlDisabled,
        )
    }

    fun sliderControlColor(enabled: Boolean = true): Int {
        return controlColor(
            enabled = enabled,
            control = Theme.components.slider.control,
            controlDisabled = Theme.components.slider.controlDisabled,
        )
    }

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
