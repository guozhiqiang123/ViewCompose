package com.viewcompose.renderer.view.graphics

import com.viewcompose.graphics.core.ImageFilterModel
import com.viewcompose.graphics.core.Radius
import com.viewcompose.graphics.core.Rect
import com.viewcompose.graphics.core.RoundRect
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AndroidDrawCommandExecutorTest {
    @Test
    fun `roundRectRadii keeps corner order as tl tr br bl`() {
        val roundRect = RoundRect(
            rect = Rect(left = 0f, top = 0f, right = 100f, bottom = 80f),
            topLeft = Radius(x = 8f, y = 9f),
            topRight = Radius(x = 10f, y = 11f),
            bottomRight = Radius(x = 12f, y = 13f),
            bottomLeft = Radius(x = 14f, y = 15f),
        )

        assertArrayEquals(
            floatArrayOf(8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f),
            AndroidDrawCommandExecutor.roundRectRadii(roundRect),
            0f,
        )
    }

    @Test
    fun `roundRectRadii clamps negative values to zero`() {
        val roundRect = RoundRect(
            rect = Rect(left = 0f, top = 0f, right = 100f, bottom = 80f),
            topLeft = Radius(x = -1f, y = 3f),
            topRight = Radius(x = 4f, y = -2f),
            bottomRight = Radius(x = -5f, y = -6f),
            bottomLeft = Radius(x = 7f, y = 8f),
        )

        assertArrayEquals(
            floatArrayOf(0f, 3f, 4f, 0f, 0f, 0f, 7f, 8f),
            AndroidDrawCommandExecutor.roundRectRadii(roundRect),
            0f,
        )
    }

    @Test
    fun `combinedBlurRadii merges nested chain as single gaussian radius`() {
        val chain = ImageFilterModel.Chain(
            outer = ImageFilterModel.Blur(radiusX = 4f, radiusY = 3f),
            inner = ImageFilterModel.Chain(
                outer = ImageFilterModel.Blur(radiusX = 0f, radiusY = 4f),
                inner = ImageFilterModel.Blur(radiusX = 3f, radiusY = 0f),
            ),
        )

        val merged = AndroidDrawCommandExecutor.combinedBlurRadii(chain)

        requireNotNull(merged)
        assertEquals(5f, merged.first, 0.0001f)
        assertEquals(5f, merged.second, 0.0001f)
    }

    @Test
    fun `combinedBlurRadii returns null when filter is absent`() {
        assertNull(AndroidDrawCommandExecutor.combinedBlurRadii(null))
    }
}
