package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.MinHeightModifierElement
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class ButtonTest {
    @Test
    fun `button emits icon props and themed defaults`() {
        val tree = buildVNodeTree {
            Button(
                text = "Continue",
                leadingIcon = ImageSource.Resource(11),
                trailingIcon = ImageSource.Resource(12),
                size = ButtonSize.Large,
            )
        }

        val node = tree.single()
        val elements = node.modifier.readModifierElements()
        val minHeight = elements.last { it is MinHeightModifierElement } as MinHeightModifierElement

        assertEquals(NodeType.Button, node.type)
        assertEquals(ImageSource.Resource(11), node.props.values[PropKeys.BUTTON_LEADING_ICON])
        assertEquals(ImageSource.Resource(12), node.props.values[PropKeys.BUTTON_TRAILING_ICON])
        assertEquals(ButtonDefaults.iconSize(ButtonSize.Large), node.props.values[PropKeys.BUTTON_ICON_SIZE])
        assertEquals(ButtonDefaults.iconSpacing(ButtonSize.Large), node.props.values[PropKeys.BUTTON_ICON_SPACING])
        assertEquals(ButtonDefaults.contentColor(), node.props.values[PropKeys.TEXT_COLOR])
        assertEquals(ButtonDefaults.textStyle(ButtonSize.Large).fontSizeSp, node.props.values[PropKeys.TEXT_SIZE_SP])
        assertEquals(ButtonDefaults.height(ButtonSize.Large), minHeight.minHeight)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.gzq.uiframework.renderer.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
