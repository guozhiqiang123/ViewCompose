package com.gzq.uiframework.widget.core

object InputControlDefaults {
    fun labelStyle(): UiTextStyle = TextDefaults.bodyStyle()

    fun checkboxLabelColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalCheckboxColors)
        return if (enabled) {
            override?.label ?: Theme.input.fieldText
        } else {
            override?.labelDisabled ?: Theme.input.fieldTextDisabled
        }
    }

    fun checkboxControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalCheckboxColors)
        return if (enabled) {
            override?.control ?: Theme.input.control
        } else {
            override?.controlDisabled ?: Theme.input.controlDisabled
        }
    }

    fun switchLabelColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSwitchColors)
        return if (enabled) {
            override?.label ?: Theme.input.fieldText
        } else {
            override?.labelDisabled ?: Theme.input.fieldTextDisabled
        }
    }

    fun switchControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSwitchColors)
        return if (enabled) {
            override?.control ?: Theme.input.control
        } else {
            override?.controlDisabled ?: Theme.input.controlDisabled
        }
    }

    fun radioButtonLabelColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.label ?: Theme.input.fieldText
        } else {
            override?.labelDisabled ?: Theme.input.fieldTextDisabled
        }
    }

    fun radioButtonControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.control ?: Theme.input.control
        } else {
            override?.controlDisabled ?: Theme.input.controlDisabled
        }
    }

    fun sliderControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSliderColors)
        return if (enabled) {
            override?.control ?: Theme.input.control
        } else {
            override?.controlDisabled ?: Theme.input.controlDisabled
        }
    }

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
