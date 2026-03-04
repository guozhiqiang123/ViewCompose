package com.gzq.uiframework.widget.core

object InputControlDefaults {
    fun labelStyle(): UiTextStyle = TextDefaults.bodyStyle()

    fun checkboxLabelColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalCheckboxColors)
        return if (enabled) {
            override?.label ?: Theme.colors.textPrimary
        } else {
            override?.labelDisabled ?: Theme.colors.textSecondary
        }
    }

    fun checkboxControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalCheckboxColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun switchLabelColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSwitchColors)
        return if (enabled) {
            override?.label ?: Theme.colors.textPrimary
        } else {
            override?.labelDisabled ?: Theme.colors.textSecondary
        }
    }

    fun switchControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSwitchColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun radioButtonLabelColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.label ?: Theme.colors.textPrimary
        } else {
            override?.labelDisabled ?: Theme.colors.textSecondary
        }
    }

    fun radioButtonControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun sliderControlColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSliderColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun pressedColor(): Int = pressedOverlayColorFor(Theme.colors.textPrimary)
}
