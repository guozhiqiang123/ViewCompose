package com.gzq.uiframework.renderer.view.tree

import android.content.res.ColorStateList
import android.widget.ProgressBar
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
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
        return ProgressSpec(
            enabled = node.props[TypedPropKeys.Enabled] ?: true,
            progress = node.props[TypedPropKeys.ProgressFraction],
            indicatorColor = node.props[TypedPropKeys.ProgressIndicatorColor] ?: 0xFF000000.toInt(),
            trackColor = node.props[TypedPropKeys.ProgressTrackColor] ?: 0x33000000,
            trackThickness = node.props[TypedPropKeys.ProgressTrackThickness] ?: 4,
            indicatorSize = node.props[TypedPropKeys.ProgressIndicatorSize] ?: 32,
        )
    }
}
