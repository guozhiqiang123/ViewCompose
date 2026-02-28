package com.gzq.uiframework.renderer.layout

import org.junit.Assert.assertEquals
import org.junit.Test

class CrossAxisPlacementCalculatorTest {
    @Test
    fun `center horizontal alignment respects margins`() {
        val result = CrossAxisPlacementCalculator.calculateHorizontal(
            containerSize = 120,
            childSize = 40,
            leadingMargin = 10,
            trailingMargin = 6,
            alignment = HorizontalAlignment.Center,
        )

        assertEquals(42, result)
    }

    @Test
    fun `end vertical alignment respects margins`() {
        val result = CrossAxisPlacementCalculator.calculateVertical(
            containerSize = 140,
            childSize = 30,
            leadingMargin = 8,
            trailingMargin = 12,
            alignment = VerticalAlignment.Bottom,
        )

        assertEquals(98, result)
    }

    @Test
    fun `oversized child clamps to leading margin`() {
        val result = CrossAxisPlacementCalculator.calculateHorizontal(
            containerSize = 60,
            childSize = 80,
            leadingMargin = 6,
            trailingMargin = 4,
            alignment = HorizontalAlignment.End,
        )

        assertEquals(6, result)
    }
}
