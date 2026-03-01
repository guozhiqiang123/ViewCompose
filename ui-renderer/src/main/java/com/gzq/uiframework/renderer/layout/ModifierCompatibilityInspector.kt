package com.gzq.uiframework.renderer.layout

import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.node.VNode

internal object ModifierCompatibilityInspector {
    fun warnings(node: VNode): List<String> {
        val warnings = mutableListOf<String>()
        if (node.modifier.elements.any { it is TextColorModifierElement }) {
            warnings += "Modifier.textColor() is deprecated. Prefer widget props such as Text(color = ...)."
        }
        if (node.modifier.elements.any { it is TextSizeModifierElement }) {
            warnings += "Modifier.textSize() is deprecated. Prefer widget props such as Text(style = ...) or widget-specific props."
        }
        return warnings
    }
}
