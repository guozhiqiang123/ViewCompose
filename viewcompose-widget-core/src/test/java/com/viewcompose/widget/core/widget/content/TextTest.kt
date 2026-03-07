package com.viewcompose.widget.core

import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.TextAlign
import com.viewcompose.renderer.node.TextOverflow
import com.viewcompose.renderer.node.spec.TextNodeProps
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

    @Test
    fun `text inherits content color from ProvideContentColor`() {
        val tree = buildVNodeTree {
            ProvideLocal(LocalContentColor, 0xFFABCDEF.toInt()) {
                Text("colored")
            }
        }

        val spec = tree.single().spec as TextNodeProps
        assertEquals(0xFFABCDEF.toInt(), spec.textColor)
    }

    @Test
    fun `text defaults to ContentColor current`() {
        val tree = buildVNodeTree {
            Text("default color")
        }

        val spec = tree.single().spec as TextNodeProps
        assertEquals(Theme.colors.textPrimary, spec.textColor)
    }
}
