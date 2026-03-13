package com.viewcompose.widget.core

import com.viewcompose.ui.node.ImageSource
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.ButtonNodeProps
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

    @Test
    fun `button emits full text style fields`() {
        val style = UiTextStyle(
            fontSizeSp = 17.sp,
            fontWeight = 700,
            letterSpacingEm = 0.06f,
            lineHeightSp = 24.sp,
            includeFontPadding = true,
        )

        val tree = buildVNodeTree {
            Button(
                text = "Styled",
                style = style,
            )
        }

        val spec = tree.single().spec as ButtonNodeProps

        assertEquals(style.fontWeight, spec.fontWeight)
        assertEquals(style.letterSpacingEm, spec.letterSpacingEm)
        assertEquals(style.lineHeightSp, spec.lineHeightSp)
        assertEquals(style.includeFontPadding, spec.includeFontPadding)
    }
}
