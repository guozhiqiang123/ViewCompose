package com.viewcompose.renderer.debug

import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.Props
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.reconcile.InsertPatch
import com.viewcompose.renderer.reconcile.ReconcileResult
import com.viewcompose.renderer.reconcile.RemovePatch
import com.viewcompose.renderer.reconcile.ReusePatch
import com.viewcompose.renderer.view.tree.RenderStats
import com.viewcompose.renderer.view.tree.RenderStructureStats
import com.viewcompose.renderer.view.tree.RenderTreeResult
import com.viewcompose.renderer.view.tree.MountedNode
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

    @Test
    fun `formats render result summary with stats`() {
        val result = RenderTreeResult(
            mountedNodes = emptyList(),
            reconcileResult = ReconcileResult(
                patches = listOf(
                    InsertPatch<MountedNode>(targetIndex = 0, nextVNode = vnode(NodeType.Text)),
                ),
                removals = emptyList(),
            ),
            stats = RenderStats(
                inserts = 1,
                reuses = 2,
                removals = 3,
                reboundNodes = 4,
                patchedNodes = 5,
                skippedBindings = 6,
                skippedSubtrees = 2,
            ),
            structure = RenderStructureStats(
                vnodeCount = 7,
                mountedNodeCount = 8,
                maxVNodeDepth = 3,
                maxMountedDepth = 4,
            ),
            warnings = listOf("Deep mounted view tree detected: depth=12 exceeds recommended limit 10."),
        )

        assertEquals(
            """
            insert@0:Text
            --
            inserts=1 reuses=2 removals=3 rebound=4 patched=5 skipped=6 subtreeSkipped=2
            vnodeCount=7 mountedCount=8 vnodeDepth=3 mountedDepth=4
            warning: Deep mounted view tree detected: depth=12 exceeds recommended limit 10.
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
