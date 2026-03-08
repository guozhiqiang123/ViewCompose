package com.viewcompose.renderer.layout

import com.viewcompose.ui.modifier.BoxAlignModifierElement
import com.viewcompose.ui.modifier.HorizontalAlignModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.VerticalAlignModifierElement
import com.viewcompose.ui.modifier.WeightModifierElement
import com.viewcompose.ui.layout.BoxAlignment
import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.EmptyNodeSpec
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
