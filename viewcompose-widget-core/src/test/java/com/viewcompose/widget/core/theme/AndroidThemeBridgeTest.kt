package com.viewcompose.widget.core

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
    fun `theme mapper falls back to light defaults`() {
        val tokens = ThemeTokenMapper.fromThemeColors(
            readColor = { attr ->
                when (attr) {
                    androidx.appcompat.R.attr.colorPrimary -> 99
                    else -> null
                }
            },
        )

        assertEquals(99, tokens.colors.primary)
        assertEquals(UiThemeDefaults.light().colors.surface, tokens.colors.surface)
        assertEquals(UiThemeDefaults.light().colors.textPrimary, tokens.colors.textPrimary)
        assertEquals(UiThemeDefaults.light().typography.body.fontSizeSp, tokens.typography.body.fontSizeSp)
    }

    @Test
    fun `theme mapper uses dark defaults when isDarkMode is true`() {
        val tokens = ThemeTokenMapper.fromThemeColors(
            readColor = { null },
            isDarkMode = true,
        )

        assertEquals(UiThemeDefaults.dark().colors.background, tokens.colors.background)
        assertEquals(UiThemeDefaults.dark().colors.surface, tokens.colors.surface)
        assertEquals(UiThemeDefaults.dark().colors.primary, tokens.colors.primary)
        assertEquals(UiThemeDefaults.dark().colors.textPrimary, tokens.colors.textPrimary)
        assertEquals(UiThemeDefaults.dark().typography.title.fontSizeSp, tokens.typography.title.fontSizeSp)
    }

    @Test
    fun `theme mapper bridges typography from text appearance attrs`() {
        val textSizes = mapOf(
            android.R.attr.textAppearanceLarge to 28,
            android.R.attr.textAppearanceMedium to 18,
            android.R.attr.textAppearanceSmall to 12,
        )

        val tokens = ThemeTokenMapper.fromThemeColors(
            readColor = { null },
            readTextSizeSp = textSizes::get,
        )

        assertEquals(28, tokens.typography.title.fontSizeSp)
        assertEquals(18, tokens.typography.body.fontSizeSp)
        assertEquals(12, tokens.typography.label.fontSizeSp)
    }

    @Test
    fun `typography falls back to defaults when text appearances unavailable`() {
        val tokens = ThemeTokenMapper.fromThemeColors(
            readColor = { null },
            readTextSizeSp = { null },
        )

        val fallback = UiThemeDefaults.light()
        assertEquals(fallback.typography.title.fontSizeSp, tokens.typography.title.fontSizeSp)
        assertEquals(fallback.typography.body.fontSizeSp, tokens.typography.body.fontSizeSp)
        assertEquals(fallback.typography.label.fontSizeSp, tokens.typography.label.fontSizeSp)
    }

    @Test
    fun `partial typography override keeps defaults for missing attrs`() {
        val tokens = ThemeTokenMapper.fromThemeColors(
            readColor = { null },
            readTextSizeSp = { attr ->
                when (attr) {
                    android.R.attr.textAppearanceMedium -> 20
                    else -> null
                }
            },
        )

        val fallback = UiThemeDefaults.light()
        assertEquals(fallback.typography.title.fontSizeSp, tokens.typography.title.fontSizeSp)
        assertEquals(20, tokens.typography.body.fontSizeSp)
        assertEquals(fallback.typography.label.fontSizeSp, tokens.typography.label.fontSizeSp)
    }
}
