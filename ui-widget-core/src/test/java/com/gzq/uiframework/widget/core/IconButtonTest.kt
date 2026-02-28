package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.BorderModifierElement
import com.gzq.uiframework.renderer.modifier.CornerRadiusModifierElement
import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
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
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement
        val cornerRadius = elements.last { it is CornerRadiusModifierElement } as CornerRadiusModifierElement

        assertEquals(NodeType.IconButton, node.type)
        assertEquals(ImageSource.Resource(42), node.props.values[PropKeys.IMAGE_SOURCE])
        assertEquals("Action", node.props.values[PropKeys.IMAGE_CONTENT_DESCRIPTION])
        assertEquals(ImageContentScale.Inside, node.props.values[PropKeys.IMAGE_CONTENT_SCALE])
        assertEquals(IconButtonDefaults.contentColor(), node.props.values[PropKeys.IMAGE_TINT])
        assertEquals(IconButtonDefaults.size(), size.width)
        assertEquals(IconButtonDefaults.size(), size.height)
        assertEquals(IconButtonDefaults.containerColor(), background.color)
        assertEquals(IconButtonDefaults.cornerRadius(), cornerRadius.radius)
        assertEquals(true, node.props.values[PropKeys.ENABLED])
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
        val elements = node.modifier.readModifierElements()
        val border = elements.last { it is BorderModifierElement } as BorderModifierElement

        assertEquals(false, node.props.values[PropKeys.ENABLED])
        assertEquals(IconButtonDefaults.borderWidth(ButtonVariant.Outlined), border.width)
        assertEquals(
            IconButtonDefaults.borderColor(ButtonVariant.Outlined, enabled = false),
            border.color,
        )
        assertEquals(
            IconButtonDefaults.contentColor(ButtonVariant.Outlined, enabled = false),
            node.props.values[PropKeys.IMAGE_TINT],
        )
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
