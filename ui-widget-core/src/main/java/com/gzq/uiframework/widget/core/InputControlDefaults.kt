package com.gzq.uiframework.widget.core

object InputControlDefaults {
    fun labelStyle(): UiTextStyle = TextDefaults.bodyStyle()

    fun labelColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.input.fieldText
        } else {
            Theme.input.fieldTextDisabled
        }
    }

    fun controlColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.input.control
        } else {
            Theme.input.controlDisabled
        }
    }

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
