package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LazyListDiffTest {
    @Test
    fun `produces move updates for keyed reorder`() {
        val result = LazyListDiff.calculate(
            previous = listOf(item("A"), item("B"), item("C")),
            next = listOf(item("C"), item("B"), item("A")),
        )

        assertEquals(
            listOf(
                LazyListUpdate.Move(2, 0),
                LazyListUpdate.Move(2, 1),
            ),
            result.updates,
        )
        assertEquals(listOf("C", "B", "A"), result.items.map { it.key })
    }

    @Test
    fun `produces insert and remove updates`() {
        val result = LazyListDiff.calculate(
            previous = listOf(item("A"), item("B")),
            next = listOf(item("B"), item("C")),
        )

        assertEquals(
            listOf(
                LazyListUpdate.Remove(0),
                LazyListUpdate.Insert(1, item("C")),
            ),
            result.updates,
        )
        assertEquals(listOf("B", "C"), result.items.map { it.key })
    }

    @Test
    fun `falls back to reload when keys are missing`() {
        val result = LazyListDiff.calculate(
            previous = listOf(item(null)),
            next = listOf(item(null)),
        )

        assertEquals(listOf(LazyListUpdate.ReloadAll), result.updates)
    }

    @Test
    fun `keeps latest lazy item instances when keyed diff produces no updates`() {
        val previous = item("A", contentToken = "stable")
        val next = item("A", contentToken = "stable")

        val result = LazyListDiff.calculate(
            previous = listOf(previous),
            next = listOf(next),
        )

        assertEquals(emptyList<LazyListUpdate>(), result.updates)
        assertSame(next, result.items.first())
    }

    @Test
    fun `keeps latest page instances when pager pages are structurally stable`() {
        val previous = item("page-1", contentToken = "stable")
        val next = item("page-1", contentToken = "stable")

        val result = LazyListDiff.calculate(
            previous = listOf(previous),
            next = listOf(next),
        )

        assertEquals(emptyList<LazyListUpdate>(), result.updates)
        assertSame(next, result.items.first())
    }

    @Test
    fun `keeps latest grid item instances when grid rows are structurally stable`() {
        val previous = item("grid-1", contentToken = "stable")
        val next = item("grid-1", contentToken = "stable")

        val result = LazyListDiff.calculate(
            previous = listOf(previous),
            next = listOf(next),
        )

        assertEquals(emptyList<LazyListUpdate>(), result.updates)
        assertSame(next, result.items.first())
    }

    private fun item(
        key: String?,
        contentToken: Any? = key,
    ): LazyListItem {
        return LazyListItem(
            key = key,
            contentToken = contentToken,
            sessionFactory = LazyListItemSessionFactory {
                object : LazyListItemSession {
                    override fun render() = Unit

                    override fun dispose() = Unit
                }
            },
        )
    }
}
