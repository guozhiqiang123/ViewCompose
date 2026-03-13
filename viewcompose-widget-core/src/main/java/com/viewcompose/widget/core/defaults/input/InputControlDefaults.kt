package com.viewcompose.widget.core

object InputControlDefaults {
    fun labelStyle(): UiTextStyle = TextDefaults.bodyStyle()

    fun checkboxLabelColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalCheckboxColors)
        return if (enabled) {
            override?.label ?: Theme.colors.onSurface
        } else {
            override?.labelDisabled ?: Theme.colors.onSurfaceVariant
        }
    }

    fun checkboxControlColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalCheckboxColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun checkboxCheckedColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalCheckboxColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun checkboxUncheckedColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalCheckboxColors)
        return if (enabled) {
            Theme.colors.outline
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun switchLabelColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalSwitchColors)
        return if (enabled) {
            override?.label ?: Theme.colors.onSurface
        } else {
            override?.labelDisabled ?: Theme.colors.onSurfaceVariant
        }
    }

    fun switchControlColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalSwitchColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun switchThumbColor(checked: Boolean = true, enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalSwitchColors)
        return when {
            !enabled -> override?.controlDisabled ?: Theme.colors.outlineVariant
            checked -> override?.control ?: Theme.colors.primary
            else -> Theme.colors.outline
        }
    }

    fun switchTrackColor(checked: Boolean = true, enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalSwitchColors)
        return when {
            !enabled -> override?.controlDisabled ?: Theme.colors.outlineVariant
            checked -> {
                val base = override?.control ?: Theme.colors.primary
                (base and 0x00FFFFFF) or 0x61000000
            }
            else -> Theme.colors.outlineVariant
        }
    }

    fun radioButtonLabelColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.label ?: Theme.colors.onSurface
        } else {
            override?.labelDisabled ?: Theme.colors.onSurfaceVariant
        }
    }

    fun radioButtonControlColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun radioButtonCheckedColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalRadioButtonColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun radioButtonUncheckedColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalRadioButtonColors)
        return if (enabled) {
            Theme.colors.outline
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun sliderControlColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalSliderColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun sliderThumbColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalSliderColors)
        return if (enabled) {
            override?.control ?: Theme.colors.primary
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun sliderTrackColor(enabled: Boolean = true): Int {
        val override = UiLocals.current(LocalSliderColors)
        return if (enabled) {
            val base = override?.control ?: Theme.colors.primary
            (base and 0x00FFFFFF) or 0x61000000
        } else {
            override?.controlDisabled ?: Theme.colors.outlineVariant
        }
    }

    fun pressedColor(): Int = Theme.colors.ripple
}
