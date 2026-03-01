package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.props
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import org.junit.Assert.assertSame
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

        assertSame(NodeBindingPlan.Skip, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `rebinds when node spec changes`() {
        val previous = textNode(text = "before")
        val next = textNode(text = "after")

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is TextNodePatch)
    }

    @Test
    fun `rebinds when modifier changes`() {
        val previous = textNode()
        val next = textNode(modifier = Modifier.padding(8))

        assertSame(NodeBindingPlan.Rebind, NodeBindingDiffer.plan(previous, next))
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

        assertSame(NodeBindingPlan.Rebind, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `patches button semantic updates when style is unchanged`() {
        val previous = buttonNode(text = "Continue")
        val next = buttonNode(text = "Continue now")

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ButtonNodePatch)
    }

    @Test
    fun `rebinds button when style props change`() {
        val previous = buttonNode(textColor = 0xFF000000.toInt())
        val next = buttonNode(textColor = 0xFFFF0000.toInt())

        assertSame(NodeBindingPlan.Rebind, NodeBindingDiffer.plan(previous, next))
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

    private fun buttonNode(
        text: String = "Continue",
        textColor: Int = 0xFF000000.toInt(),
    ): VNode {
        return VNode(
            type = NodeType.Button,
            props = props {
                set(TypedPropKeys.TextColor, textColor)
            },
            spec = ButtonNodeProps(
                text = text,
                enabled = true,
                iconSpacing = 8,
                leadingIcon = null,
                trailingIcon = null,
                iconTint = textColor,
                iconSize = 18,
                onClick = null,
            ),
            modifier = Modifier,
        )
    }
}
