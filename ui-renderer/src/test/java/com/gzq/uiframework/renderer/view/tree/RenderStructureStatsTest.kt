package com.gzq.uiframework.renderer.view.tree

import android.view.View
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import org.junit.Assert.assertEquals
import org.junit.Test

class RenderStructureStatsTest {
    @Test
    fun `computes vnode and mounted tree depth`() {
        val vnodeTree = listOf(
            VNode(
                type = NodeType.Column,
                modifier = Modifier,
                props = Props.Empty,
                children = listOf(
                    VNode(
                        type = NodeType.Row,
                        modifier = Modifier,
                        props = Props.Empty,
                        children = listOf(
                            VNode(
                                type = NodeType.Text,
                                modifier = Modifier,
                                props = Props.Empty,
                            ),
                        ),
                    ),
                    VNode(
                        type = NodeType.Button,
                        modifier = Modifier,
                        props = Props.Empty,
                    ),
                ),
            ),
        )
        val mountedTree = listOf(
            MountedNode(
                vnode = vnodeTree.first(),
                view = View(null),
                children = listOf(
                    MountedNode(
                        vnode = vnodeTree.first().children.first(),
                        view = View(null),
                        children = listOf(
                            MountedNode(
                                vnode = vnodeTree.first().children.first().children.first(),
                                view = View(null),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val stats = RenderStructureStats.from(
            nodes = vnodeTree,
            mountedNodes = mountedTree,
        )

        assertEquals(4, stats.vnodeCount)
        assertEquals(3, stats.mountedNodeCount)
        assertEquals(3, stats.maxVNodeDepth)
        assertEquals(3, stats.maxMountedDepth)
    }
}
