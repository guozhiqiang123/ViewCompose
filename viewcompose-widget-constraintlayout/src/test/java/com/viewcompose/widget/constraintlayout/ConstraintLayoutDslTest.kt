package com.viewcompose.widget.constraintlayout

import com.viewcompose.ui.modifier.ConstraintModifierElement
import com.viewcompose.ui.modifier.LayoutIdModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.ConstraintChainOrientation
import com.viewcompose.ui.node.spec.ConstraintChainStyle
import com.viewcompose.ui.node.spec.ConstraintGuidelineDirection
import com.viewcompose.ui.node.spec.ConstraintGuidelinePosition
import com.viewcompose.ui.node.spec.ConstraintLayoutNodeProps
import com.viewcompose.ui.node.spec.ConstraintSetSpec
import com.viewcompose.widget.core.Text
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.buildVNodeTree
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConstraintLayoutDslTest {
    @Test
    fun `constraint layout emits node props and helper metadata`() {
        val tree = buildVNodeTree {
            ConstraintLayout {
                val (title, subtitle) = createRefs("title", "subtitle")
                val topGuide = createGuidelineFromTop(fraction = 0.2f, id = "guide-top")
                createHorizontalChain(
                    title,
                    subtitle,
                    style = ConstraintChainStyle.Packed,
                    bias = 0.35f,
                )
                Text(
                    text = "Title",
                    modifier = Modifier.constrainAs(title) {
                        topToTop(topGuide)
                        startToStart(parent)
                    },
                )
                Text(
                    text = "Subtitle",
                    modifier = Modifier.constrain("subtitle") {
                        topToBottom(title, margin = 8)
                        startToStart(parent)
                    },
                )
            }
        }

        val node = tree.single()
        assertEquals(NodeType.ConstraintLayout, node.type)
        val spec = node.spec as ConstraintLayoutNodeProps
        assertEquals(1, spec.helpers.guidelines.size)
        assertEquals(ConstraintGuidelineDirection.FromTop, spec.helpers.guidelines.single().direction)
        assertTrue(spec.helpers.guidelines.single().position is ConstraintGuidelinePosition.Fraction)
        assertEquals(1, spec.helpers.chains.size)
        assertEquals(ConstraintChainOrientation.Horizontal, spec.helpers.chains.single().orientation)
        assertEquals(2, node.children.size)
        val firstChildConstraint = node.children[0].modifier.elements.filterIsInstance<ConstraintModifierElement>().single()
        assertEquals("title", firstChildConstraint.referenceId)
    }

    @Test
    fun `constrain shortcut keeps layoutId and constraint metadata aligned`() {
        val ref = ConstraintReference("hero")
        val byRef = Modifier.constrainAs(ref) {
            startToStart(parent)
            endToEnd(parent)
        }
        val byId = Modifier.constrain("hero") {
            startToStart(parent)
            endToEnd(parent)
        }

        val byRefLayoutId = byRef.elements.filterIsInstance<LayoutIdModifierElement>().single()
        val byIdLayoutId = byId.elements.filterIsInstance<LayoutIdModifierElement>().single()
        val byRefConstraint = byRef.elements.filterIsInstance<ConstraintModifierElement>().single()
        val byIdConstraint = byId.elements.filterIsInstance<ConstraintModifierElement>().single()

        assertEquals("hero", byRefLayoutId.layoutId)
        assertEquals("hero", byIdLayoutId.layoutId)
        assertEquals(byRefConstraint.constraint, byIdConstraint.constraint)
        assertEquals("hero", byRefConstraint.referenceId)
        assertEquals("hero", byIdConstraint.referenceId)
    }

    @Test
    fun `decoupled constraintSet collects constraints and helpers`() {
        val set: ConstraintSetSpec = constraintSet {
            val hero = createRef("hero")
            val details = createRef("details")
            val topGuide = createGuidelineFromTop(0.3f, id = "guide")
            createVerticalChain(hero, details, style = ConstraintChainStyle.SpreadInside)
            constrain("hero") {
                topToTop(topGuide)
                startToStart(parent)
            }
            constrain("details") {
                topToBottom(hero, margin = 12)
                startToStart(parent)
            }
        }

        assertEquals(2, set.constraints.size)
        assertEquals(1, set.helpers.guidelines.size)
        assertEquals(1, set.helpers.chains.size)
        assertEquals("guide", set.helpers.guidelines.single().id)
    }

    @Test
    fun `helper apis fail fast outside constraint layout scope`() {
        val builder = UiTreeBuilder()
        try {
            builder.createGuidelineFromTop(10)
            throw AssertionError("Expected an IllegalArgumentException for out-of-scope helper API")
        } catch (expected: IllegalArgumentException) {
            assertTrue(expected.message?.contains("ConstraintLayout helper APIs") == true)
        }
    }
}
