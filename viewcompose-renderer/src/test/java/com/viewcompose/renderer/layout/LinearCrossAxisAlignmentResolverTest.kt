package com.viewcompose.renderer.layout

import android.view.Gravity
import org.junit.Assert.assertEquals
import org.junit.Test

class LinearCrossAxisAlignmentResolverTest {
    @Test
    fun `child vertical gravity overrides container gravity`() {
        val result = LinearCrossAxisAlignmentResolver.resolveVertical(
            containerGravity = Gravity.TOP,
            childGravity = Gravity.BOTTOM,
        )

        assertEquals(VerticalAlignment.Bottom, result)
    }

    @Test
    fun `child horizontal gravity overrides container gravity`() {
        val result = LinearCrossAxisAlignmentResolver.resolveHorizontal(
            containerGravity = Gravity.START,
            childGravity = Gravity.CENTER_HORIZONTAL,
        )

        assertEquals(HorizontalAlignment.Center, result)
    }

    @Test
    fun `container gravity is used when child gravity is absent`() {
        val result = LinearCrossAxisAlignmentResolver.resolveHorizontal(
            containerGravity = Gravity.END,
            childGravity = null,
        )

        assertEquals(HorizontalAlignment.End, result)
    }
}
