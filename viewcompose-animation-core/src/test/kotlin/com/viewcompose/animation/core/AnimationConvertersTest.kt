package com.viewcompose.animation.core

import org.junit.Assert.assertEquals
import org.junit.Test

class AnimationConvertersTest {
    @Test
    fun `color converter round-trips argb channels`() {
        val color = 0xCC3366AA.toInt()
        val vector = AnimationConverters.ColorInt.toVector(color)
        val restored = AnimationConverters.ColorInt.fromVector(vector)
        assertEquals(color, restored)
    }
}
