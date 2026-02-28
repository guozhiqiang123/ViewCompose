package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.BorderModifierElement
import com.gzq.uiframework.renderer.modifier.CornerRadiusModifierElement
import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.modifier.RippleColorModifierElement
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
        assertEquals(
            customTheme.input.fieldContainer,
            (elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement).color,
        )
        assertEquals(
            customTheme.shapes.controlCornerRadius,
            (elements.last { it is CornerRadiusModifierElement } as CornerRadiusModifierElement).radius,
        )
        assertEquals(
            customTheme.interactions.pressedOverlay,
            (elements.last { it is RippleColorModifierElement } as RippleColorModifierElement).color,
        )
        assertEquals(true, node.props.values[PropKeys.ENABLED])
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

        val elements = tree.single().modifier.readModifierElements()
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement
        val border = elements.last { it is BorderModifierElement } as BorderModifierElement

        assertEquals(0x00000000, background.color)
        assertEquals(Theme.input.control, border.color)
        assertEquals(1.dp, border.width)
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
        val textSize = elements.last { it is TextSizeModifierElement } as TextSizeModifierElement

        assertEquals(TextFieldDefaults.height(TextFieldSize.Compact), height.height)
        assertEquals(Theme.typography.label.fontSizeSp, textSize.sizeSp)
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

        val disabledElements = disabledTree.single().modifier.readModifierElements()
        val disabledBackground = disabledElements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement
        val errorElements = errorTree.single().modifier.readModifierElements()
        val errorBorder = errorElements.last { it is BorderModifierElement } as BorderModifierElement

        assertEquals(202, disabledBackground.color)
        assertEquals(209, errorBorder.color)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
