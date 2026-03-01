package com.gzq.uiframework.renderer.layout

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.VNode
import org.junit.Assert.assertEquals
import org.junit.Test

class ModifierCompatibilityInspectorTest {
    @Test
    fun `legacy text modifiers produce migration warnings`() {
        val warnings = ModifierCompatibilityInspector.warnings(
            node = VNode(
                type = NodeType.Text,
                modifier = Modifier
                    .then(TextColorModifierElement(1))
                    .then(TextSizeModifierElement(12)),
            ),
        )

        assertEquals(
            listOf(
                "Modifier.textColor() is deprecated. Prefer widget props such as Text(color = ...).",
                "Modifier.textSize() is deprecated. Prefer widget props such as Text(style = ...) or widget-specific props.",
            ),
            warnings,
        )
    }
}
