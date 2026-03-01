package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SegmentedControlTest {
    @Test
    fun `segmented control emits themed props`() {
        var selectedIndex = -1

        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                SegmentedControl(
                    items = listOf("System", "Light", "Dark"),
                    selectedIndex = 1,
                    onSelectionChange = { selectedIndex = it },
                )
            }
        }

        val node = tree.single()
        val items = node.props[TypedPropKeys.SegmentItems] as List<SegmentedControlItem>
        val onSelectionChange = node.props[TypedPropKeys.OnSegmentSelected]
        val height = node.modifier.readModifierElements()
            .last { it is HeightModifierElement } as HeightModifierElement

        assertEquals(NodeType.SegmentedControl, node.type)
        assertEquals(1, node.props[TypedPropKeys.SegmentSelectedIndex])
        assertEquals(SegmentedControlDefaults.backgroundColor(), node.props[TypedPropKeys.SegmentBackgroundColor])
        assertEquals(SegmentedControlDefaults.indicatorColor(), node.props[TypedPropKeys.SegmentIndicatorColor])
        assertEquals(SegmentedControlDefaults.cornerRadius(), node.props[TypedPropKeys.SegmentCornerRadius])
        assertEquals(SegmentedControlDefaults.textColor(), node.props[TypedPropKeys.SegmentTextColor])
        assertEquals(
            SegmentedControlDefaults.selectedTextColor(),
            node.props[TypedPropKeys.SegmentSelectedTextColor],
        )
        assertEquals(SegmentedControlDefaults.rippleColor(), node.props[TypedPropKeys.SegmentRippleColor])
        assertEquals(
            SegmentedControlDefaults.textStyle().fontSizeSp,
            node.props[TypedPropKeys.SegmentTextSizeSp],
        )
        assertEquals(3, items.size)
        assertEquals("System", items[0].label)
        assertEquals(SegmentedControlDefaults.height(), height.height)
        assertTrue(node.spec is SegmentedControlNodeProps)

        onSelectionChange?.invoke(2)
        assertEquals(2, selectedIndex)
    }

    @Test
    fun `segmented control uses component style tokens`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = baseTheme.components.button,
                textField = baseTheme.components.textField,
                checkbox = baseTheme.components.checkbox,
                switchControl = baseTheme.components.switchControl,
                radioButton = baseTheme.components.radioButton,
                slider = baseTheme.components.slider,
                progressIndicator = baseTheme.components.progressIndicator,
                segmentedControl = UiSegmentedControlStyles(
                    background = 401,
                    backgroundDisabled = 405,
                    indicator = 406,
                    indicatorDisabled = 407,
                    text = 408,
                    textDisabled = 409,
                    selectedText = 410,
                    selectedTextDisabled = 411,
                ),
                tabPager = baseTheme.components.tabPager,
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                SegmentedControl(
                    items = listOf("A", "B"),
                    selectedIndex = 0,
                    onSelectionChange = {},
                )
            }
        }

        val node = tree.single()

        assertEquals(401, node.props[TypedPropKeys.SegmentBackgroundColor])
        assertEquals(406, node.props[TypedPropKeys.SegmentIndicatorColor])
        assertEquals(408, node.props[TypedPropKeys.SegmentTextColor])
        assertEquals(410, node.props[TypedPropKeys.SegmentSelectedTextColor])
    }

    @Test
    fun `segmented control uses disabled component style tokens`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = baseTheme.components.button,
                textField = baseTheme.components.textField,
                checkbox = baseTheme.components.checkbox,
                switchControl = baseTheme.components.switchControl,
                radioButton = baseTheme.components.radioButton,
                slider = baseTheme.components.slider,
                progressIndicator = baseTheme.components.progressIndicator,
                segmentedControl = UiSegmentedControlStyles(
                    background = 501,
                    backgroundDisabled = 502,
                    indicator = 503,
                    indicatorDisabled = 504,
                    text = 505,
                    textDisabled = 506,
                    selectedText = 507,
                    selectedTextDisabled = 508,
                ),
                tabPager = baseTheme.components.tabPager,
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                SegmentedControl(
                    items = listOf("A", "B"),
                    selectedIndex = 0,
                    enabled = false,
                    onSelectionChange = {},
                )
            }
        }

        val node = tree.single()

        assertEquals(false, node.props[TypedPropKeys.Enabled])
        assertEquals(502, node.props[TypedPropKeys.SegmentBackgroundColor])
        assertEquals(504, node.props[TypedPropKeys.SegmentIndicatorColor])
        assertEquals(506, node.props[TypedPropKeys.SegmentTextColor])
        assertEquals(508, node.props[TypedPropKeys.SegmentSelectedTextColor])
        assertEquals(0x00000000, node.props[TypedPropKeys.SegmentRippleColor])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
