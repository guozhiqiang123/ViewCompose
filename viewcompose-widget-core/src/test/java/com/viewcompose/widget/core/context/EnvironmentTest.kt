package com.viewcompose.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class EnvironmentTest {
    @Test
    fun `environment uses defaults outside provider`() {
        assertEquals(UiLayoutDirection.Ltr, Environment.layoutDirection)
        assertEquals(listOf("und"), Environment.localeTags)
        assertEquals(1f, Environment.density.density)
    }

    @Test
    fun `environment provider exposes nested values`() {
        val customValues = UiEnvironmentValues(
            density = UiDensity(
                density = 2f,
                scaledDensity = 3f,
            ),
            localeTags = listOf("zh-CN", "en-US"),
            layoutDirection = UiLayoutDirection.Rtl,
        )
        var density = 0f
        var layoutDirection = UiLayoutDirection.Ltr
        var primaryLocale = ""

        buildVNodeTree {
            UiEnvironment(customValues) {
                density = Environment.density.density
                layoutDirection = Environment.layoutDirection
                primaryLocale = Environment.localeTags.first()
            }
        }

        assertEquals(2f, density)
        assertEquals(UiLayoutDirection.Rtl, layoutDirection)
        assertEquals("zh-CN", primaryLocale)
    }
}
