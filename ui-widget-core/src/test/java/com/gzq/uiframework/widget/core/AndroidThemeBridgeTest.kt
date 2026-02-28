package com.gzq.uiframework.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidThemeBridgeTest {
    @Test
    fun `theme mapper uses provided attribute colors`() {
        val attrs = mapOf(
            android.R.attr.colorBackground to 1,
            com.google.android.material.R.attr.colorSurface to 2,
            com.google.android.material.R.attr.colorSurfaceVariant to 3,
            androidx.appcompat.R.attr.colorPrimary to 4,
            com.google.android.material.R.attr.colorSecondary to 5,
            com.google.android.material.R.attr.colorOutline to 6,
            android.R.attr.textColorPrimary to 7,
            android.R.attr.textColorSecondary to 8,
        )

        val tokens = ThemeTokenMapper.fromThemeColors(attrs::get)

        assertEquals(1, tokens.colors.background)
        assertEquals(2, tokens.colors.surface)
        assertEquals(3, tokens.colors.surfaceVariant)
        assertEquals(4, tokens.colors.primary)
        assertEquals(5, tokens.colors.accent)
        assertEquals(6, tokens.colors.divider)
        assertEquals(7, tokens.colors.textPrimary)
        assertEquals(8, tokens.colors.textSecondary)
    }

    @Test
    fun `theme mapper falls back to framework defaults`() {
        val tokens = ThemeTokenMapper.fromThemeColors { attr ->
            when (attr) {
                androidx.appcompat.R.attr.colorPrimary -> 99
                else -> null
            }
        }

        assertEquals(99, tokens.colors.primary)
        assertEquals(UiThemeDefaults.light().colors.surface, tokens.colors.surface)
        assertEquals(UiThemeDefaults.light().colors.textPrimary, tokens.colors.textPrimary)
    }
}
