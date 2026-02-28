package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import org.junit.Assert.assertEquals
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
        @Suppress("UNCHECKED_CAST")
        val items = node.props.values[PropKeys.SEGMENT_ITEMS] as List<SegmentedControlItem>
        @Suppress("UNCHECKED_CAST")
        val onSelectionChange = node.props.values[PropKeys.ON_SEGMENT_SELECTED] as? (Int) -> Unit
        val height = node.modifier.readModifierElements()
            .last { it is HeightModifierElement } as HeightModifierElement

        assertEquals(NodeType.SegmentedControl, node.type)
        assertEquals(1, node.props.values[PropKeys.SEGMENT_SELECTED_INDEX])
        assertEquals(SegmentedControlDefaults.backgroundColor(), node.props.values[PropKeys.SEGMENT_BACKGROUND_COLOR])
        assertEquals(SegmentedControlDefaults.indicatorColor(), node.props.values[PropKeys.SEGMENT_INDICATOR_COLOR])
        assertEquals(SegmentedControlDefaults.cornerRadius(), node.props.values[PropKeys.SEGMENT_CORNER_RADIUS])
        assertEquals(SegmentedControlDefaults.textColor(), node.props.values[PropKeys.SEGMENT_TEXT_COLOR])
        assertEquals(
            SegmentedControlDefaults.selectedTextColor(),
            node.props.values[PropKeys.SEGMENT_SELECTED_TEXT_COLOR],
        )
        assertEquals(SegmentedControlDefaults.rippleColor(), node.props.values[PropKeys.SEGMENT_RIPPLE_COLOR])
        assertEquals(
            SegmentedControlDefaults.textStyle().fontSizeSp,
            node.props.values[PropKeys.SEGMENT_TEXT_SIZE_SP],
        )
        assertEquals(3, items.size)
        assertEquals("System", items[0].label)
        assertEquals(SegmentedControlDefaults.height(), height.height)

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
                inputControl = baseTheme.components.inputControl,
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

        assertEquals(401, node.props.values[PropKeys.SEGMENT_BACKGROUND_COLOR])
        assertEquals(406, node.props.values[PropKeys.SEGMENT_INDICATOR_COLOR])
        assertEquals(408, node.props.values[PropKeys.SEGMENT_TEXT_COLOR])
        assertEquals(410, node.props.values[PropKeys.SEGMENT_SELECTED_TEXT_COLOR])
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
                inputControl = baseTheme.components.inputControl,
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

        assertEquals(false, node.props.values[PropKeys.ENABLED])
        assertEquals(502, node.props.values[PropKeys.SEGMENT_BACKGROUND_COLOR])
        assertEquals(504, node.props.values[PropKeys.SEGMENT_INDICATOR_COLOR])
        assertEquals(506, node.props.values[PropKeys.SEGMENT_TEXT_COLOR])
        assertEquals(508, node.props.values[PropKeys.SEGMENT_SELECTED_TEXT_COLOR])
        assertEquals(0x00000000, node.props.values[PropKeys.SEGMENT_RIPPLE_COLOR])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
