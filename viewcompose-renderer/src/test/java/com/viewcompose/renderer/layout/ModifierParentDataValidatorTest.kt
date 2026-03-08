package com.viewcompose.renderer.layout

import com.viewcompose.renderer.modifier.BoxAlignModifierElement
import com.viewcompose.renderer.modifier.HorizontalAlignModifierElement
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.VerticalAlignModifierElement
import com.viewcompose.renderer.modifier.WeightModifierElement
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.EmptyNodeSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModifierParentDataValidatorTest {
    @Test
    fun `weight is valid inside linear layout`() {
        val warnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Row,
            node = VNode(
                type = NodeType.Text,
                spec = EmptyNodeSpec,
                modifier = Modifier.then(WeightModifierElement(1f)),
            ),
        )

        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `weight warns outside linear layout`() {
        val warnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Box,
            node = VNode(
                type = NodeType.Text,
                spec = EmptyNodeSpec,
                modifier = Modifier.then(WeightModifierElement(1f)),
            ),
        )

        assertEquals(listOf("Modifier.weight() only applies to children of Row/Column."), warnings)
    }

    @Test
    fun `box align warns outside box layout`() {
        val warnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Row,
            node = VNode(
                type = NodeType.Text,
                spec = EmptyNodeSpec,
                modifier = Modifier.then(BoxAlignModifierElement(BoxAlignment.Center)),
            ),
        )

        assertEquals(listOf("Modifier.align(BoxAlignment) only applies to children of Box/Surface."), warnings)
    }

    @Test
    fun `row and column align only pass in matching orientation`() {
        val verticalWarnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Row,
            node = VNode(
                type = NodeType.Text,
                spec = EmptyNodeSpec,
                modifier = Modifier.then(VerticalAlignModifierElement(VerticalAlignment.Center)),
            ),
        )
        val horizontalWarnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Column,
            node = VNode(
                type = NodeType.Text,
                spec = EmptyNodeSpec,
                modifier = Modifier.then(HorizontalAlignModifierElement(HorizontalAlignment.Center)),
            ),
        )
        val mismatchedVerticalWarnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Column,
            node = VNode(
                type = NodeType.Text,
                spec = EmptyNodeSpec,
                modifier = Modifier.then(VerticalAlignModifierElement(VerticalAlignment.Center)),
            ),
        )

        assertTrue(verticalWarnings.isEmpty())
        assertTrue(horizontalWarnings.isEmpty())
        assertEquals(
            listOf("Modifier.align(VerticalAlignment) only applies to children of Row."),
            mismatchedVerticalWarnings,
        )
    }
}
