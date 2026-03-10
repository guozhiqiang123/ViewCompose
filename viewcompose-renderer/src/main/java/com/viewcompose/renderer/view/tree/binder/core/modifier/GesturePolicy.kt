package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.gesture.SwipeDirection
import kotlin.math.abs
import kotlin.math.max

internal enum class GestureAxis {
    Horizontal,
    Vertical,
}

internal sealed interface SwipeDecision {
    data class Swipe(val direction: SwipeDirection) : SwipeDecision

    data class Settle(val target: SwipeSettleTarget) : SwipeDecision

    data object None : SwipeDecision
}

internal enum class SwipeSettleTarget {
    Min,
    Max,
}

internal fun shouldActivateTransform(
    panMotion: Float,
    zoomMotion: Float,
    rotationMotion: Float,
    touchSlop: Float,
): Boolean {
    return panMotion > touchSlop || zoomMotion > touchSlop || rotationMotion > touchSlop
}

internal fun resolveSwipeDecision(
    axis: GestureAxis,
    total: Float,
    velocity: Float,
    minAnchor: Float?,
    maxAnchor: Float?,
    startAnchor: Float,
    touchSlop: Float,
    minFlingVelocity: Float,
): SwipeDecision {
    val anchorSpan = if (minAnchor != null && maxAnchor != null) {
        abs(maxAnchor - minAnchor)
    } else {
        0f
    }
    val distanceThreshold = max(touchSlop * 2f, anchorSpan * 0.35f)
    val preferVelocity = abs(velocity) >= minFlingVelocity
    val towardMax = when {
        preferVelocity -> velocity > 0f
        abs(total) >= distanceThreshold -> total > 0f
        else -> null
    }
    if (towardMax != null) {
        return SwipeDecision.Swipe(
            direction = when (axis) {
                GestureAxis.Horizontal -> if (towardMax) {
                    SwipeDirection.StartToEnd
                } else {
                    SwipeDirection.EndToStart
                }

                GestureAxis.Vertical -> if (towardMax) {
                    SwipeDirection.TopToBottom
                } else {
                    SwipeDirection.BottomToTop
                }
            },
        )
    }
    if (minAnchor != null && maxAnchor != null) {
        val projected = startAnchor + total
        val distanceToMin = abs(projected - minAnchor)
        val distanceToMax = abs(projected - maxAnchor)
        return if (distanceToMax < distanceToMin) {
            SwipeDecision.Settle(SwipeSettleTarget.Max)
        } else {
            SwipeDecision.Settle(SwipeSettleTarget.Min)
        }
    }
    return SwipeDecision.None
}
