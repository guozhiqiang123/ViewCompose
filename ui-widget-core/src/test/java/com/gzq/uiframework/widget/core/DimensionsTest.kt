package com.gzq.uiframework.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class DimensionsTest {
    @Test
    fun `dp uses current environment density`() {
        var resolved = 0
        var floatResolved = 0

        buildVNodeTree {
            UiEnvironment(
                values = UiEnvironmentValues(
                    density = UiDensity(
                        density = 2f,
                        scaledDensity = 3f,
                    ),
                    localeTags = listOf("en-US"),
                    layoutDirection = UiLayoutDirection.Ltr,
                ),
            ) {
                resolved = 8.dp
                floatResolved = 8.4f.dp
            }
        }

        assertEquals(16, resolved)
        assertEquals(16, floatResolved)
    }

    @Test
    fun `sp keeps semantic text units`() {
        assertEquals(14, 14.sp)
        assertEquals(15, 14.6f.sp)
    }
}
