package com.viewcompose.widget.core

object ProgressIndicatorDefaults {
    fun linearIndicatorColor(): Int {
        val override = UiLocals.current(LocalProgressIndicatorColors)
        return override?.linearIndicator ?: Theme.colors.primary
    }

    fun linearTrackColor(): Int {
        val override = UiLocals.current(LocalProgressIndicatorColors)
        return override?.linearTrack ?: Theme.colors.outlineVariant
    }

    fun linearTrackThickness(): Int = Theme.controls.progressIndicator.linearTrackThickness

    fun circularIndicatorColor(): Int {
        val override = UiLocals.current(LocalProgressIndicatorColors)
        return override?.circularIndicator ?: Theme.colors.primary
    }

    fun circularTrackColor(): Int {
        val override = UiLocals.current(LocalProgressIndicatorColors)
        return override?.circularTrack ?: Theme.colors.outlineVariant
    }

    fun circularSize(): Int = Theme.controls.progressIndicator.circularSize

    fun circularTrackThickness(): Int = Theme.controls.progressIndicator.circularTrackThickness
}
