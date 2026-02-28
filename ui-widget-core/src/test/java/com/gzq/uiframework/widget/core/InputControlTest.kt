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
            components = UiComponentStyles(
                button = UiThemeDefaults.light().components.button,
                textField = UiThemeDefaults.light().components.textField,
                checkbox = UiCheckboxStyles(
                    label = 91,
                    labelDisabled = 92,
                    control = 93,
                    controlDisabled = 94,
                ),
                switchControl = UiSwitchStyles(
                    label = 95,
                    labelDisabled = 96,
                    control = 97,
                    controlDisabled = 98,
                ),
                radioButton = UiRadioButtonStyles(
                    label = 99,
                    labelDisabled = 100,
                    control = 101,
                    controlDisabled = 102,
                ),
                slider = UiSliderStyles(
                    control = 103,
                    controlDisabled = 104,
                ),
                segmentedControl = UiThemeDefaults.light().components.segmentedControl,
                progressIndicator = UiThemeDefaults.light().components.progressIndicator,
                tabPager = UiThemeDefaults.light().components.tabPager,
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
        assertEquals(93, node.props.values[PropKeys.CONTROL_COLOR])
        assertEquals(
            91,
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

    @Test
    fun `input control component styles support disabled state override`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = baseTheme.components.button,
                textField = baseTheme.components.textField,
                checkbox = UiCheckboxStyles(
                    label = 101,
                    labelDisabled = 102,
                    control = 103,
                    controlDisabled = 104,
                ),
                switchControl = baseTheme.components.switchControl,
                radioButton = baseTheme.components.radioButton,
                slider = baseTheme.components.slider,
                segmentedControl = baseTheme.components.segmentedControl,
                progressIndicator = baseTheme.components.progressIndicator,
                tabPager = baseTheme.components.tabPager,
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Checkbox(
                    text = "Disabled",
                    checked = false,
                    enabled = false,
                    onCheckedChange = {},
                )
            }
        }

        val node = tree.single()
        val elements = node.modifier.readModifierElements()

        assertEquals(104, node.props.values[PropKeys.CONTROL_COLOR])
        assertEquals(
            102,
            (elements.last { it is TextColorModifierElement } as TextColorModifierElement).color,
        )
    }

    @Test
    fun `input controls resolve independent component domains`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = baseTheme.components.button,
                textField = baseTheme.components.textField,
                checkbox = UiCheckboxStyles(
                    label = 201,
                    labelDisabled = 202,
                    control = 203,
                    controlDisabled = 204,
                ),
                switchControl = UiSwitchStyles(
                    label = 205,
                    labelDisabled = 206,
                    control = 207,
                    controlDisabled = 208,
                ),
                radioButton = UiRadioButtonStyles(
                    label = 209,
                    labelDisabled = 210,
                    control = 211,
                    controlDisabled = 212,
                ),
                slider = UiSliderStyles(
                    control = 213,
                    controlDisabled = 214,
                ),
                segmentedControl = baseTheme.components.segmentedControl,
                progressIndicator = baseTheme.components.progressIndicator,
                tabPager = baseTheme.components.tabPager,
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Column {
                    Checkbox(
                        text = "Checkbox",
                        checked = true,
                        onCheckedChange = {},
                    )
                    Switch(
                        text = "Switch",
                        checked = true,
                        onCheckedChange = {},
                    )
                    RadioButton(
                        text = "Radio",
                        checked = true,
                        onCheckedChange = {},
                    )
                    Slider(
                        value = 12,
                        onValueChange = {},
                    )
                }
            }
        }

        val root = tree.single()
        val checkbox = root.children[0]
        val switchControl = root.children[1]
        val radioButton = root.children[2]
        val slider = root.children[3]

        assertEquals(203, checkbox.props.values[PropKeys.CONTROL_COLOR])
        assertEquals(207, switchControl.props.values[PropKeys.CONTROL_COLOR])
        assertEquals(211, radioButton.props.values[PropKeys.CONTROL_COLOR])
        assertEquals(213, slider.props.values[PropKeys.CONTROL_COLOR])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
