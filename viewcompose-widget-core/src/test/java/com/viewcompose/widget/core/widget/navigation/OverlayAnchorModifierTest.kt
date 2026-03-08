package com.viewcompose.widget.core

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.OverlayAnchorModifierElement
import com.viewcompose.ui.modifier.overlayAnchor
import com.viewcompose.ui.node.NodeType
import org.junit.Assert.assertEquals
import org.junit.Test

class OverlayAnchorModifierTest {
    @Test
    fun `box with overlay anchor emits anchor metadata`() {
        val nodes = buildVNodeTree {
            Box(modifier = Modifier.overlayAnchor("feedback_popup_anchor")) {
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

    @Test
    fun `last overlay anchor modifier wins`() {
        val nodes = buildVNodeTree {
            Box(
                modifier = Modifier
                    .overlayAnchor("stale_anchor")
                    .overlayAnchor("expected_anchor"),
            ) {
                Text(text = "Anchor content")
            }
        }

        val node = nodes.single()
        val elements = node.modifier.readModifierElements()
        val anchor = elements.last { it is OverlayAnchorModifierElement } as OverlayAnchorModifierElement

        assertEquals("expected_anchor", anchor.anchorId)
    }

    private fun com.viewcompose.ui.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.viewcompose.ui.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
