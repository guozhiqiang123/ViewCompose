package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
        val spec = node.spec as TabPagerNodeProps

        assertEquals(NodeType.TabPager, node.type)
        assertEquals(1, spec.selectedTabIndex)
        assertEquals(TabPagerDefaults.backgroundColor(), spec.backgroundColor)
        assertEquals(TabPagerDefaults.indicatorColor(), spec.indicatorColor)
        assertEquals(TabPagerDefaults.cornerRadius(), spec.cornerRadius)
        assertEquals(TabPagerDefaults.indicatorHeight(), spec.indicatorHeight)
        assertEquals(TabPagerDefaults.tabPaddingHorizontal(), spec.tabPaddingHorizontal)
        assertEquals(TabPagerDefaults.tabPaddingVertical(), spec.tabPaddingVertical)
        assertEquals(TabPagerDefaults.selectedTextColor(), spec.selectedTextColor)
        assertEquals(TabPagerDefaults.unselectedTextColor(), spec.unselectedTextColor)
        assertEquals(TabPagerDefaults.rippleColor(), spec.rippleColor)
        assertEquals(2, spec.pages.size)
        assertEquals("Overview", spec.pages[0].title)
        assertEquals("Input", spec.pages[1].title)
        assertTrue(node.spec is TabPagerNodeProps)

        spec.onTabSelected?.invoke(0)
        assertEquals(0, selectedIndex)
        assertNotNull(spec.pages[0].item.sessionFactory)
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

        val spec = tree.single().spec as TabPagerNodeProps

        assertEquals(601, spec.backgroundColor)
        assertEquals(602, spec.indicatorColor)
        assertEquals(603, spec.unselectedTextColor)
        assertEquals(604, spec.selectedTextColor)
    }
}
