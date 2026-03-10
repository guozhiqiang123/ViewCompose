package com.viewcompose.animation.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransitionCoreTest {
    @Test
    fun `updateTarget starts running segment and tracks states`() {
        val core = TransitionCore(initialState = false)

        core.updateTarget(true)

        assertTrue(core.isRunning)
        assertEquals(false, core.currentState)
        assertEquals(false, core.segmentInitialState)
        assertEquals(true, core.segmentTargetState)
        assertEquals(1L, core.segmentVersion)
        assertEquals(0L, core.playTimeNanos)
    }

    @Test
    fun `registerDuration keeps max duration for active segment`() {
        val core = TransitionCore(initialState = false)
        core.updateTarget(true)

        core.registerDuration(16L)
        core.registerDuration(64L)
        core.registerDuration(32L)

        assertEquals(64L, core.segmentDurationNanos)
    }

    @Test
    fun `play time reaching duration completes segment`() {
        val core = TransitionCore(initialState = false)
        core.updateTarget(true)
        core.registerDuration(48L)

        core.updatePlayTime(48L)

        assertFalse(core.isRunning)
        assertEquals(true, core.currentState)
        assertEquals(true, core.targetState)
        assertEquals(48L, core.playTimeNanos)
    }

    @Test
    fun `retarget while running uses previous target as new initial segment state`() {
        val core = TransitionCore(initialState = false)
        core.updateTarget(true)
        core.registerDuration(100L)
        core.updatePlayTime(40L)

        core.updateTarget(false)

        assertTrue(core.isRunning)
        assertEquals(true, core.segmentInitialState)
        assertEquals(false, core.segmentTargetState)
        assertEquals(2L, core.segmentVersion)
        assertEquals(0L, core.playTimeNanos)
    }
}
