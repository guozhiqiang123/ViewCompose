package com.viewcompose.renderer.view.tree

import com.viewcompose.gesture.core.SwipeDecision
import com.viewcompose.gesture.core.SwipeDecisionAxis
import com.viewcompose.gesture.core.SwipeSettleTarget
import com.viewcompose.gesture.core.resolveSwipeDecision
import com.viewcompose.gesture.core.shouldActivateTransform
import com.viewcompose.ui.gesture.SwipeDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GesturePolicyTest {
    @Test
    fun shouldActivateTransform_returnsFalse_whenAllMotionsBelowSlop() {
        val active = shouldActivateTransform(
            panMotion = 7.9f,
            zoomMotion = 5.0f,
            rotationMotion = 2.0f,
            touchSlop = 8f,
        )
        assertFalse(active)
    }

    @Test
    fun shouldActivateTransform_returnsTrue_whenAnyMotionExceedsSlop() {
        val active = shouldActivateTransform(
            panMotion = 8.1f,
            zoomMotion = 0f,
            rotationMotion = 0f,
            touchSlop = 8f,
        )
        assertTrue(active)
    }

    @Test
    fun resolveSwipeDecision_prefersVelocityOverDistance() {
        val decision = resolveSwipeDecision(
            axis = SwipeDecisionAxis.Horizontal,
            total = 8f,
            velocity = 1800f,
            minAnchor = 0f,
            maxAnchor = 120f,
            startAnchor = 0f,
            touchSlop = 8f,
            minFlingVelocity = 50f,
        )
        assertEquals(
            SwipeDecision.Swipe(SwipeDirection.StartToEnd),
            decision,
        )
    }

    @Test
    fun resolveSwipeDecision_usesDistanceThreshold_whenVelocityLow() {
        val decision = resolveSwipeDecision(
            axis = SwipeDecisionAxis.Vertical,
            total = -100f,
            velocity = -10f,
            minAnchor = 0f,
            maxAnchor = 200f,
            startAnchor = 0f,
            touchSlop = 8f,
            minFlingVelocity = 300f,
        )
        assertEquals(
            SwipeDecision.Swipe(SwipeDirection.BottomToTop),
            decision,
        )
    }

    @Test
    fun resolveSwipeDecision_settlesToNearestAnchor_whenBelowThreshold() {
        val decision = resolveSwipeDecision(
            axis = SwipeDecisionAxis.Horizontal,
            total = 40f,
            velocity = 0f,
            minAnchor = 0f,
            maxAnchor = 120f,
            startAnchor = 0f,
            touchSlop = 8f,
            minFlingVelocity = 300f,
        )
        assertEquals(
            SwipeDecision.Settle(SwipeSettleTarget.Min),
            decision,
        )
    }

    @Test
    fun resolveSwipeDecision_returnsNone_whenNoAnchorsAndNoThresholdHit() {
        val decision = resolveSwipeDecision(
            axis = SwipeDecisionAxis.Horizontal,
            total = 4f,
            velocity = 0f,
            minAnchor = null,
            maxAnchor = null,
            startAnchor = 0f,
            touchSlop = 8f,
            minFlingVelocity = 300f,
        )
        assertEquals(SwipeDecision.None, decision)
    }
}
