package com.viewcompose.renderer.view.tree.patch

import android.content.res.ColorStateList
import android.widget.ProgressBar
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.viewcompose.renderer.view.tree.ProgressIndicatorNodePatch
import kotlin.math.roundToInt

internal object FeedbackNodePatchApplier {
    fun applyProgressIndicatorPatch(
        view: ProgressBar,
        patch: ProgressIndicatorNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.enabled != next.enabled) {
            view.isEnabled = next.enabled
        }
        if (previous.progress != next.progress) {
            view.isIndeterminate = next.progress == null
        }
        if (previous.indicatorColor != next.indicatorColor) {
            val tint = ColorStateList.valueOf(next.indicatorColor)
            view.progressTintList = tint
            view.indeterminateTintList = tint
        }
        if (view is BaseProgressIndicator<*>) {
            if (previous.trackColor != next.trackColor) {
                view.trackColor = next.trackColor
            }
            if (previous.trackThickness != next.trackThickness) {
                view.trackThickness = next.trackThickness
            }
            if (previous.indicatorColor != next.indicatorColor) {
                view.setIndicatorColor(next.indicatorColor)
            }
        } else {
            if (previous.trackColor != next.trackColor) {
                view.progressBackgroundTintList = ColorStateList.valueOf(next.trackColor)
            }
        }
        if (view is CircularProgressIndicator) {
            if (previous.indicatorSize != next.indicatorSize) {
                view.indicatorSize = next.indicatorSize
            }
        }
        if (next.progress != null && (previous.progress != next.progress)) {
            view.max = 10_000
            view.progress = (next.progress.coerceIn(0f, 1f) * 10_000f).roundToInt()
        }
    }
}
