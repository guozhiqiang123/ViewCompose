package com.viewcompose.renderer.modifier

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.backgroundColor
import com.viewcompose.ui.modifier.backgroundDrawableRes
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
}
