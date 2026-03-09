package com.viewcompose.renderer.view.tree

import android.graphics.Color
import com.viewcompose.ui.modifier.CornerRadiusModifierElement
import com.viewcompose.ui.modifier.PaddingModifierElement
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.BoxNodeProps
import com.viewcompose.ui.node.spec.ButtonNodeProps
import com.viewcompose.ui.node.spec.IconButtonNodeProps
import com.viewcompose.ui.node.spec.TextFieldNodeProps
import com.viewcompose.ui.node.spec.TextNodeProps
import com.viewcompose.ui.node.spec.ToggleNodeProps
import com.viewcompose.renderer.modifier.ResolvedModifiers

internal object ModifierNodeStyleResolver {
    fun resolveNodeStyle(
        node: VNode,
        resolved: ResolvedModifiers,
        defaultRippleColor: Int,
    ): NodeStyle {
        return NodeStyle(
            backgroundDrawableResId = resolved.backgroundDrawableRes?.resId,
            backgroundColor = resolved.backgroundColor?.color ?: readNodeBackgroundColor(node),
            borderWidth = resolved.border?.width ?: readNodeBorderWidth(node) ?: 0,
            borderColor = resolved.border?.color ?: readNodeBorderColor(node) ?: Color.TRANSPARENT,
            cornerRadius = resolved.cornerRadius
                ?: readNodeCornerRadius(node)?.let {
                    CornerRadiusModifierElement(it, it, it, it)
                },
            padding = resolved.padding ?: readNodePadding(node),
            minHeight = resolved.minHeight?.minHeight ?: readNodeMinHeight(node) ?: 0,
            minWidth = resolved.minWidth?.minWidth ?: 0,
            rippleColor = readNodeRippleColor(node) ?: defaultRippleColor,
            textColor = readNodeTextColor(node),
            textSizeSp = readNodeTextSize(node),
            clickable = resolved.clickable != null || readNodeClickable(node),
        )
    }

    fun resolveHostStyle(
        resolved: ResolvedModifiers,
        nodeStyle: NodeStyle,
    ): HostStyle {
        val hasWindowInsetsPadding = resolved.systemBarsInsetsPadding != null || resolved.imeInsetsPadding != null
        return HostStyle(
            hasWindowInsetsPadding = hasWindowInsetsPadding,
            padding = nodeStyle.padding,
            minHeight = nodeStyle.minHeight,
            minWidth = nodeStyle.minWidth,
        )
    }

    private fun readNodeTextColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.textColor
        is TextNodeProps -> spec.textColor
        is TextFieldNodeProps -> spec.textColor
        is ToggleNodeProps -> spec.textColor
        else -> null
    }

    private fun readNodeTextSize(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.textSizeSp
        is TextNodeProps -> spec.textSizeSp
        is TextFieldNodeProps -> spec.textSizeSp
        is ToggleNodeProps -> spec.textSizeSp
        else -> null
    }

    private fun readNodeBackgroundColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.backgroundColor
        is TextFieldNodeProps -> spec.backgroundColor
        is IconButtonNodeProps -> spec.backgroundColor
        else -> null
    }

    private fun readNodeBorderWidth(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderWidth
        is TextFieldNodeProps -> spec.borderWidth
        is IconButtonNodeProps -> spec.borderWidth
        else -> null
    }

    private fun readNodeBorderColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderColor
        is TextFieldNodeProps -> spec.borderColor
        is IconButtonNodeProps -> spec.borderColor
        else -> null
    }

    private fun readNodeCornerRadius(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.cornerRadius
        is TextFieldNodeProps -> spec.cornerRadius
        is IconButtonNodeProps -> spec.cornerRadius
        else -> null
    }

    private fun readNodeRippleColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.rippleColor
        is IconButtonNodeProps -> spec.rippleColor
        is ToggleNodeProps -> spec.rippleColor
        is BoxNodeProps -> spec.rippleColor
        else -> null
    }

    private fun readNodeClickable(node: VNode): Boolean = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.onClick != null && spec.enabled
        is IconButtonNodeProps -> spec.enabled
        else -> false
    }

    private fun readNodeMinHeight(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.minHeight
        is TextFieldNodeProps -> spec.minHeight
        else -> null
    }

    private fun readNodePadding(node: VNode): PaddingModifierElement? = when (val spec = node.spec) {
        is ButtonNodeProps -> PaddingModifierElement(
            left = spec.paddingHorizontal,
            top = spec.paddingVertical,
            right = spec.paddingHorizontal,
            bottom = spec.paddingVertical,
        )
        is TextFieldNodeProps -> PaddingModifierElement(
            left = spec.paddingHorizontal,
            top = spec.paddingVertical,
            right = spec.paddingHorizontal,
            bottom = spec.paddingVertical,
        )
        is IconButtonNodeProps -> PaddingModifierElement(
            left = spec.contentPadding,
            top = spec.contentPadding,
            right = spec.contentPadding,
            bottom = spec.contentPadding,
        )
        else -> null
    }
}

internal data class NodeStyle(
    val backgroundDrawableResId: Int?,
    val backgroundColor: Int?,
    val borderWidth: Int,
    val borderColor: Int,
    val cornerRadius: CornerRadiusModifierElement?,
    val padding: PaddingModifierElement?,
    val minHeight: Int,
    val minWidth: Int,
    val rippleColor: Int,
    val textColor: Int?,
    val textSizeSp: Int?,
    val clickable: Boolean,
)

internal data class HostStyle(
    val hasWindowInsetsPadding: Boolean,
    val padding: PaddingModifierElement?,
    val minHeight: Int,
    val minWidth: Int,
)
