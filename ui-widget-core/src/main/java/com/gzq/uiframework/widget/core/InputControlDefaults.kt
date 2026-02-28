package com.gzq.uiframework.widget.core

object InputControlDefaults {
    fun labelStyle(): UiTextStyle = TextDefaults.bodyStyle()

    fun labelColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.components.inputControl.label
        } else {
            Theme.components.inputControl.labelDisabled
        }
    }

    fun controlColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.components.inputControl.control
        } else {
            Theme.components.inputControl.controlDisabled
        }
    }

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
