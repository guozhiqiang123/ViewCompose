package com.viewcompose.renderer.view.tree

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.viewcompose.ui.layout.BoxAlignment
import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.ui.modifier.MarginModifierElement
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.renderer.layout.LayoutParamDefaultsResolver
import com.viewcompose.renderer.layout.ModifierParentDataValidator
import com.viewcompose.renderer.modifier.ResolvedModifiers
import com.viewcompose.renderer.modifier.resolve
import com.viewcompose.renderer.view.container.DeclarativeBoxLayout
import com.viewcompose.renderer.view.container.DeclarativeFlowColumnLayout
import com.viewcompose.renderer.view.container.DeclarativeFlowRowLayout
import com.viewcompose.renderer.view.container.DeclarativeLinearLayout

internal object ViewLayoutParamsFactory {
    fun createLayoutParams(
        parent: ViewGroup,
        node: VNode,
        warningTag: String,
        emittedModifierWarnings: MutableSet<String>,
        resolved: ResolvedModifiers = node.modifier.resolve(),
    ): ViewGroup.LayoutParams {
        emitModifierWarnings(
            parent = parent,
            node = node,
            warningTag = warningTag,
            emittedModifierWarnings = emittedModifierWarnings,
        )
        val boxAlign = resolved.boxAlign
        val margin = resolved.margin
        val size = resolved.size
        val widthModifier = resolved.width
        val heightModifier = resolved.height
        val weight = resolved.weight
        val horizontalAlign = resolved.horizontalAlign
        val verticalAlign = resolved.verticalAlign
        val useLinearLikeDefaults = parent is DeclarativeLinearLayout ||
            parent is DeclarativeFlowRowLayout ||
            parent is DeclarativeFlowColumnLayout
        val linearLikeOrientation = when (parent) {
            is DeclarativeLinearLayout -> parent.orientation
            is DeclarativeFlowRowLayout -> LinearLayout.HORIZONTAL
            is DeclarativeFlowColumnLayout -> LinearLayout.VERTICAL
            else -> null
        }
        val defaultWidth = if (node.type == NodeType.Divider) {
            defaultDividerWidth(parent, node)
        } else {
            LayoutParamDefaultsResolver.defaultWidth(
                nodeType = node.type,
                useLinearLikeDefaults = useLinearLikeDefaults,
                linearOrientation = linearLikeOrientation,
            )
        }
        val defaultHeight = if (node.type == NodeType.Divider) {
            defaultDividerHeight(parent, node)
        } else {
            LayoutParamDefaultsResolver.defaultHeight(
                nodeType = node.type,
                useLinearLikeDefaults = useLinearLikeDefaults,
                linearOrientation = linearLikeOrientation,
            )
        }
        val width = widthModifier?.width ?: size?.width ?: defaultWidth
        val height = heightModifier?.height ?: size?.height ?: defaultHeight
        return when (parent) {
            is DeclarativeLinearLayout -> {
                val resolvedWidth = if (
                    weight != null &&
                    parent.orientation == LinearLayout.HORIZONTAL &&
                    widthModifier == null &&
                    size?.width == null
                ) {
                    0
                } else {
                    width
                }
                val resolvedHeight = if (
                    weight != null &&
                    parent.orientation == LinearLayout.VERTICAL &&
                    heightModifier == null &&
                    size?.height == null
                ) {
                    0
                } else {
                    height
                }
                android.widget.LinearLayout.LayoutParams(resolvedWidth, resolvedHeight).applyLayoutParams(
                    margin = margin,
                ) {
                    this.weight = weight?.weight ?: 0f
                    gravity = when (parent.orientation) {
                        LinearLayout.HORIZONTAL -> verticalAlign?.alignment?.toGravity() ?: -1
                        else -> horizontalAlign?.alignment?.toGravity() ?: -1
                    }
                }
            }

            is DeclarativeBoxLayout -> FrameLayout.LayoutParams(width, height).applyLayoutParams(
                margin = margin,
            ) {
                gravity = boxAlign?.alignment?.toGravity() ?: DeclarativeBoxLayout.UNSET_GRAVITY
            }

            is FrameLayout -> FrameLayout.LayoutParams(width, height).applyLayoutParams(margin = margin)
            else -> ViewGroup.MarginLayoutParams(width, height).applyMargin(margin)
        }
    }

    private fun emitModifierWarnings(
        parent: ViewGroup,
        node: VNode,
        warningTag: String,
        emittedModifierWarnings: MutableSet<String>,
    ) {
        ModifierParentDataValidator.validate(parent, node).forEach { warning ->
            val key = "${parent::class.java.name}|${node.type}|$warning"
            if (emittedModifierWarnings.add(key)) {
                Log.w(warningTag, warning)
            }
        }
    }

    private fun <T : ViewGroup.MarginLayoutParams> T.applyLayoutParams(
        margin: MarginModifierElement?,
        block: T.() -> Unit = {},
    ): T {
        applyMargin(margin)
        block()
        return this
    }

    private fun <T : ViewGroup.MarginLayoutParams> T.applyMargin(
        margin: MarginModifierElement?,
    ): T {
        if (margin == null) {
            setMargins(0, 0, 0, 0)
            return this
        }
        setMargins(
            margin.left,
            margin.top,
            margin.right,
            margin.bottom,
        )
        return this
    }

    private fun defaultDividerWidth(parent: ViewGroup, node: VNode): Int {
        val thickness = ContainerViewBinder.readDividerSpec(node).thickness
        return if ((parent as? LinearLayout)?.orientation == LinearLayout.HORIZONTAL) {
            thickness
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun defaultDividerHeight(parent: ViewGroup, node: VNode): Int {
        val thickness = ContainerViewBinder.readDividerSpec(node).thickness
        return if ((parent as? LinearLayout)?.orientation == LinearLayout.HORIZONTAL) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            thickness
        }
    }

    private fun VerticalAlignment.toGravity(): Int {
        return when (this) {
            VerticalAlignment.Top -> android.view.Gravity.TOP
            VerticalAlignment.Center -> android.view.Gravity.CENTER_VERTICAL
            VerticalAlignment.Bottom -> android.view.Gravity.BOTTOM
        }
    }

    private fun HorizontalAlignment.toGravity(): Int {
        return when (this) {
            HorizontalAlignment.Start -> android.view.Gravity.START
            HorizontalAlignment.Center -> android.view.Gravity.CENTER_HORIZONTAL
            HorizontalAlignment.End -> android.view.Gravity.END
        }
    }

    private fun BoxAlignment.toGravity(): Int {
        return when (this) {
            BoxAlignment.TopStart -> android.view.Gravity.TOP or android.view.Gravity.START
            BoxAlignment.TopCenter -> android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL
            BoxAlignment.TopEnd -> android.view.Gravity.TOP or android.view.Gravity.END
            BoxAlignment.CenterStart -> android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.START
            BoxAlignment.Center -> android.view.Gravity.CENTER
            BoxAlignment.CenterEnd -> android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.END
            BoxAlignment.BottomStart -> android.view.Gravity.BOTTOM or android.view.Gravity.START
            BoxAlignment.BottomCenter -> android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
            BoxAlignment.BottomEnd -> android.view.Gravity.BOTTOM or android.view.Gravity.END
        }
    }
}
