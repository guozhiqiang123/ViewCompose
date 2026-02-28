package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import org.junit.Assert.assertEquals
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

    private fun item(key: String?): LazyListItem {
        return LazyListItem(
            key = key,
            nodes = listOf(
                VNode(
                    type = NodeType.Text,
                    key = key,
                    props = Props.Empty,
                    modifier = Modifier.Empty,
                ),
            ),
        )
    }
}
