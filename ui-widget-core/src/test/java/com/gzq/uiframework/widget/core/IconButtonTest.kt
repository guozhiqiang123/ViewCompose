package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class IconButtonTest {
    @Test
    fun `icon button emits themed defaults`() {
        val tree = buildVNodeTree {
            IconButton(
                icon = ImageSource.Resource(42),
                contentDescription = "Action",
            )
        }

        val node = tree.single()
        val elements = node.modifier.readModifierElements()
        val size = elements.last { it is SizeModifierElement } as SizeModifierElement

        assertEquals(NodeType.IconButton, node.type)
        assertEquals(ImageSource.Resource(42), node.props[TypedPropKeys.ImageSource])
        assertEquals("Action", node.props[TypedPropKeys.ImageContentDescription])
        assertEquals(ImageContentScale.Inside, node.props[TypedPropKeys.ImageContentScale])
        assertEquals(IconButtonDefaults.contentColor(), node.props[TypedPropKeys.ImageTint])
        assertEquals(IconButtonDefaults.size(), size.width)
        assertEquals(IconButtonDefaults.size(), size.height)
        assertEquals(IconButtonDefaults.containerColor(), node.props[TypedPropKeys.StyleBackgroundColor])
        assertEquals(IconButtonDefaults.cornerRadius(), node.props[TypedPropKeys.StyleCornerRadius])
        assertEquals(true, node.props[TypedPropKeys.Enabled])
    }

    @Test
    fun `outlined icon button uses border tokens`() {
        val tree = buildVNodeTree {
            IconButton(
                icon = ImageSource.Resource(7),
                variant = ButtonVariant.Outlined,
                enabled = false,
            )
        }

        val node = tree.single()

        assertEquals(false, node.props[TypedPropKeys.Enabled])
        assertEquals(IconButtonDefaults.borderWidth(ButtonVariant.Outlined), node.props[TypedPropKeys.StyleBorderWidth])
        assertEquals(
            IconButtonDefaults.borderColor(ButtonVariant.Outlined, enabled = false),
            node.props[TypedPropKeys.StyleBorderColor],
        )
        assertEquals(
            IconButtonDefaults.contentColor(ButtonVariant.Outlined, enabled = false),
            node.props[TypedPropKeys.ImageTint],
        )
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
