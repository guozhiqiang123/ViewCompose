package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextTest {
    @Test
    fun `text emits max lines overflow and alignment props`() {
        val tree = buildVNodeTree {
            Text(
                text = "Hello",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        val node = tree.single()
        val spec = node.spec as TextNodeProps

        assertEquals(NodeType.Text, node.type)
        assertEquals(2, spec.maxLines)
        assertEquals(TextOverflow.Ellipsis, spec.overflow)
        assertEquals(TextAlign.Center, spec.textAlign)
        assertTrue(node.spec is TextNodeProps)
    }
}
