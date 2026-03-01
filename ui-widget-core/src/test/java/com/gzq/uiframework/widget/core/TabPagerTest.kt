package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.TypedPropKeys
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
        val pages = node.props[TypedPropKeys.TabPages] as List<TabPage>
        val onTabSelected = node.props[TypedPropKeys.OnTabSelected]

        assertEquals(NodeType.TabPager, node.type)
        assertEquals(1, node.props[TypedPropKeys.SelectedTabIndex])
        assertEquals(TabPagerDefaults.backgroundColor(), node.props[TypedPropKeys.TabBackgroundColor])
        assertEquals(TabPagerDefaults.indicatorColor(), node.props[TypedPropKeys.TabIndicatorColor])
        assertEquals(TabPagerDefaults.cornerRadius(), node.props[TypedPropKeys.TabCornerRadius])
        assertEquals(TabPagerDefaults.indicatorHeight(), node.props[TypedPropKeys.TabIndicatorHeight])
        assertEquals(
            TabPagerDefaults.tabPaddingHorizontal(),
            node.props[TypedPropKeys.TabContentPaddingHorizontal],
        )
        assertEquals(
            TabPagerDefaults.tabPaddingVertical(),
            node.props[TypedPropKeys.TabContentPaddingVertical],
        )
        assertEquals(TabPagerDefaults.selectedTextColor(), node.props[TypedPropKeys.TabSelectedTextColor])
        assertEquals(TabPagerDefaults.unselectedTextColor(), node.props[TypedPropKeys.TabUnselectedTextColor])
        assertEquals(TabPagerDefaults.rippleColor(), node.props[TypedPropKeys.TabRippleColor])
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

        assertEquals(601, node.props[TypedPropKeys.TabBackgroundColor])
        assertEquals(602, node.props[TypedPropKeys.TabIndicatorColor])
        assertEquals(603, node.props[TypedPropKeys.TabUnselectedTextColor])
        assertEquals(604, node.props[TypedPropKeys.TabSelectedTextColor])
    }
}
