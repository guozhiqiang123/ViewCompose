package com.viewcompose.renderer.layout

import android.view.ViewGroup
import android.widget.LinearLayout
import com.viewcompose.ui.modifier.BoxAlignModifierElement
import com.viewcompose.ui.modifier.HorizontalAlignModifierElement
import com.viewcompose.ui.modifier.VerticalAlignModifierElement
import com.viewcompose.ui.modifier.WeightModifierElement
import com.viewcompose.ui.node.VNode
import com.viewcompose.renderer.view.container.DeclarativeBoxLayout
import com.viewcompose.renderer.view.container.DeclarativeLinearLayout

internal enum class ParentDataHost {
    Row,
    Column,
    Box,
    Other,
}

internal object ModifierParentDataValidator {
    fun validate(
        parent: ViewGroup,
        node: VNode,
    ): List<String> {
        return validate(
            parent = when (parent) {
                is DeclarativeBoxLayout -> ParentDataHost.Box
                is DeclarativeLinearLayout -> if (parent.orientation == LinearLayout.HORIZONTAL) {
                    ParentDataHost.Row
                } else {
                    ParentDataHost.Column
                }

                else -> ParentDataHost.Other
            },
            node = node,
        )
    }

    fun validate(
        parent: ParentDataHost,
        node: VNode,
    ): List<String> {
        val warnings = mutableListOf<String>()
        val hasWeight = node.modifier.elements.any { it is WeightModifierElement }
        val hasBoxAlign = node.modifier.elements.any { it is BoxAlignModifierElement }
        val hasHorizontalAlign = node.modifier.elements.any { it is HorizontalAlignModifierElement }
        val hasVerticalAlign = node.modifier.elements.any { it is VerticalAlignModifierElement }

        if (hasWeight && parent !in setOf(ParentDataHost.Row, ParentDataHost.Column)) {
            warnings += "Modifier.weight() only applies to children of Row/Column."
        }
        if (hasBoxAlign && parent != ParentDataHost.Box) {
            warnings += "Modifier.align(BoxAlignment) only applies to children of Box/Surface."
        }
        if (hasHorizontalAlign) {
            if (parent != ParentDataHost.Column) {
                warnings += "Modifier.align(HorizontalAlignment) only applies to children of Column."
            }
        }
        if (hasVerticalAlign) {
            if (parent != ParentDataHost.Row) {
                warnings += "Modifier.align(VerticalAlignment) only applies to children of Row."
            }
        }
        return warnings
    }
}
