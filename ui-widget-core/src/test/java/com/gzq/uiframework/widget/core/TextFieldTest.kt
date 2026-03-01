package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextFieldTest {
    @Test
    fun `text field emits expected props and themed defaults`() {
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
                    label = "Display name",
                    supportingText = "Shown in profile",
                    maxLines = 3,
                    imeAction = TextFieldImeAction.Next,
                )
            }
        }

        val node = tree.single()
        assertEquals(NodeType.TextField, node.type)
        assertEquals("hello", node.props[TypedPropKeys.Value])
        assertEquals("Type here", node.props[TypedPropKeys.Hint])
        assertEquals("Display name", node.props[TypedPropKeys.Label])
        assertEquals("Type here", node.props[TypedPropKeys.Placeholder])
        assertEquals("Shown in profile", node.props[TypedPropKeys.SupportingText])
        assertEquals(true, node.props[TypedPropKeys.SingleLine])
        assertEquals(TextFieldType.Text, node.props[TypedPropKeys.TextFieldType])
        assertEquals(3, node.props[TypedPropKeys.MaxLines])
        assertEquals(TextFieldImeAction.Next, node.props[TypedPropKeys.ImeAction])
        assertEquals(customTheme.colors.textSecondary, node.props[TypedPropKeys.HintTextColor])
        assertEquals(customTheme.colors.textPrimary, node.props[TypedPropKeys.TextColor])
        assertEquals(customTheme.typography.body.fontSizeSp, node.props[TypedPropKeys.TextSizeSp])
        assertEquals(customTheme.input.fieldContainer, node.props[TypedPropKeys.StyleBackgroundColor])
        assertEquals(customTheme.shapes.controlCornerRadius, node.props[TypedPropKeys.StyleCornerRadius])
        assertEquals(customTheme.interactions.pressedOverlay, node.props[TypedPropKeys.StyleRippleColor])
        assertEquals(true, node.props[TypedPropKeys.Enabled])
        assertTrue(node.spec is TextFieldNodeProps)
    }

    @Test
    fun `password field uses password input type`() {
        val tree = buildVNodeTree {
            PasswordField(
                value = "secret",
                onValueChange = {},
                hint = "Password",
                label = "Password",
                supportingText = "At least 8 characters",
            )
        }

        val node = tree.single()

        assertEquals(NodeType.TextField, node.type)
        assertEquals(TextFieldType.Password, node.props[TypedPropKeys.TextFieldType])
        assertEquals("Password", node.props[TypedPropKeys.Label])
        assertEquals("At least 8 characters", node.props[TypedPropKeys.SupportingText])
        assertTrue(node.props[TypedPropKeys.SingleLine] as Boolean)
    }

    @Test
    fun `text area exposes read only and multiline semantics`() {
        val tree = buildVNodeTree {
            TextArea(
                value = "Line 1",
                onValueChange = {},
                label = "Bio",
                supportingText = "Visible to collaborators",
                readOnly = true,
                minLines = 4,
                maxLines = 6,
                imeAction = TextFieldImeAction.Done,
            )
        }

        val node = tree.single()

        assertEquals(false, node.props[TypedPropKeys.SingleLine])
        assertEquals(true, node.props[TypedPropKeys.ReadOnly])
        assertEquals(4, node.props[TypedPropKeys.MinLines])
        assertEquals(6, node.props[TypedPropKeys.MaxLines])
        assertEquals(TextFieldImeAction.Done, node.props[TypedPropKeys.ImeAction])
        assertEquals("Bio", node.props[TypedPropKeys.Label])
        assertEquals("Visible to collaborators", node.props[TypedPropKeys.SupportingText])
    }

    @Test
    fun `outlined text field uses border variant`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                TextField(
                    value = "hello",
                    onValueChange = {},
                    variant = TextFieldVariant.Outlined,
                )
            }
        }

        val node = tree.single()

        assertEquals(0x00000000, node.props[TypedPropKeys.StyleBackgroundColor])
        assertEquals(Theme.input.control, node.props[TypedPropKeys.StyleBorderColor])
        assertEquals(1.dp, node.props[TypedPropKeys.StyleBorderWidth])
    }

    @Test
    fun `compact text field applies themed height and typography`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                TextField(
                    value = "hello",
                    onValueChange = {},
                    size = TextFieldSize.Compact,
                )
            }
        }

        val elements = tree.single().modifier.readModifierElements()

        assertFalse(elements.any { it is com.gzq.uiframework.renderer.modifier.HeightModifierElement })
        assertEquals(TextFieldDefaults.height(TextFieldSize.Compact), tree.single().props[TypedPropKeys.StyleMinHeight])
        assertEquals(Theme.typography.label.fontSizeSp, tree.single().props[TypedPropKeys.TextSizeSp])
    }

    @Test
    fun `disabled and error text field states use themed component styles`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = baseTheme.components.button,
                textField = UiTextFieldStyles(
                    filledContainer = 201,
                    filledDisabledContainer = 202,
                    filledErrorContainer = 203,
                    tonalContainer = 204,
                    tonalDisabledContainer = 205,
                    tonalErrorContainer = 206,
                    outlinedBorder = 207,
                    outlinedDisabledBorder = 208,
                    outlinedErrorBorder = 209,
                ),
                checkbox = baseTheme.components.checkbox,
                switchControl = baseTheme.components.switchControl,
                radioButton = baseTheme.components.radioButton,
                slider = baseTheme.components.slider,
                segmentedControl = baseTheme.components.segmentedControl,
                progressIndicator = baseTheme.components.progressIndicator,
                tabPager = baseTheme.components.tabPager,
            ),
        )

        val disabledTree = buildVNodeTree {
            UiTheme(customTheme) {
                TextField(
                    value = "hello",
                    onValueChange = {},
                    enabled = false,
                )
            }
        }
        val errorTree = buildVNodeTree {
            UiTheme(customTheme) {
                TextField(
                    value = "hello",
                    onValueChange = {},
                    variant = TextFieldVariant.Outlined,
                    isError = true,
                )
            }
        }

        assertEquals(202, disabledTree.single().props[TypedPropKeys.StyleBackgroundColor])
        assertEquals(209, errorTree.single().props[TypedPropKeys.StyleBorderColor])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.gzq.uiframework.renderer.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
