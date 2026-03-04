package com.gzq.uiframework.widget.core

object ProgressIndicatorDefaults {
    fun linearIndicatorColor(): Int {
        val override = LocalContext.current(LocalProgressIndicatorColors)
        return override?.linearIndicator ?: Theme.colors.primary
    }

    fun linearTrackColor(): Int {
        val override = LocalContext.current(LocalProgressIndicatorColors)
        return override?.linearTrack ?: Theme.colors.divider
    }

    fun linearTrackThickness(): Int = Theme.controls.progressIndicator.linearTrackThickness

    fun circularIndicatorColor(): Int {
        val override = LocalContext.current(LocalProgressIndicatorColors)
        return override?.circularIndicator ?: Theme.colors.primary
    }

    fun circularTrackColor(): Int {
        val override = LocalContext.current(LocalProgressIndicatorColors)
        return override?.circularTrack ?: Theme.colors.divider
    }

    fun circularSize(): Int = Theme.controls.progressIndicator.circularSize

    fun circularTrackThickness(): Int = Theme.controls.progressIndicator.circularTrackThickness
}
