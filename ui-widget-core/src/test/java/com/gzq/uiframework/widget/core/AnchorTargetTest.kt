package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class AnchorTargetTest {
    @Test
    fun `anchor target emits box node with anchor id`() {
        val nodes = buildVNodeTree {
            AnchorTarget(anchorId = "feedback_popup_anchor") {
                Text(text = "Anchor content")
            }
        }

        val node = nodes.single()
        assertEquals(NodeType.Box, node.type)
        assertEquals("feedback_popup_anchor", node.props[TypedPropKeys.AnchorId])
        assertEquals(1, node.children.size)
    }
}
