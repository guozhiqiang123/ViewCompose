package com.viewcompose.widget.core

import android.content.Context
import android.view.View
import com.viewcompose.ui.layout.BoxAlignment
import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.MainAxisArrangement
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.AndroidViewNodeProps
import com.viewcompose.ui.node.spec.BoxNodeProps
import com.viewcompose.ui.node.spec.ColumnNodeProps
import com.viewcompose.ui.node.spec.DividerNodeProps
import com.viewcompose.ui.node.spec.RowNodeProps
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
        var updatedView: View? = null
        val update: (View) -> Unit = { view ->
            updatedView = view
        }

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
        assertTrue(updatedView == null)
    }
}
