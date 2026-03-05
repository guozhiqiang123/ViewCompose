package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.spec.HorizontalPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.TabRowNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TabPagerTest {
    @Test
    fun `tab pager emits column with tab row and horizontal pager`() {
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

        val column = tree.single()
        assertEquals(NodeType.Column, column.type)
        assertEquals(2, column.children.size)

        val tabRowNode = column.children[0]
        assertEquals(NodeType.TabRow, tabRowNode.type)
        val tabRowSpec = tabRowNode.spec as TabRowNodeProps
        assertEquals(1, tabRowSpec.selectedIndex)
        assertEquals(2, tabRowSpec.tabs.size)

        val pagerNode = column.children[1]
        assertEquals(NodeType.HorizontalPager, pagerNode.type)
        val pagerSpec = pagerNode.spec as HorizontalPagerNodeProps
        assertEquals(1, pagerSpec.currentPage)
        assertEquals(2, pagerSpec.pages.size)

        tabRowSpec.onTabSelected?.invoke(0)
        assertEquals(0, selectedIndex)
        assertNotNull(pagerSpec.pages[0].sessionFactory)
    }

    @Test
    fun `tab pager uses color override tokens`() {
        val baseTheme = UiThemeDefaults.light()

        val tree = buildVNodeTree {
            UiTheme(baseTheme) {
                ProvideTabPagerColors(
                    TabPagerColorOverride(
                        background = 601,
                        indicator = 602,
                        text = 603,
                        selectedText = 604,
                    ),
                ) {
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
        }

        val column = tree.single()
        val tabRowSpec = column.children[0].spec as TabRowNodeProps

        assertEquals(601, tabRowSpec.containerColor)
        assertEquals(602, tabRowSpec.indicatorColor)
    }
}
