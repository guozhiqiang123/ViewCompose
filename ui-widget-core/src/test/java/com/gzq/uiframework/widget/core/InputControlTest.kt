package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InputControlTest {
    @Test
    fun `checkbox emits checked props and themed text modifiers`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                accent = 5,
                divider = 6,
                textPrimary = 70,
                textSecondary = 80,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 31),
                body = UiTextStyle(fontSizeSp = 19),
                label = UiTextStyle(fontSizeSp = 13),
            ),
        )
        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Checkbox(
                    text = "Enable logs",
                    checked = true,
                    onCheckedChange = {},
                )
            }
        }

        val node = tree.single()
        val elements = node.modifier.readModifierElements()

        assertEquals(NodeType.Checkbox, node.type)
        assertEquals("Enable logs", node.props.values[PropKeys.TEXT])
        assertEquals(true, node.props.values[PropKeys.CHECKED])
        assertEquals(true, node.props.values[PropKeys.ENABLED])
        assertEquals(
            customTheme.colors.textPrimary,
            (elements.last { it is TextColorModifierElement } as TextColorModifierElement).color,
        )
        assertEquals(
            customTheme.typography.body.fontSizeSp,
            (elements.last { it is TextSizeModifierElement } as TextSizeModifierElement).sizeSp,
        )
    }

    @Test
    fun `slider emits range and value props`() {
        val tree = buildVNodeTree {
            Slider(
                value = 24,
                onValueChange = {},
                min = 10,
                max = 60,
                enabled = false,
            )
        }

        val node = tree.single()

        assertEquals(NodeType.Slider, node.type)
        assertEquals(24, node.props.values[PropKeys.SLIDER_VALUE])
        assertEquals(10, node.props.values[PropKeys.MIN_VALUE])
        assertEquals(60, node.props.values[PropKeys.MAX_VALUE])
        assertEquals(false, node.props.values[PropKeys.ENABLED])
    }

    @Test
    fun `radio button and switch keep enabled prop`() {
        val tree = buildVNodeTree {
            Column {
                RadioButton(
                    text = "Alpha",
                    checked = true,
                    enabled = false,
                    onCheckedChange = {},
                )
                Switch(
                    text = "Beta",
                    checked = false,
                    onCheckedChange = {},
                )
            }
        }

        assertEquals(NodeType.RadioButton, tree[0].children[0].type)
        assertEquals(NodeType.Switch, tree[0].children[1].type)
        assertEquals(false, tree[0].children[0].props.values[PropKeys.ENABLED])
        assertTrue(tree[0].children[1].props.values[PropKeys.ENABLED] as Boolean)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
