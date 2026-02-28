package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.node.PropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {
    @Test
    fun `text uses current theme text color by default`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                accent = 5,
                divider = 6,
                textPrimary = 7,
                textSecondary = 8,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 12),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Text("Hello")
            }
        }

        val textColor = tree.single()
            .modifier
            .readModifierElements()
            .last { it is TextColorModifierElement } as TextColorModifierElement
        assertEquals(7, textColor.color)
        val textSize = tree.single()
            .modifier
            .readModifierElements()
            .last { it is TextSizeModifierElement } as TextSizeModifierElement
        assertEquals(18, textSize.sizeSp)
    }

    @Test
    fun `divider uses current theme divider color by default`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                accent = 5,
                divider = 42,
                textPrimary = 7,
                textSecondary = 8,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 12),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Divider()
            }
        }

        assertEquals(42, tree.single().props.values[PropKeys.DIVIDER_COLOR])
    }

    @Test
    fun `nested theme overrides parent colors`() {
        val outerTheme = UiThemeDefaults.light()
        val innerTheme = UiThemeDefaults.dark()
        var outerColor = 0
        var innerColor = 0
        var outerSize = 0
        var innerSize = 0

        buildVNodeTree {
            UiTheme(outerTheme) {
                outerColor = Theme.colors.textPrimary
                outerSize = Theme.typography.body.fontSizeSp
                UiTheme(innerTheme) {
                    innerColor = Theme.colors.textPrimary
                    innerSize = Theme.typography.body.fontSizeSp
                }
            }
        }

        assertEquals(outerTheme.colors.textPrimary, outerColor)
        assertEquals(innerTheme.colors.textPrimary, innerColor)
        assertEquals(outerTheme.typography.body.fontSizeSp, outerSize)
        assertEquals(innerTheme.typography.body.fontSizeSp, innerSize)
    }

    @Test
    fun `button uses themed container and readable content colors`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 0xFF102030.toInt(),
                accent = 5,
                divider = 6,
                textPrimary = 7,
                textSecondary = 8,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 12),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Button("Tap")
            }
        }

        val elements = tree.single().modifier.readModifierElements()
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement
        val textColor = elements.last { it is TextColorModifierElement } as TextColorModifierElement
        val textSize = elements.last { it is TextSizeModifierElement } as TextSizeModifierElement

        assertEquals(customTheme.colors.primary, background.color)
        assertEquals(0xFFFFFFFF.toInt(), textColor.color)
        assertEquals(customTheme.typography.label.fontSizeSp, textSize.sizeSp)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
