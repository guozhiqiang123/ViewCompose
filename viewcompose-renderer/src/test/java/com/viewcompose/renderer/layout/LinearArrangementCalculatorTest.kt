package com.viewcompose.renderer.layout

import com.viewcompose.ui.layout.MainAxisArrangement
import org.junit.Assert.assertEquals
import org.junit.Test

class LinearArrangementCalculatorTest {
    @Test
    fun `space evenly distributes extra space without weights`() {
        val result = LinearArrangementCalculator.calculate(
            arrangement = MainAxisArrangement.SpaceEvenly,
            itemSpacing = 8,
            extraSpace = 40,
            childCount = 3,
            hasWeightedChildren = false,
        )

        assertEquals(10, result.leadingSpace)
        assertEquals(18, result.gap)
    }

    @Test
    fun `space around distributes extra space without weights`() {
        val result = LinearArrangementCalculator.calculate(
            arrangement = MainAxisArrangement.SpaceAround,
            itemSpacing = 8,
            extraSpace = 60,
            childCount = 3,
            hasWeightedChildren = false,
        )

        assertEquals(10, result.leadingSpace)
        assertEquals(28, result.gap)
    }

    @Test
    fun `weighted children disable distributive arrangements`() {
        val result = LinearArrangementCalculator.calculate(
            arrangement = MainAxisArrangement.SpaceEvenly,
            itemSpacing = 8,
            extraSpace = 60,
            childCount = 3,
            hasWeightedChildren = true,
        )

        assertEquals(0, result.leadingSpace)
        assertEquals(8, result.gap)
    }

    @Test
    fun `weighted children still support end alignment`() {
        val result = LinearArrangementCalculator.calculate(
            arrangement = MainAxisArrangement.End,
            itemSpacing = 8,
            extraSpace = 40,
            childCount = 2,
            hasWeightedChildren = true,
        )

        assertEquals(40, result.leadingSpace)
        assertEquals(8, result.gap)
    }
}
