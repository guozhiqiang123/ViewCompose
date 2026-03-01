package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.node.TypedPropKeys
import org.junit.Assert.assertEquals
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

        assertEquals(NodeType.Text, node.type)
        assertEquals(2, node.props[TypedPropKeys.TextMaxLines])
        assertEquals(TextOverflow.Ellipsis, node.props[TypedPropKeys.TextOverflow])
        assertEquals(TextAlign.Center, node.props[TypedPropKeys.TextAlign])
    }
}
