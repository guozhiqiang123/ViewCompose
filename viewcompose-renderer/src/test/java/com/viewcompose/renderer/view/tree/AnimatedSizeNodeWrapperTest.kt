package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.modifier.AlphaModifierElement
import com.viewcompose.ui.modifier.AnimateContentSizeModifierElement
import com.viewcompose.ui.modifier.ContentSizeTweenSpecModel
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.width
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.TextAlign
import com.viewcompose.ui.node.TextOverflow
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.AnimatedSizeHostNodeProps
import com.viewcompose.ui.node.spec.TextNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimatedSizeNodeWrapperTest {
    @Test
    fun `wraps animateContentSize nodes with animated size host`() {
        val original = textNode(
            modifier = Modifier
                .width(240)
                .then(AlphaModifierElement(alpha = 0.8f))
                .then(
                    AnimateContentSizeModifierElement(
                        animationSpec = ContentSizeTweenSpecModel(
                            durationMillis = 240,
                            delayMillis = 24,
                        ),
                    ),
                ),
        )

        val wrapped = AnimatedSizeNodeWrapper.wrapTree(listOf(original)).single()
        val wrappedChild = wrapped.children.single()

        assertEquals(NodeType.AnimatedSizeHost, wrapped.type)
        assertTrue(wrapped.spec is AnimatedSizeHostNodeProps)
        assertEquals(NodeType.Text, wrappedChild.type)
        assertTrue(wrapped.modifier.elements.any { it is com.viewcompose.ui.modifier.WidthModifierElement })
        assertTrue(wrappedChild.modifier.elements.any { it is AlphaModifierElement })
        assertFalse(wrappedChild.modifier.elements.any { it is AnimateContentSizeModifierElement })
    }

    @Test
    fun `keeps tree untouched when animateContentSize is absent`() {
        val original = textNode(modifier = Modifier.width(120))

        val wrapped = AnimatedSizeNodeWrapper.wrapTree(listOf(original)).single()

        assertEquals(NodeType.Text, wrapped.type)
        assertEquals(original.spec, wrapped.spec)
        assertEquals(original.modifier, wrapped.modifier)
    }

    private fun textNode(modifier: Modifier): VNode {
        return VNode(
            type = NodeType.Text,
            spec = TextNodeProps(
                text = "demo",
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Start,
                textColor = 0xFF000000.toInt(),
                textSizeSp = 14,
            ),
            modifier = modifier,
        )
    }
}
