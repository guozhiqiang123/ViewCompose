package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChildReconcilerTest {
    @Test
    fun `reuses keyed children across reordering`() {
        val previous = listOf(
            reconcileNode("A"),
            reconcileNode("B"),
            reconcileNode("C"),
        )

        val result = ChildReconciler.reconcile(
            previous = previous,
            nodes = listOf(
                vnode("C"),
                vnode("B"),
                vnode("A"),
            ),
        )

        assertEquals(3, result.patches.size)
        val payloadOrder = result.patches.map { patch ->
            (patch as ReusePatch).payload
        }
        assertEquals(listOf("C", "B", "A"), payloadOrder)
        assertTrue(result.removals.isEmpty())
    }

    @Test
    fun `removes nodes that are no longer present`() {
        val previous = listOf(
            reconcileNode("A"),
            reconcileNode("B"),
        )

        val result = ChildReconciler.reconcile(
            previous = previous,
            nodes = listOf(vnode("A")),
        )

        assertEquals(1, result.patches.size)
        assertEquals(1, result.removals.size)
        assertEquals("B", result.removals.single().payload)
    }

    private fun reconcileNode(key: String): ReconcileNode<String> {
        return ReconcileNode(
            vnode = vnode(key),
            payload = key,
        )
    }

    private fun vnode(key: String): VNode {
        return VNode(
            type = NodeType.Text,
            key = key,
            props = Props.Empty,
            modifier = Modifier.Empty,
        )
    }
}
