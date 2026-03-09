package com.viewcompose.widget.core

import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.PlatformRenderContainerHandle
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

    @Test
    fun `lazy column session factory validates android viewgroup container`() {
        val tree = buildVNodeTree {
            LazyColumn(
                items = listOf("A"),
            ) { item ->
                Text(item)
            }
        }
        val spec = tree.single().spec as LazyColumnNodeProps

        val error = runCatching {
            spec.items.single().sessionFactory.create(
                object : PlatformRenderContainerHandle {
                    override val container: Any = Any()
                },
            )
        }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        assertTrue(error?.message?.contains("Android ViewGroup container") == true)
    }
}
