package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.TabPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TabPagerTest {
    @Test
    fun `tab pager emits mapped tab page models`() {
        var selectedIndex = -1

        val tree = buildVNodeTree {
            TabPager(
                selectedTabIndex = 1,
                onTabSelected = { selectedIndex = it },
            ) {
                Page(title = "Overview", key = "overview") {
                    Text("Overview content")
                }
                Page(title = "Input", key = "input") {
                    Text("Input content")
                }
            }
        }

        val node = tree.single()
        @Suppress("UNCHECKED_CAST")
        val pages = node.props.values[PropKeys.TAB_PAGES] as List<TabPage>
        @Suppress("UNCHECKED_CAST")
        val onTabSelected = node.props.values[PropKeys.ON_TAB_SELECTED] as? (Int) -> Unit

        assertEquals(NodeType.TabPager, node.type)
        assertEquals(1, node.props.values[PropKeys.SELECTED_TAB_INDEX])
        assertEquals(TabPagerDefaults.backgroundColor(), node.props.values[PropKeys.TAB_BACKGROUND_COLOR])
        assertEquals(TabPagerDefaults.indicatorColor(), node.props.values[PropKeys.TAB_INDICATOR_COLOR])
        assertEquals(TabPagerDefaults.cornerRadius(), node.props.values[PropKeys.TAB_CORNER_RADIUS])
        assertEquals(TabPagerDefaults.indicatorHeight(), node.props.values[PropKeys.TAB_INDICATOR_HEIGHT])
        assertEquals(
            TabPagerDefaults.tabPaddingHorizontal(),
            node.props.values[PropKeys.TAB_CONTENT_PADDING_HORIZONTAL],
        )
        assertEquals(
            TabPagerDefaults.tabPaddingVertical(),
            node.props.values[PropKeys.TAB_CONTENT_PADDING_VERTICAL],
        )
        assertEquals(TabPagerDefaults.selectedTextColor(), node.props.values[PropKeys.TAB_SELECTED_TEXT_COLOR])
        assertEquals(TabPagerDefaults.unselectedTextColor(), node.props.values[PropKeys.TAB_UNSELECTED_TEXT_COLOR])
        assertEquals(TabPagerDefaults.rippleColor(), node.props.values[PropKeys.TAB_RIPPLE_COLOR])
        assertEquals(2, pages.size)
        assertEquals("Overview", pages[0].title)
        assertEquals("Input", pages[1].title)

        onTabSelected?.invoke(0)
        assertEquals(0, selectedIndex)
        assertNotNull(pages[0].item.sessionFactory)
    }

    @Test
    fun `tab pager uses component style tokens`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = baseTheme.components.button,
                textField = baseTheme.components.textField,
                segmentedControl = baseTheme.components.segmentedControl,
                checkbox = baseTheme.components.checkbox,
                switchControl = baseTheme.components.switchControl,
                radioButton = baseTheme.components.radioButton,
                slider = baseTheme.components.slider,
                progressIndicator = baseTheme.components.progressIndicator,
                tabPager = UiTabPagerStyles(
                    background = 601,
                    indicator = 602,
                    text = 603,
                    selectedText = 604,
                ),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                TabPager(
                    selectedTabIndex = 0,
                    onTabSelected = {},
                ) {
                    Page(title = "Overview", key = "overview") {
                        Text("Overview content")
                    }
                }
            }
        }

        val node = tree.single()

        assertEquals(601, node.props.values[PropKeys.TAB_BACKGROUND_COLOR])
        assertEquals(602, node.props.values[PropKeys.TAB_INDICATOR_COLOR])
        assertEquals(603, node.props.values[PropKeys.TAB_UNSELECTED_TEXT_COLOR])
        assertEquals(604, node.props.values[PropKeys.TAB_SELECTED_TEXT_COLOR])
    }
}
