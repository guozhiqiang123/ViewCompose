package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.TextFieldType
import org.junit.Assert.assertEquals
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
        assertEquals("hello", node.props.values[PropKeys.VALUE])
        assertEquals("Type here", node.props.values[PropKeys.HINT])
        assertEquals("Display name", node.props.values[PropKeys.LABEL])
        assertEquals("Type here", node.props.values[PropKeys.PLACEHOLDER])
        assertEquals("Shown in profile", node.props.values[PropKeys.SUPPORTING_TEXT])
        assertEquals(true, node.props.values[PropKeys.SINGLE_LINE])
        assertEquals(TextFieldType.Text, node.props.values[PropKeys.TEXT_FIELD_TYPE])
        assertEquals(3, node.props.values[PropKeys.MAX_LINES])
        assertEquals(TextFieldImeAction.Next, node.props.values[PropKeys.IME_ACTION])
        assertEquals(customTheme.colors.textSecondary, node.props.values[PropKeys.HINT_TEXT_COLOR])
        assertEquals(customTheme.colors.textPrimary, node.props.values[PropKeys.TEXT_COLOR])
        assertEquals(customTheme.typography.body.fontSizeSp, node.props.values[PropKeys.TEXT_SIZE_SP])
        assertEquals(customTheme.input.fieldContainer, node.props.values[PropKeys.STYLE_BACKGROUND_COLOR])
        assertEquals(customTheme.shapes.controlCornerRadius, node.props.values[PropKeys.STYLE_CORNER_RADIUS])
        assertEquals(customTheme.interactions.pressedOverlay, node.props.values[PropKeys.STYLE_RIPPLE_COLOR])
        assertEquals(true, node.props.values[PropKeys.ENABLED])
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
        assertEquals(TextFieldType.Password, node.props.values[PropKeys.TEXT_FIELD_TYPE])
        assertEquals("Password", node.props.values[PropKeys.LABEL])
        assertEquals("At least 8 characters", node.props.values[PropKeys.SUPPORTING_TEXT])
        assertTrue(node.props.values[PropKeys.SINGLE_LINE] as Boolean)
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

        assertEquals(false, node.props.values[PropKeys.SINGLE_LINE])
        assertEquals(true, node.props.values[PropKeys.READ_ONLY])
        assertEquals(4, node.props.values[PropKeys.MIN_LINES])
        assertEquals(6, node.props.values[PropKeys.MAX_LINES])
        assertEquals(TextFieldImeAction.Done, node.props.values[PropKeys.IME_ACTION])
        assertEquals("Bio", node.props.values[PropKeys.LABEL])
        assertEquals("Visible to collaborators", node.props.values[PropKeys.SUPPORTING_TEXT])
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

        assertEquals(0x00000000, node.props.values[PropKeys.STYLE_BACKGROUND_COLOR])
        assertEquals(Theme.input.control, node.props.values[PropKeys.STYLE_BORDER_COLOR])
        assertEquals(1.dp, node.props.values[PropKeys.STYLE_BORDER_WIDTH])
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
        val height = elements.last { it is HeightModifierElement } as HeightModifierElement

        assertEquals(TextFieldDefaults.height(TextFieldSize.Compact), height.height)
        assertEquals(Theme.typography.label.fontSizeSp, tree.single().props.values[PropKeys.TEXT_SIZE_SP])
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

        assertEquals(202, disabledTree.single().props.values[PropKeys.STYLE_BACKGROUND_COLOR])
        assertEquals(209, errorTree.single().props.values[PropKeys.STYLE_BORDER_COLOR])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.gzq.uiframework.renderer.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
