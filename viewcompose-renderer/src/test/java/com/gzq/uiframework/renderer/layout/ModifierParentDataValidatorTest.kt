package com.gzq.uiframework.renderer.layout

import com.gzq.uiframework.renderer.modifier.BoxAlignModifierElement
import com.gzq.uiframework.renderer.modifier.HorizontalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.VerticalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.VNode
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
                modifier = Modifier.then(VerticalAlignModifierElement(VerticalAlignment.Center)),
            ),
        )
        val horizontalWarnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Column,
            node = VNode(
                type = NodeType.Text,
                modifier = Modifier.then(HorizontalAlignModifierElement(HorizontalAlignment.Center)),
            ),
        )
        val mismatchedVerticalWarnings = ModifierParentDataValidator.validate(
            parent = ParentDataHost.Column,
            node = VNode(
                type = NodeType.Text,
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
