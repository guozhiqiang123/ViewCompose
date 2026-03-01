package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeBindingDifferTest {
    @Test
    fun `ignores child changes when node self is unchanged`() {
        val previous = textNode(
            children = listOf(
                VNode(
                    type = NodeType.Text,
                    spec = TextNodeProps(
                        text = "child-1",
                        maxLines = 1,
                        overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                        textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
                    ),
                ),
            ),
        )
        val next = textNode(
            children = listOf(
                VNode(
                    type = NodeType.Text,
                    spec = TextNodeProps(
                        text = "child-2",
                        maxLines = 1,
                        overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                        textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
                    ),
                ),
            ),
        )

        assertFalse(NodeBindingDiffer.shouldRebind(previous, next))
    }

    @Test
    fun `rebinds when node spec changes`() {
        val previous = textNode(text = "before")
        val next = textNode(text = "after")

        assertTrue(NodeBindingDiffer.shouldRebind(previous, next))
    }

    @Test
    fun `rebinds when modifier changes`() {
        val previous = textNode()
        val next = textNode(modifier = Modifier.padding(8))

        assertTrue(NodeBindingDiffer.shouldRebind(previous, next))
    }

    @Test
    fun `rebinds when props change for nodes without spec`() {
        val previous = VNode(
            type = NodeType.Spacer,
            props = Props.Empty,
            modifier = Modifier,
        )
        val next = VNode(
            type = NodeType.Spacer,
            props = Props(mapOf("width" to 10)),
            modifier = Modifier,
        )

        assertTrue(NodeBindingDiffer.shouldRebind(previous, next))
    }

    private fun textNode(
        text: String = "value",
        modifier: Modifier = Modifier,
        children: List<VNode> = emptyList(),
    ): VNode {
        return VNode(
            type = NodeType.Text,
            props = Props.Empty,
            spec = TextNodeProps(
                text = text,
                maxLines = 1,
                overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
            ),
            modifier = modifier,
            children = children,
        )
    }
}
