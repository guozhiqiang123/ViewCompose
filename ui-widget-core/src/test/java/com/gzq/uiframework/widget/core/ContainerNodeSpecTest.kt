package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.View
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContainerNodeSpecTest {
    @Test
    fun `box emits box node spec`() {
        val tree = buildVNodeTree {
            Box(
                contentAlignment = BoxAlignment.BottomEnd,
            ) {
                Text("Box")
            }
        }

        val node = tree.single()

        assertEquals(NodeType.Box, node.type)
        assertEquals(BoxAlignment.BottomEnd, node.props[TypedPropKeys.BoxAlignment])
        val spec = node.spec as BoxNodeProps
        assertEquals(BoxAlignment.BottomEnd, spec.contentAlignment)
    }

    @Test
    fun `row emits row node spec`() {
        val tree = buildVNodeTree {
            Row(
                spacing = 12.dp,
                arrangement = MainAxisArrangement.SpaceBetween,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                Text("A")
                Text("B")
            }
        }

        val node = tree.single()

        assertEquals(NodeType.Row, node.type)
        val spec = node.spec as RowNodeProps
        assertEquals(12.dp, spec.spacing)
        assertEquals(MainAxisArrangement.SpaceBetween, spec.arrangement)
        assertEquals(VerticalAlignment.Center, spec.verticalAlignment)
    }

    @Test
    fun `column emits column node spec`() {
        val tree = buildVNodeTree {
            Column(
                spacing = 8.dp,
                arrangement = MainAxisArrangement.End,
                horizontalAlignment = HorizontalAlignment.Center,
            ) {
                Text("A")
            }
        }

        val node = tree.single()

        assertEquals(NodeType.Column, node.type)
        val spec = node.spec as ColumnNodeProps
        assertEquals(8.dp, spec.spacing)
        assertEquals(MainAxisArrangement.End, spec.arrangement)
        assertEquals(HorizontalAlignment.Center, spec.horizontalAlignment)
    }

    @Test
    fun `divider emits divider node spec`() {
        val tree = buildVNodeTree {
            Divider(
                color = 321,
                thickness = 6.dp,
            )
        }

        val node = tree.single()

        assertEquals(NodeType.Divider, node.type)
        val spec = node.spec as DividerNodeProps
        assertEquals(321, spec.color)
        assertEquals(6.dp, spec.thickness)
    }

    @Test
    fun `android view emits android view node spec`() {
        val factory: (Context) -> View = { context -> View(context) }
        val update: (View) -> Unit = {}

        val tree = buildVNodeTree {
            AndroidView(
                factory = factory,
                update = update,
            )
        }

        val node = tree.single()

        assertEquals(NodeType.AndroidView, node.type)
        val spec = node.spec as AndroidViewNodeProps
        assertNotNull(spec.factory)
        assertNotNull(spec.update)
        assertTrue(spec.update === update)
    }
}
