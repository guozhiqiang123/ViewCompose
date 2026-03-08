package com.viewcompose.widget.core

import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.LazyColumnNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val spec = node.spec as LazyColumnNodeProps

        assertEquals(NodeType.LazyColumn, node.type)
        assertEquals(12.dp, spec.contentPadding)
        assertEquals(8.dp, spec.spacing)
        assertTrue(node.spec is LazyColumnNodeProps)
    }
}
