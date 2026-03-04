package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val spec = node.spec as ButtonNodeProps

        assertEquals(NodeType.Button, node.type)
        assertEquals(ImageSource.Resource(11), spec.leadingIcon)
        assertEquals(ImageSource.Resource(12), spec.trailingIcon)
        assertEquals(ButtonDefaults.iconSize(ButtonSize.Large), spec.iconSize)
        assertEquals(ButtonDefaults.iconSpacing(ButtonSize.Large), spec.iconSpacing)
        assertEquals(ButtonDefaults.contentColor(), spec.textColor)
        assertEquals(ButtonDefaults.textStyle(ButtonSize.Large).fontSizeSp, spec.textSizeSp)
        assertEquals(ButtonDefaults.height(ButtonSize.Large), spec.minHeight)
        assertTrue(node.spec is ButtonNodeProps)
    }
}
