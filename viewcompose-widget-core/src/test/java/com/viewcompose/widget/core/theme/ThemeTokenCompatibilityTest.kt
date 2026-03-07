package com.viewcompose.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTokenCompatibilityTest {
    @Test
    @Suppress("DEPRECATION")
    fun `legacy token aliases remain compatible`() {
        val colors = UiColors(
            background = 1,
            surface = 2,
            surfaceVariant = 3,
            primary = 4,
            secondary = 5,
            error = 6,
            success = 7,
            warning = 8,
            info = 9,
            divider = 10,
            textPrimary = 11,
            textSecondary = 12,
        )
        val shapes = UiShapes(
            cardCornerRadius = 20,
            controlCornerRadius = 14,
        )
        val typography = UiTypography(
            title = UiTextStyle(fontSizeSp = 30),
            body = UiTextStyle(fontSizeSp = 18),
            label = UiTextStyle(fontSizeSp = 14),
        )

        assertEquals(colors.secondary, colors.accent)
        assertEquals(shapes.interactiveCornerRadius, shapes.controlCornerRadius)
        assertEquals(typography.title, typography.titleMedium)
        assertEquals(typography.body, typography.bodyMedium)
        assertEquals(typography.label, typography.labelMedium)
    }

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
                controlCornerRadius = 10,
                interactiveCornerRadius = 22,
            ),
        )
        var secondaryContainer = 0
        var errorColor = 0
        var compactTextSize = 0
        var largeTextSize = 0
        var listHeadlineSize = 0
        var topTitleSize = 0
        var interactiveCornerRadius = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                secondaryContainer = ButtonDefaults.containerColor(ButtonVariant.Secondary)
                errorColor = TextFieldDefaults.hintColor(isError = true)
                compactTextSize = TextFieldDefaults.textStyle(TextFieldSize.Compact).fontSizeSp
                largeTextSize = TextFieldDefaults.textStyle(TextFieldSize.Large).fontSizeSp
                listHeadlineSize = ListItemDefaults.headlineStyle().fontSizeSp
                topTitleSize = TopAppBarDefaults.titleStyle().fontSizeSp
                interactiveCornerRadius = ButtonDefaults.cornerRadius()
            }
        }

        assertEquals(customTheme.colors.secondary, secondaryContainer)
        assertEquals(customTheme.colors.error, errorColor)
        assertEquals(customTheme.typography.labelSmall.fontSizeSp, compactTextSize)
        assertEquals(customTheme.typography.bodyLarge.fontSizeSp, largeTextSize)
        assertEquals(customTheme.typography.bodyMedium.fontSizeSp, listHeadlineSize)
        assertEquals(customTheme.typography.titleMedium.fontSizeSp, topTitleSize)
        assertEquals(customTheme.shapes.interactiveCornerRadius, interactiveCornerRadius)
    }
}
