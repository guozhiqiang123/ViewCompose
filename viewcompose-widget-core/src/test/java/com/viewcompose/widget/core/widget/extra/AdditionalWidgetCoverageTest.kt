package com.viewcompose.widget.core

import com.viewcompose.renderer.layout.HorizontalAlignment
import com.viewcompose.renderer.layout.MainAxisArrangement
import com.viewcompose.renderer.layout.VerticalAlignment
import com.viewcompose.renderer.node.ImageSource
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.TextFieldImeAction
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.FlowRowNodeProps
import com.viewcompose.renderer.node.spec.LazyRowNodeProps
import com.viewcompose.renderer.node.spec.NavigationBarNodeProps
import com.viewcompose.renderer.node.spec.RowNodeProps
import com.viewcompose.renderer.node.spec.ScrollableColumnNodeProps
import com.viewcompose.renderer.node.spec.ScrollableRowNodeProps
import com.viewcompose.renderer.node.spec.TextFieldNodeProps
import com.viewcompose.renderer.node.spec.TextNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdditionalWidgetCoverageTest {
    @Test
    fun `chip composes row with label and icon slots`() {
        val tree = buildVNodeTree {
            Chip(
                label = "Sync",
                onClick = {},
                variant = ChipVariant.Filter,
                selected = true,
                leadingIcon = ImageSource.Resource(10),
                onTrailingIconClick = {},
            )
        }

        val node = tree.single()
        val spec = node.spec as RowNodeProps
        val textChildren = collectTextNodes(node)

        assertEquals(NodeType.Row, node.type)
        assertEquals(ChipDefaults.iconSpacing(), spec.spacing)
        assertEquals(VerticalAlignment.Center, spec.verticalAlignment)
        assertTrue(textChildren.any { it.text == "Sync" })
        assertTrue(node.children.size >= 2)
    }

    @Test
    fun `search bar emits text field child with search ime action`() {
        val tree = buildVNodeTree {
            SearchBar(
                query = "query",
                onQueryChange = {},
                onSearch = {},
                placeholder = "Type keyword",
                leadingIcon = ImageSource.Resource(11),
                trailingIcon = {
                    Icon(source = ImageSource.Resource(12))
                },
            )
        }

        val node = tree.single()
        val rowSpec = node.spec as RowNodeProps
        val textField = node.children.first { it.type == NodeType.TextField }.spec as TextFieldNodeProps

        assertEquals(NodeType.Row, node.type)
        assertEquals(SearchBarDefaults.iconSpacing(), rowSpec.spacing)
        assertEquals(VerticalAlignment.Center, rowSpec.verticalAlignment)
        assertEquals("query", textField.value)
        assertEquals("Type keyword", textField.placeholder)
        assertEquals(TextFieldImeAction.Search, textField.imeAction)
    }

    @Test
    fun `navigation bar emits items and selection props`() {
        val tree = buildVNodeTree {
            NavigationBar(
                selectedIndex = 1,
                onItemSelected = {},
            ) {
                Item(label = "Home", icon = ImageSource.Resource(1), badgeCount = 2)
                Item(label = "Profile", icon = ImageSource.Resource(2))
            }
        }

        val node = tree.single()
        val spec = node.spec as NavigationBarNodeProps

        assertEquals(NodeType.NavigationBar, node.type)
        assertEquals(1, spec.selectedIndex)
        assertEquals(2, spec.items.size)
        assertEquals("Home", spec.items[0].label)
        assertEquals(2, spec.items[0].badgeCount)
    }

    @Test
    fun `scaffold composes top content fab and bottom slots`() {
        val tree = buildVNodeTree {
            Scaffold(
                topBar = { Text("Top") },
                bottomBar = { Text("Bottom") },
                floatingActionButton = { Text("Fab") },
            ) {
                Text("Body")
            }
        }

        val root = tree.single()
        val textChildren = collectTextNodes(root)

        assertEquals(NodeType.Column, root.type)
        assertEquals(3, root.children.size)
        assertTrue(textChildren.any { it.text == "Top" })
        assertTrue(textChildren.any { it.text == "Body" })
        assertTrue(textChildren.any { it.text == "Fab" })
        assertTrue(textChildren.any { it.text == "Bottom" })
    }

    @Test
    fun `lazy row emits content padding spacing and keyed items`() {
        val tree = buildVNodeTree {
            LazyRow(
                items = listOf("A", "B"),
                key = { it },
                contentPadding = 14.dp,
                spacing = 6.dp,
            ) { item ->
                Text(item)
            }
        }

        val node = tree.single()
        val spec = node.spec as LazyRowNodeProps

        assertEquals(NodeType.LazyRow, node.type)
        assertEquals(14.dp, spec.contentPadding)
        assertEquals(6.dp, spec.spacing)
        assertEquals("A", spec.items[0].key)
        assertEquals("B", spec.items[1].key)
    }

    @Test
    fun `flow row emits spacing and max items props`() {
        val tree = buildVNodeTree {
            FlowRow(
                horizontalSpacing = 10.dp,
                verticalSpacing = 4.dp,
                maxItemsInEachRow = 3,
            ) {
                Text("1")
                Text("2")
                Text("3")
            }
        }

        val node = tree.single()
        val spec = node.spec as FlowRowNodeProps

        assertEquals(NodeType.FlowRow, node.type)
        assertEquals(10.dp, spec.horizontalSpacing)
        assertEquals(4.dp, spec.verticalSpacing)
        assertEquals(3, spec.maxItemsInEachRow)
    }

    @Test
    fun `scrollable column emits arrangement alignment and spacing`() {
        val tree = buildVNodeTree {
            ScrollableColumn(
                spacing = 9.dp,
                arrangement = MainAxisArrangement.SpaceBetween,
                horizontalAlignment = HorizontalAlignment.Center,
            ) {
                Text("A")
            }
        }

        val node = tree.single()
        val spec = node.spec as ScrollableColumnNodeProps

        assertEquals(NodeType.ScrollableColumn, node.type)
        assertEquals(9.dp, spec.spacing)
        assertEquals(MainAxisArrangement.SpaceBetween, spec.arrangement)
        assertEquals(HorizontalAlignment.Center, spec.horizontalAlignment)
    }

    @Test
    fun `scrollable row emits arrangement alignment and spacing`() {
        val tree = buildVNodeTree {
            ScrollableRow(
                spacing = 7.dp,
                arrangement = MainAxisArrangement.End,
                verticalAlignment = VerticalAlignment.Bottom,
            ) {
                Text("A")
                Text("B")
            }
        }

        val node = tree.single()
        val spec = node.spec as ScrollableRowNodeProps

        assertEquals(NodeType.ScrollableRow, node.type)
        assertEquals(7.dp, spec.spacing)
        assertEquals(MainAxisArrangement.End, spec.arrangement)
        assertEquals(VerticalAlignment.Bottom, spec.verticalAlignment)
    }

    private fun collectTextNodes(node: VNode): List<TextNodeProps> {
        val result = mutableListOf<TextNodeProps>()
        fun visit(current: VNode) {
            val spec = current.spec
            if (spec is TextNodeProps) {
                result += spec
            }
            current.children.forEach(::visit)
        }
        visit(node)
        return result
    }
}
