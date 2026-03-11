package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.ConstraintAnchor
import com.viewcompose.ui.node.spec.ConstraintAnchorLink
import com.viewcompose.ui.node.spec.ConstraintAnchorTarget
import com.viewcompose.ui.node.spec.ConstraintBarrierDirection
import com.viewcompose.ui.node.spec.ConstraintBarrierSpec
import com.viewcompose.ui.node.spec.ConstraintChainOrientation
import com.viewcompose.ui.node.spec.ConstraintChainSpec
import com.viewcompose.ui.node.spec.ConstraintChainStyle
import com.viewcompose.ui.node.spec.ConstraintDimension
import com.viewcompose.ui.node.spec.ConstraintGuidelineDirection
import com.viewcompose.ui.node.spec.ConstraintGuidelinePosition
import com.viewcompose.ui.node.spec.ConstraintGuidelineSpec
import com.viewcompose.ui.node.spec.ConstraintHelpersSpec
import com.viewcompose.ui.node.spec.ConstraintItemSpec
import com.viewcompose.ui.node.spec.ConstraintLayoutNodeProps
import com.viewcompose.ui.node.spec.ConstraintSetSpec
import com.viewcompose.ui.node.spec.EmptyNodeSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContainerViewSpecReaderConstraintLayoutTest {
    @Test
    fun `constraint layout spec reader returns decoupled set and inline helpers`() {
        val constraintSet = ConstraintSetSpec(
            constraints = mapOf(
                "title" to ConstraintItemSpec(
                    width = ConstraintDimension.FillToConstraints,
                    widthMin = 120,
                    widthPercent = 0.4f,
                    constrainedWidth = true,
                    start = ConstraintAnchorLink(
                        target = ConstraintAnchorTarget.parent(ConstraintAnchor.Start),
                        margin = 16,
                    ),
                ),
            ),
            helpers = ConstraintHelpersSpec(
                guidelines = listOf(
                    ConstraintGuidelineSpec(
                        id = "guide-start",
                        direction = ConstraintGuidelineDirection.FromStart,
                        position = ConstraintGuidelinePosition.Fraction(0.4f),
                    ),
                ),
            ),
        )
        val inlineHelpers = ConstraintHelpersSpec(
            barriers = listOf(
                ConstraintBarrierSpec(
                    id = "end-barrier",
                    direction = ConstraintBarrierDirection.End,
                    referencedIds = listOf("title"),
                    margin = 12,
                ),
            ),
            chains = listOf(
                ConstraintChainSpec(
                    orientation = ConstraintChainOrientation.Horizontal,
                    referencedIds = listOf("a", "b", "c"),
                    weights = listOf(1f, 2f, 1f),
                    style = ConstraintChainStyle.SpreadInside,
                ),
            ),
        )
        val node = VNode(
            type = NodeType.ConstraintLayout,
            spec = ConstraintLayoutNodeProps(
                constraintSet = constraintSet,
                helpers = inlineHelpers,
            ),
        )

        val read = ContainerViewSpecReader.readConstraintLayoutSpec(node)

        assertEquals(constraintSet, read.decoupledConstraintSet)
        assertEquals(inlineHelpers, read.inlineHelpers)
    }

    @Test
    fun `constraint layout spec reader fails fast on wrong spec type`() {
        val node = VNode(
            type = NodeType.ConstraintLayout,
            spec = EmptyNodeSpec,
        )

        val error = try {
            ContainerViewSpecReader.readConstraintLayoutSpec(node)
            throw AssertionError("Expected IllegalStateException")
        } catch (expected: IllegalStateException) {
            expected
        }

        assertTrue(error.message.orEmpty().contains("requires spec=ConstraintLayoutNodeProps"))
        assertTrue(error.message.orEmpty().contains("was EmptyNodeSpec"))
    }
}
