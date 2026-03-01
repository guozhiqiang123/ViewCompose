package com.gzq.uiframework.renderer.view.tree

import android.content.res.ColorStateList
import android.widget.ProgressBar
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlin.math.roundToInt

internal object FeedbackViewBinder {
    fun bindLinearProgressIndicator(
        view: LinearProgressIndicator,
        enabled: Boolean,
        progress: Float?,
        indicatorColor: Int,
        trackColor: Int,
        trackThickness: Int,
    ) {
        bindProgressIndicator(
            view = view,
            enabled = enabled,
            progress = progress,
            indicatorColor = indicatorColor,
            trackColor = trackColor,
            trackThickness = trackThickness,
        )
    }

    fun bindCircularProgressIndicator(
        view: CircularProgressIndicator,
        enabled: Boolean,
        progress: Float?,
        indicatorColor: Int,
        trackColor: Int,
        trackThickness: Int,
        indicatorSize: Int,
    ) {
        bindProgressIndicator(
            view = view,
            enabled = enabled,
            progress = progress,
            indicatorColor = indicatorColor,
            trackColor = trackColor,
            trackThickness = trackThickness,
        )
        view.indicatorSize = indicatorSize
    }

    private fun bindProgressIndicator(
        view: ProgressBar,
        enabled: Boolean,
        progress: Float?,
        indicatorColor: Int,
        trackColor: Int,
        trackThickness: Int,
    ) {
        val tint = ColorStateList.valueOf(indicatorColor)
        view.isEnabled = enabled
        view.isIndeterminate = progress == null
        view.progressTintList = tint
        view.indeterminateTintList = tint

        if (view is BaseProgressIndicator<*>) {
            view.trackColor = trackColor
            view.trackThickness = trackThickness
            view.setIndicatorColor(indicatorColor)
        } else {
            view.progressBackgroundTintList = ColorStateList.valueOf(trackColor)
        }

        if (progress != null) {
            view.max = 10_000
            view.progress = (progress.coerceIn(0f, 1f) * 10_000f).roundToInt()
        }
    }
}
