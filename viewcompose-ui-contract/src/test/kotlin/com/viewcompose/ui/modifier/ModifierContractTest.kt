package com.viewcompose.ui.modifier

import com.viewcompose.graphics.core.DrawCommand
import com.viewcompose.ui.node.spec.ConstraintAnchor
import com.viewcompose.ui.node.spec.ConstraintAnchorLink
import com.viewcompose.ui.node.spec.ConstraintAnchorTarget
import com.viewcompose.ui.node.spec.ConstraintItemSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModifierContractTest {
    @Test
    fun `then keeps modifier element order`() {
        val modifier = Modifier
            .padding(8)
            .then(Modifier.margin(4))
            .overlayAnchor("anchor-1")

        assertEquals(3, modifier.elements.size)
        assertTrue(modifier.elements[0] is PaddingModifierElement)
        assertTrue(modifier.elements[1] is MarginModifierElement)
        assertEquals("anchor-1", (modifier.elements[2] as OverlayAnchorModifierElement).anchorId)
    }

    @Test
    fun `background drawable resource modifier appends expected element`() {
        val modifier = Modifier
            .backgroundColor(0xFF112233.toInt())
            .backgroundDrawableRes(123)

        assertEquals(2, modifier.elements.size)
        assertTrue(modifier.elements[0] is BackgroundColorModifierElement)
        assertEquals(123, (modifier.elements[1] as BackgroundDrawableResModifierElement).resId)
    }

    @Test
    fun `graphicsLayer appends expected transform payload`() {
        val modifier = Modifier
            .alpha(0.5f)
            .graphicsLayer(
                scaleX = 1.2f,
                scaleY = 0.8f,
                rotationZ = 15f,
                transformOrigin = TransformOrigin.Center,
                clip = true,
            )

        assertEquals(2, modifier.elements.size)
        assertTrue(modifier.elements[0] is AlphaModifierElement)
        val layer = modifier.elements[1] as GraphicsLayerModifierElement
        assertEquals(1.2f, layer.scaleX)
        assertEquals(0.8f, layer.scaleY)
        assertEquals(15f, layer.rotationZ)
        assertEquals(0.5f, layer.transformOrigin?.pivotFractionX)
        assertEquals(0.5f, layer.transformOrigin?.pivotFractionY)
        assertEquals(true, layer.clip)
    }

    @Test
    fun `draw modifiers append in chaining order`() {
        val modifier = Modifier
            .drawBehind { _ ->
                drawRect(
                    rect = com.viewcompose.graphics.core.Rect(0f, 0f, 10f, 10f),
                )
            }
            .drawWithContent { _ ->
                drawContent()
            }
            .drawWithCache {
                listOf(DrawCommand.Save, DrawCommand.Restore)
            }

        assertEquals(3, modifier.elements.size)
        assertTrue(modifier.elements[0] is DrawBehindModifierElement)
        assertTrue(modifier.elements[1] is DrawWithContentModifierElement)
        assertTrue(modifier.elements[2] is DrawWithCacheModifierElement)
    }

    @Test
    fun `lazyContainerMotion appends expected motion policy`() {
        val modifier = Modifier
            .lazyContainerReuse(sharePool = true, disableItemAnimator = false)
            .lazyContainerMotion(
                animateInsert = true,
                animateRemove = false,
                animateMove = true,
                animateChange = false,
            )

        assertEquals(2, modifier.elements.size)
        val motion = modifier.elements[1] as LazyContainerMotionModifierElement
        assertEquals(true, motion.animateInsert)
        assertEquals(false, motion.animateRemove)
        assertEquals(true, motion.animateMove)
        assertEquals(false, motion.animateChange)
    }

    @Test
    fun `layoutId and constraint metadata append in order`() {
        val modifier = Modifier
            .layoutId("hero-card")
            .then(
                ConstraintModifierElement(
                    referenceId = "hero-card",
                    constraint = ConstraintItemSpec(
                        top = ConstraintAnchorLink(
                            target = ConstraintAnchorTarget.parent(ConstraintAnchor.Top),
                            margin = 12,
                        ),
                    ),
                ),
            )

        assertEquals(2, modifier.elements.size)
        assertEquals("hero-card", (modifier.elements[0] as LayoutIdModifierElement).layoutId)
        val constraintElement = modifier.elements[1] as ConstraintModifierElement
        assertEquals("hero-card", constraintElement.referenceId)
        assertEquals(12, constraintElement.constraint.top?.margin)
    }
}
