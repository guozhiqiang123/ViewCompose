package com.gzq.uiframework.renderer.debug

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.InsertPatch
import com.gzq.uiframework.renderer.reconcile.ReconcileResult
import com.gzq.uiframework.renderer.reconcile.RemovePatch
import com.gzq.uiframework.renderer.reconcile.ReusePatch
import org.junit.Assert.assertEquals
import org.junit.Test

class DebugStringsTest {
    @Test
    fun `formats vnode tree with indentation`() {
        val tree = listOf(
            VNode(
                type = NodeType.Column,
                children = listOf(
                    VNode(type = NodeType.Text, key = "title"),
                    VNode(type = NodeType.Button),
                ),
            ),
        )

        assertEquals(
            """
            Column
              Text(key=title)
              Button
            """.trimIndent(),
            tree.debugTree(),
        )
    }

    @Test
    fun `formats reconcile summary`() {
        val result = ReconcileResult(
            patches = listOf(
                InsertPatch<String>(targetIndex = 0, nextVNode = vnode(NodeType.Text)),
                ReusePatch(
                    targetIndex = 1,
                    previousIndex = 2,
                    payload = "payload",
                    nextVNode = vnode(NodeType.Button),
                ),
            ),
            removals = listOf(
                RemovePatch(previousIndex = 3, payload = "stale"),
            ),
        )

        assertEquals(
            """
            insert@0:Text
            reuse 2->1:Button
            remove@3
            """.trimIndent(),
            result.debugSummary(),
        )
    }

    private fun vnode(type: NodeType): VNode {
        return VNode(
            type = type,
            props = Props.Empty,
            modifier = Modifier,
        )
    }
}
