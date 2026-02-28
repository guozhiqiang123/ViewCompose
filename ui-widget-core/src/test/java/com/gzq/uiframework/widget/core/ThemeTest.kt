package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
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

        buildVNodeTree {
            UiTheme(outerTheme) {
                outerColor = Theme.colors.textPrimary
                UiTheme(innerTheme) {
                    innerColor = Theme.colors.textPrimary
                }
            }
        }

        assertEquals(outerTheme.colors.textPrimary, outerColor)
        assertEquals(innerTheme.colors.textPrimary, innerColor)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
