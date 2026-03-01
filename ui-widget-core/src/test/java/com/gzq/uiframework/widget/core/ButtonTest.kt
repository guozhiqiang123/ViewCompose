package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
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

        assertEquals(NodeType.Button, node.type)
        assertEquals(ImageSource.Resource(11), node.props[TypedPropKeys.ButtonLeadingIcon])
        assertEquals(ImageSource.Resource(12), node.props[TypedPropKeys.ButtonTrailingIcon])
        assertEquals(ButtonDefaults.iconSize(ButtonSize.Large), node.props[TypedPropKeys.ButtonIconSize])
        assertEquals(ButtonDefaults.iconSpacing(ButtonSize.Large), node.props[TypedPropKeys.ButtonIconSpacing])
        assertEquals(ButtonDefaults.contentColor(), node.props[TypedPropKeys.TextColor])
        assertEquals(ButtonDefaults.textStyle(ButtonSize.Large).fontSizeSp, node.props[TypedPropKeys.TextSizeSp])
        assertEquals(ButtonDefaults.height(ButtonSize.Large), node.props[TypedPropKeys.StyleMinHeight])
        assertTrue(node.spec is ButtonNodeProps)
    }
}
