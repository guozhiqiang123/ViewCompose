package com.viewcompose.renderer.layout

import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.VerticalAlignment
import kotlin.math.max

internal object CrossAxisPlacementCalculator {
    fun calculateHorizontal(
        containerSize: Int,
        childSize: Int,
        leadingMargin: Int,
        trailingMargin: Int,
        alignment: HorizontalAlignment,
    ): Int {
        return calculate(
            containerSize = containerSize,
            childSize = childSize,
            leadingMargin = leadingMargin,
            trailingMargin = trailingMargin,
            centerAligned = alignment == HorizontalAlignment.Center,
            endAligned = alignment == HorizontalAlignment.End,
        )
    }

    fun calculateVertical(
        containerSize: Int,
        childSize: Int,
        leadingMargin: Int,
        trailingMargin: Int,
        alignment: VerticalAlignment,
    ): Int {
        return calculate(
            containerSize = containerSize,
            childSize = childSize,
            leadingMargin = leadingMargin,
            trailingMargin = trailingMargin,
            centerAligned = alignment == VerticalAlignment.Center,
            endAligned = alignment == VerticalAlignment.Bottom,
        )
    }

    private fun calculate(
        containerSize: Int,
        childSize: Int,
        leadingMargin: Int,
        trailingMargin: Int,
        centerAligned: Boolean,
        endAligned: Boolean,
    ): Int {
        val consumedSize = childSize + leadingMargin + trailingMargin
        val extra = max(0, containerSize - consumedSize)
        return when {
            centerAligned -> extra / 2 + leadingMargin
            endAligned -> extra + leadingMargin
            else -> leadingMargin
        }
    }
}
