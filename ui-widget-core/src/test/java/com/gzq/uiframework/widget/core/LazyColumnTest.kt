package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class LazyColumnTest {
    @Test
    fun `lazy column emits content padding and spacing props`() {
        val tree = buildVNodeTree {
            LazyColumn(
                items = listOf("A", "B"),
                contentPadding = 12.dp,
                spacing = 8.dp,
            ) { item ->
                Text(item)
            }
        }

        val node = tree.single()

        assertEquals(NodeType.LazyColumn, node.type)
        assertEquals(12.dp, node.props.values[PropKeys.LAZY_CONTENT_PADDING])
        assertEquals(8.dp, node.props.values[PropKeys.LAZY_SPACING])
    }
}
