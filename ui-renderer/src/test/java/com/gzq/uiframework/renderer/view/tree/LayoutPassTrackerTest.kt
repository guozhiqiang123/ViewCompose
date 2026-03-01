package com.gzq.uiframework.renderer.view.tree

import org.junit.Assert.assertEquals
import org.junit.Test

class LayoutPassTrackerTest {
    @Test
    fun `records and resets layout pass counts`() {
        LayoutPassTracker.reset()

        LayoutPassTracker.recordMeasure("DeclarativeLinearLayout")
        LayoutPassTracker.recordMeasure("DeclarativeLinearLayout")
        LayoutPassTracker.recordLayout("DeclarativeLinearLayout")
        LayoutPassTracker.recordLayout("DeclarativeBoxLayout")

        val snapshot = LayoutPassTracker.snapshot()

        assertEquals(2, snapshot.totalMeasureCount)
        assertEquals(2, snapshot.totalLayoutCount)
        assertEquals(
            listOf(
                LayoutPassEntry(
                    viewName = "DeclarativeLinearLayout",
                    measureCount = 2,
                    layoutCount = 1,
                ),
                LayoutPassEntry(
                    viewName = "DeclarativeBoxLayout",
                    measureCount = 0,
                    layoutCount = 1,
                ),
            ),
            snapshot.entries,
        )

        LayoutPassTracker.reset()
        assertEquals(LayoutPassSnapshot(), LayoutPassTracker.snapshot())
    }
}
