package com.viewcompose.renderer.view.tree

import android.view.View
import com.viewcompose.renderer.modifier.ResolvedModifiers
import com.viewcompose.renderer.modifier.resolve
import com.viewcompose.renderer.node.VNode

internal object ViewModifierApplier {
    fun bindView(
        view: View,
        node: VNode,
        defaultRippleColor: Int,
        resolved: ResolvedModifiers = node.modifier.resolve(),
    ) {
        applyModifier(
            view = view,
            node = node,
            defaultRippleColor = defaultRippleColor,
            resolved = resolved,
        )
        NodeViewBinderRegistry.bind(view, node)
        ModifierInteractionApplier.applyNativeViewConfigs(view, node)
    }

    fun cacheOriginalBackground(view: View) {
        ModifierSurfaceStyleApplier.cacheOriginalBackground(view)
    }

    fun cacheOriginalForeground(view: View) {
        ModifierSurfaceStyleApplier.cacheOriginalForeground(view)
    }

    fun applyStylePatch(
        view: View,
        backgroundColor: Int,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: Int,
        rippleColor: Int,
        clickable: Boolean,
    ) {
        ModifierSurfaceStyleApplier.applyStylePatch(
            view = view,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = cornerRadius,
            rippleColor = rippleColor,
            clickable = clickable,
        )
    }

    fun applyModifier(
        view: View,
        node: VNode,
        defaultRippleColor: Int,
        resolved: ResolvedModifiers = node.modifier.resolve(),
    ) {
        ModifierContainerPolicyApplier.applyScrollableContainerPolicies(
            view = view,
            node = node,
        )
        val nodeStyle = ModifierNodeStyleResolver.resolveNodeStyle(
            node = node,
            resolved = resolved,
            defaultRippleColor = defaultRippleColor,
        )
        val hostStyle = ModifierNodeStyleResolver.resolveHostStyle(
            resolved = resolved,
            nodeStyle = nodeStyle,
        )
        ModifierSurfaceStyleApplier.applySurfaceStyle(
            view = view,
            resolved = resolved,
            nodeStyle = nodeStyle,
        )
        ModifierInteractionApplier.applyCommonHostProperties(
            view = view,
            resolved = resolved,
            minHeight = hostStyle.minHeight,
            minWidth = hostStyle.minWidth,
        )
        ModifierInteractionApplier.applyClickAndFocusState(
            view = view,
            node = node,
            resolved = resolved,
        )
        ModifierInsetsApplier.applyHostPaddingWhenNoInsets(
            view = view,
            hasWindowInsetsPadding = hostStyle.hasWindowInsetsPadding,
            hostPadding = hostStyle.padding,
        )
        ModifierInsetsApplier.applyWindowInsetsPadding(
            view = view,
            systemBarsModifier = resolved.systemBarsInsetsPadding,
            imeModifier = resolved.imeInsetsPadding,
            basePadding = if (hostStyle.hasWindowInsetsPadding) {
                nodeStyle.padding
            } else {
                null
            },
        )
        ModifierInteractionApplier.applyTextAppearanceIfTextView(
            view = view,
            textColor = nodeStyle.textColor,
            textSizeSp = nodeStyle.textSizeSp,
        )
    }
}
