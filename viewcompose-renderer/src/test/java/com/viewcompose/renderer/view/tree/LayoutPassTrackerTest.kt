package com.viewcompose.renderer.view.tree

import org.junit.Assert.assertEquals
import org.junit.Test

class LayoutPassTrackerTest {
    @Test
    fun `records and resets layout pass counts`() {
        LayoutPassTracker.reset()

        LayoutPassTracker.recordMeasure("DeclarativeLinearLayout", durationNs = 2_000_000)
        LayoutPassTracker.recordMeasure("DeclarativeLinearLayout", durationNs = 3_000_000)
        LayoutPassTracker.recordLayout("DeclarativeLinearLayout", durationNs = 1_500_000)
        LayoutPassTracker.recordLayout("DeclarativeBoxLayout", durationNs = 4_000_000)

        val snapshot = LayoutPassTracker.snapshot()

        assertEquals(2, snapshot.totalMeasureCount)
        assertEquals(2, snapshot.totalLayoutCount)
        assertEquals(5_000_000, snapshot.totalMeasureNs)
        assertEquals(5_500_000, snapshot.totalLayoutNs)
        assertEquals(
            listOf(
                LayoutPassEntry(
                    viewName = "DeclarativeLinearLayout",
                    measureCount = 2,
                    layoutCount = 1,
                    totalMeasureNs = 5_000_000,
                    totalLayoutNs = 1_500_000,
                ),
                LayoutPassEntry(
                    viewName = "DeclarativeBoxLayout",
                    measureCount = 0,
                    layoutCount = 1,
                    totalMeasureNs = 0,
                    totalLayoutNs = 4_000_000,
                ),
            ),
            snapshot.entries,
        )

        LayoutPassTracker.reset()
        assertEquals(LayoutPassSnapshot(), LayoutPassTracker.snapshot())
    }
}
