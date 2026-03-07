package com.viewcompose.widget.core

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

    fun checkboxCheckedColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalCheckboxColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun checkboxUncheckedColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalCheckboxColors)
        return if (enabled) {
            Theme.colors.surfaceVariant
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

    fun switchThumbColor(checked: Boolean = true, enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSwitchColors)
        return when {
            !enabled -> override?.controlDisabled ?: Theme.colors.divider
            checked -> override?.control ?: Theme.colors.primary
            else -> Theme.colors.surfaceVariant
        }
    }

    fun switchTrackColor(checked: Boolean = true, enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSwitchColors)
        return when {
            !enabled -> override?.controlDisabled ?: Theme.colors.divider
            checked -> {
                val base = override?.control ?: Theme.colors.primary
                (base and 0x00FFFFFF) or 0x61000000
            }
            else -> Theme.colors.surfaceVariant
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

    fun radioButtonCheckedColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun radioButtonUncheckedColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalRadioButtonColors)
        return if (enabled) {
            Theme.colors.surfaceVariant
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

    fun sliderThumbColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSliderColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun sliderTrackColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSliderColors)
        return if (enabled) {
            val base = override?.control ?: Theme.colors.primary
            (base and 0x00FFFFFF) or 0x61000000
        } else {
            override?.controlDisabled ?: Theme.colors.divider
        }
    }

    fun pressedColor(): Int = Theme.colors.ripple
}
