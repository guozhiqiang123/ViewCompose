package com.viewcompose.renderer.view.tree

import android.content.res.ColorStateList
import android.widget.ProgressBar
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.ProgressIndicatorNodeProps
import kotlin.math.roundToInt

internal object FeedbackViewBinder {
    data class ProgressSpec(
        val enabled: Boolean,
        val progress: Float?,
        val indicatorColor: Int,
        val trackColor: Int,
        val trackThickness: Int,
        val indicatorSize: Int,
    )

    fun bindLinearProgressIndicator(
        view: LinearProgressIndicator,
        spec: ProgressSpec,
    ) {
        bindProgressIndicator(
            view = view,
            spec = spec,
        )
    }

    fun bindCircularProgressIndicator(
        view: CircularProgressIndicator,
        spec: ProgressSpec,
    ) {
        bindProgressIndicator(
            view = view,
            spec = spec,
        )
        view.indicatorSize = spec.indicatorSize
    }

    private fun bindProgressIndicator(
        view: ProgressBar,
        spec: ProgressSpec,
    ) {
        val tint = ColorStateList.valueOf(spec.indicatorColor)
        view.isEnabled = spec.enabled
        view.isIndeterminate = spec.progress == null
        view.progressTintList = tint
        view.indeterminateTintList = tint

        if (view is BaseProgressIndicator<*>) {
            view.trackColor = spec.trackColor
            view.trackThickness = spec.trackThickness
            view.setIndicatorColor(spec.indicatorColor)
        } else {
            view.progressBackgroundTintList = ColorStateList.valueOf(spec.trackColor)
        }

        if (spec.progress != null) {
            view.max = 10_000
            view.progress = (spec.progress.coerceIn(0f, 1f) * 10_000f).roundToInt()
        }
    }

    fun readProgressSpec(node: VNode): ProgressSpec {
        val spec = node.spec as? ProgressIndicatorNodeProps ?: ProgressIndicatorNodeProps(
            enabled = true,
            progress = null,
            indicatorColor = 0xFF000000.toInt(),
            trackColor = 0x33000000,
            trackThickness = 4,
            indicatorSize = 32,
        )
        return ProgressSpec(
            enabled = spec.enabled,
            progress = spec.progress,
            indicatorColor = spec.indicatorColor,
            trackColor = spec.trackColor,
            trackThickness = spec.trackThickness,
            indicatorSize = spec.indicatorSize,
        )
    }
}
