package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.TextFieldType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextFieldTest {
    @Test
    fun `text field emits expected props and themed modifiers`() {
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
                TextField(
                    value = "hello",
                    onValueChange = {},
                    hint = "Type here",
                )
            }
        }

        val node = tree.single()
        val elements = node.modifier.readModifierElements()

        assertEquals(NodeType.TextField, node.type)
        assertEquals("hello", node.props.values[PropKeys.VALUE])
        assertEquals("Type here", node.props.values[PropKeys.HINT])
        assertEquals(true, node.props.values[PropKeys.SINGLE_LINE])
        assertEquals(TextFieldType.Text, node.props.values[PropKeys.TEXT_FIELD_TYPE])
        assertEquals(customTheme.colors.textSecondary, node.props.values[PropKeys.HINT_TEXT_COLOR])
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
    fun `password field uses password input type`() {
        val tree = buildVNodeTree {
            PasswordField(
                value = "secret",
                onValueChange = {},
                hint = "Password",
            )
        }

        val node = tree.single()

        assertEquals(NodeType.TextField, node.type)
        assertEquals(TextFieldType.Password, node.props.values[PropKeys.TEXT_FIELD_TYPE])
        assertTrue(node.props.values[PropKeys.SINGLE_LINE] as Boolean)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
