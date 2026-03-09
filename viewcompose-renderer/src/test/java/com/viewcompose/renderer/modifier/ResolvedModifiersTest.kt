package com.viewcompose.renderer.modifier

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.TransformOrigin
import com.viewcompose.ui.modifier.backgroundColor
import com.viewcompose.ui.modifier.backgroundDrawableRes
import com.viewcompose.ui.modifier.graphicsLayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ResolvedModifiersTest {
    @Test
    fun `resolve captures background drawable resource modifier`() {
        val resolved = Modifier
            .backgroundColor(0xFF112233.toInt())
            .backgroundDrawableRes(42)
            .resolve()

        assertEquals(0xFF112233.toInt(), resolved.backgroundColor?.color)
        assertNotNull(resolved.backgroundDrawableRes)
        assertEquals(42, resolved.backgroundDrawableRes?.resId)
    }

    @Test
    fun `resolve captures graphics layer modifier payload`() {
        val resolved = Modifier
            .graphicsLayer(
                scaleX = 1.1f,
                translationX = 20f,
                alpha = 0.6f,
                transformOrigin = TransformOrigin.Center,
                clip = true,
            )
            .resolve()

        assertNotNull(resolved.graphicsLayer)
        assertEquals(1.1f, resolved.graphicsLayer?.scaleX)
        assertEquals(20f, resolved.graphicsLayer?.translationX)
        assertEquals(0.6f, resolved.graphicsLayer?.alpha)
        assertEquals(0.5f, resolved.graphicsLayer?.transformOrigin?.pivotFractionX)
        assertEquals(0.5f, resolved.graphicsLayer?.transformOrigin?.pivotFractionY)
        assertEquals(true, resolved.graphicsLayer?.clip)
    }
}
