package com.gzq.uiframework.renderer.layout

import android.view.Gravity

internal object LinearCrossAxisAlignmentResolver {
    fun resolveHorizontal(
        containerGravity: Int,
        childGravity: Int?,
    ): HorizontalAlignment {
        val effectiveGravity = childGravity ?: containerGravity
        return when {
            effectiveGravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.CENTER_HORIZONTAL -> {
                HorizontalAlignment.Center
            }

            effectiveGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == Gravity.END ||
                effectiveGravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.RIGHT -> {
                HorizontalAlignment.End
            }

            else -> HorizontalAlignment.Start
        }
    }

    fun resolveVertical(
        containerGravity: Int,
        childGravity: Int?,
    ): VerticalAlignment {
        val effectiveGravity = childGravity ?: containerGravity
        return when (effectiveGravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.CENTER_VERTICAL -> VerticalAlignment.Center
            Gravity.BOTTOM -> VerticalAlignment.Bottom
            else -> VerticalAlignment.Top
        }
    }
}
