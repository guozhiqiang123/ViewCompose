package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import com.gzq.uiframework.renderer.node.TabPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TabPagerDiffTest {
    @Test
    fun `produces move updates for keyed reorder`() {
        val result = TabPagerDiff.calculate(
            previous = listOf(page("A"), page("B"), page("C")),
            next = listOf(page("C"), page("B"), page("A")),
        )

        assertEquals(
            listOf(
                TabPagerUpdate.Move(2, 0),
                TabPagerUpdate.Move(2, 1),
            ),
            result.updates,
        )
        assertEquals(listOf("C", "B", "A"), result.pages.map { it.item.key })
    }

    @Test
    fun `marks title update as change without replacing page identity`() {
        val result = TabPagerDiff.calculate(
            previous = listOf(page("overview", title = "Overview")),
            next = listOf(page("overview", title = "Overview 2")),
        )

        assertEquals(
            listOf(
                TabPagerUpdate.Change(0, page("overview", title = "Overview 2")),
            ),
            result.updates,
        )
    }

    @Test
    fun `selection resolver clamps active index`() {
        assertNull(TabPagerSelectionResolver.resolve(emptyList(), 2))
        assertEquals(0, TabPagerSelectionResolver.resolve(listOf(page("A")), -1))
        assertEquals(1, TabPagerSelectionResolver.resolve(listOf(page("A"), page("B")), 5))
    }

    private fun page(
        key: String,
        title: String = key,
    ): TabPage {
        return TabPage(
            title = title,
            item = LazyListItem(
                key = key,
                contentToken = key,
                sessionFactory = LazyListItemSessionFactory {
                    object : LazyListItemSession {
                        override fun render() = Unit

                        override fun dispose() = Unit
                    }
                },
            ),
        )
    }
}
