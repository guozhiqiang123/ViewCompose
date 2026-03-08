package com.viewcompose.widget.core

import com.viewcompose.renderer.modifier.OverlayAnchorModifierElement
import com.viewcompose.renderer.node.NodeType
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
        val elements = node.modifier.readModifierElements()
        val anchor = elements.last { it is OverlayAnchorModifierElement } as OverlayAnchorModifierElement
        assertEquals(NodeType.Box, node.type)
        assertEquals("feedback_popup_anchor", anchor.anchorId)
        assertEquals(1, node.children.size)
    }

    private fun com.viewcompose.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.viewcompose.renderer.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
