package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.MainAxisArrangement
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.ColumnNodeProps
import com.viewcompose.ui.node.spec.HorizontalPagerNodeProps
import com.viewcompose.ui.node.spec.RowNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeSpecAccessTest {
    @Test
    fun `require spec returns typed spec when matched`() {
        val spec = RowNodeProps(
            spacing = 8,
            arrangement = MainAxisArrangement.Start,
            verticalAlignment = VerticalAlignment.Top,
        )
        val node = VNode(
            type = NodeType.Row,
            spec = spec,
        )

        assertSame(spec, node.requireSpec<RowNodeProps>())
    }

    @Test
    fun `require spec fails fast with readable message when mismatched`() {
        val node = VNode(
            type = NodeType.Row,
            spec = ColumnNodeProps(
                spacing = 0,
                arrangement = MainAxisArrangement.Start,
                horizontalAlignment = HorizontalAlignment.Start,
            ),
        )

        val error = try {
            node.requireSpec<RowNodeProps>()
            throw AssertionError("Expected IllegalStateException")
        } catch (expected: IllegalStateException) {
            expected
        }

        assertTrue(error.message.orEmpty().contains("requires spec=RowNodeProps"))
        assertTrue(error.message.orEmpty().contains("was ColumnNodeProps"))
    }

    @Test
    fun `pager spec reader no longer falls back to defaults`() {
        val node = VNode(
            type = NodeType.HorizontalPager,
            spec = RowNodeProps(
                spacing = 0,
                arrangement = MainAxisArrangement.Start,
                verticalAlignment = VerticalAlignment.Top,
            ),
        )

        try {
            PagerViewBinder.readHorizontalPagerSpec(node)
            throw AssertionError("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
            Unit
        }
    }

    @Test
    fun `pager spec reader returns explicit spec`() {
        val spec = HorizontalPagerNodeProps(
            pages = emptyList(),
            currentPage = 2,
            onPageChanged = null,
            offscreenPageLimit = 3,
            pagerState = null,
            userScrollEnabled = false,
        )
        val node = VNode(
            type = NodeType.HorizontalPager,
            spec = spec,
        )

        val read = PagerViewBinder.readHorizontalPagerSpec(node)
        assertSame(spec.pages, read.pages)
        assertEquals(2, read.currentPage)
        assertEquals(3, read.offscreenPageLimit)
        assertTrue(!read.userScrollEnabled)
    }
}
