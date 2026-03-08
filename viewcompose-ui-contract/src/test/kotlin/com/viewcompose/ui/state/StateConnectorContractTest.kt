package com.viewcompose.ui.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StateConnectorContractTest {
    @Test
    fun `lazy list state routes scroll commands to attached connector`() {
        val state = LazyListState()
        val calls = mutableListOf<Pair<Int, Boolean>>()
        val connector = object : LazyListConnector {
            override fun scrollToPosition(index: Int, smooth: Boolean) {
                calls += index to smooth
            }
        }

        state.attach(connector)
        state.scrollToPosition(3)
        state.smoothScrollToPosition(5)
        state.attach(null)
        state.scrollToPosition(9)

        assertEquals(listOf(3 to false, 5 to true), calls)
    }

    @Test
    fun `pager state notifies listeners and delegates scroll command`() {
        val state = PagerState()
        val pageSnapshots = mutableListOf<Pair<Int, Float>>()
        var scrollTarget = -1
        val connector = object : PagerConnector {
            override fun scrollToPage(page: Int) {
                scrollTarget = page
            }
        }

        state.addOnPageSnapshotListener { page, offset ->
            pageSnapshots += page to offset
        }
        state.attach(connector)
        state.scrollToPage(7)
        state.updateFromPager(currentPage = 2, pageOffset = 0.25f)
        state.updateFromPager(currentPage = 2, pageOffset = 0.25f)

        assertEquals(7, scrollTarget)
        assertEquals(1, pageSnapshots.size)
        assertEquals(2, pageSnapshots.single().first)
        assertTrue(pageSnapshots.single().second == 0.25f)
    }
}
