package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val spec = node.spec as IconButtonNodeProps
        val elements = node.modifier.readModifierElements()
        val size = elements.last { it is SizeModifierElement } as SizeModifierElement

        assertEquals(NodeType.IconButton, node.type)
        assertEquals(ImageSource.Resource(42), spec.source)
        assertEquals("Action", spec.contentDescription)
        assertEquals(ImageContentScale.Inside, spec.contentScale)
        assertEquals(IconButtonDefaults.contentColor(ButtonVariant.Text), spec.tint)
        assertEquals(IconButtonDefaults.size(), size.width)
        assertEquals(IconButtonDefaults.size(), size.height)
        assertEquals(IconButtonDefaults.containerColor(ButtonVariant.Text), spec.backgroundColor)
        assertEquals(IconButtonDefaults.cornerRadius(), spec.cornerRadius)
        assertEquals(true, spec.enabled)
        assertTrue(node.spec is IconButtonNodeProps)
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
        val spec = node.spec as IconButtonNodeProps

        assertEquals(false, spec.enabled)
        assertEquals(IconButtonDefaults.borderWidth(ButtonVariant.Outlined), spec.borderWidth)
        assertEquals(
            IconButtonDefaults.borderColor(ButtonVariant.Outlined, enabled = false),
            spec.borderColor,
        )
        assertEquals(
            IconButtonDefaults.contentColor(ButtonVariant.Outlined, enabled = false),
            spec.tint,
        )
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
