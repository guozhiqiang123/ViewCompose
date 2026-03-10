package com.viewcompose.gesture.core

import com.viewcompose.ui.gesture.GestureOrientation
import com.viewcompose.ui.gesture.SwipeDirection
import kotlin.math.abs
import kotlin.math.max

enum class LockedAxis {
    Horizontal,
    Vertical,
}

enum class SwipeDecisionAxis {
    Horizontal,
    Vertical,
}

sealed interface SwipeDecision {
    data class Swipe(val direction: SwipeDirection) : SwipeDecision

    data class Settle(val target: SwipeSettleTarget) : SwipeDecision

    data object None : SwipeDecision
}

enum class SwipeSettleTarget {
    Min,
    Max,
}

fun resolveLockAxis(
    dx: Float,
    dy: Float,
    orientation: GestureOrientation,
    touchSlop: Float,
): LockedAxis? {
    val absDx = abs(dx)
    val absDy = abs(dy)
    return when (orientation) {
        GestureOrientation.Horizontal -> {
            if (absDx < touchSlop || absDx < absDy) null else LockedAxis.Horizontal
        }

        GestureOrientation.Vertical -> {
            if (absDy < touchSlop || absDy < absDx) null else LockedAxis.Vertical
        }

        GestureOrientation.Free -> {
            if (maxOf(absDx, absDy) < touchSlop) {
                null
            } else if (absDx >= absDy) {
                LockedAxis.Horizontal
            } else {
                LockedAxis.Vertical
            }
        }
    }
}

fun shouldActivateTransform(
    panMotion: Float,
    zoomMotion: Float,
    rotationMotion: Float,
    touchSlop: Float,
): Boolean {
    return panMotion > touchSlop || zoomMotion > touchSlop || rotationMotion > touchSlop
}

fun resolveSwipeDecision(
    axis: SwipeDecisionAxis,
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
                SwipeDecisionAxis.Horizontal -> if (towardMax) {
                    SwipeDirection.StartToEnd
                } else {
                    SwipeDirection.EndToStart
                }

                SwipeDecisionAxis.Vertical -> if (towardMax) {
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
