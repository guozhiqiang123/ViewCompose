package com.viewcompose.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTokenCompatibilityTest {
    @Test
    fun `semantic tokens drive defaults and tiered typography`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                secondary = 44,
                error = 66,
                success = 7,
                warning = 8,
                info = 9,
                divider = 10,
                textPrimary = 11,
                textSecondary = 12,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 14),
                titleMedium = UiTextStyle(fontSizeSp = 26),
                bodyLarge = UiTextStyle(fontSizeSp = 20),
                bodyMedium = UiTextStyle(fontSizeSp = 17),
                labelSmall = UiTextStyle(fontSizeSp = 12),
            ),
            shapes = UiShapes(
                cardCornerRadius = 20,
                smallCornerRadius = 22,
            ),
        )
        var secondaryContainer = 0
        var errorColor = 0
        var compactTextSize = 0
        var largeTextSize = 0
        var listHeadlineSize = 0
        var topTitleSize = 0
        var smallCornerRadius = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                secondaryContainer = ButtonDefaults.containerColor(ButtonVariant.Secondary)
                errorColor = TextFieldDefaults.hintColor(isError = true)
                compactTextSize = TextFieldDefaults.textStyle(TextFieldSize.Compact).fontSizeSp
                largeTextSize = TextFieldDefaults.textStyle(TextFieldSize.Large).fontSizeSp
                listHeadlineSize = ListItemDefaults.headlineStyle().fontSizeSp
                topTitleSize = TopAppBarDefaults.titleStyle().fontSizeSp
                smallCornerRadius = ButtonDefaults.cornerRadius()
            }
        }

        assertEquals(customTheme.colors.secondary, secondaryContainer)
        assertEquals(customTheme.colors.onErrorContainer, errorColor)
        assertEquals(customTheme.typography.labelSmall.fontSizeSp, compactTextSize)
        assertEquals(customTheme.typography.bodyLarge.fontSizeSp, largeTextSize)
        assertEquals(customTheme.typography.bodyLarge.fontSizeSp, listHeadlineSize)
        assertEquals(customTheme.typography.titleMedium.fontSizeSp, topTitleSize)
        assertEquals(customTheme.shapes.smallCornerRadius, smallCornerRadius)
    }

    @Test
    fun `tiered typography defaults map to base tokens when tiers are omitted`() {
        val typography = UiTypography(
            title = UiTextStyle(fontSizeSp = 30),
            body = UiTextStyle(fontSizeSp = 18),
            label = UiTextStyle(fontSizeSp = 14),
        )

        assertEquals(typography.title, typography.titleMedium)
        assertEquals(typography.body, typography.bodyMedium)
        assertEquals(typography.label, typography.labelMedium)
    }
}
