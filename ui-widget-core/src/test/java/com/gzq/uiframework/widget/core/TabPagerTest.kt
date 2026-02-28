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
        assertEquals(2, pages.size)
        assertEquals("Overview", pages[0].title)
        assertEquals("Input", pages[1].title)

        onTabSelected?.invoke(0)
        assertEquals(0, selectedIndex)
        assertNotNull(pages[0].item.sessionFactory)
    }
}
