package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
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
        assertEquals(12.dp, node.props[TypedPropKeys.LazyContentPadding])
        assertEquals(8.dp, node.props[TypedPropKeys.LazySpacing])
    }
}
