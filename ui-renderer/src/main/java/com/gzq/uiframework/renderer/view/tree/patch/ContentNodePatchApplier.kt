package com.gzq.uiframework.renderer.view.tree.patch

import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.view.tree.ButtonNodePatch
import com.gzq.uiframework.renderer.view.tree.ContentViewBinder
import com.gzq.uiframework.renderer.view.tree.DividerNodePatch
import com.gzq.uiframework.renderer.view.tree.TextNodePatch
import com.gzq.uiframework.renderer.view.tree.ViewModifierApplier

internal object ContentNodePatchApplier {
    fun applyTextPatch(
        view: TextView,
        patch: TextNodePatch,
    ) {
        if (patch.previous.text != patch.next.text) {
            view.text = patch.next.text
        }
        if (patch.previous.maxLines != patch.next.maxLines) {
            view.maxLines = patch.next.maxLines
        }
        if (patch.previous.overflow != patch.next.overflow) {
            view.ellipsize = when (patch.next.overflow) {
                TextOverflow.Clip -> null
                TextOverflow.Ellipsis -> TextUtils.TruncateAt.END
            }
        }
        if (patch.previous.textAlign != patch.next.textAlign) {
            view.gravity = ContentViewBinder.toTextGravity(patch.next.textAlign)
        }
        if (patch.previous.textColor != patch.next.textColor) {
            view.setTextColor(patch.next.textColor)
        }
        if (patch.previous.textSizeSp != patch.next.textSizeSp) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, patch.next.textSizeSp.toFloat())
        }
    }

    fun applyButtonPatch(
        view: Button,
        patch: ButtonNodePatch,
    ) {
        if (patch.previous.text != patch.next.text) {
            view.text = patch.next.text
        }
        if (patch.previous.enabled != patch.next.enabled) {
            view.isEnabled = patch.next.enabled
        }
        if (patch.previous.iconSpacing != patch.next.iconSpacing) {
            view.compoundDrawablePadding = patch.next.iconSpacing
        }
        if (
            patch.previous.leadingIcon != patch.next.leadingIcon ||
            patch.previous.trailingIcon != patch.next.trailingIcon ||
            patch.previous.iconTint != patch.next.iconTint ||
            patch.previous.iconSize != patch.next.iconSize
        ) {
            view.setCompoundDrawablesRelative(
                ContentViewBinder.resolveButtonIconDrawable(
                    view = view,
                    source = patch.next.leadingIcon,
                    tint = patch.next.iconTint,
                    size = patch.next.iconSize,
                ),
                null,
                ContentViewBinder.resolveButtonIconDrawable(
                    view = view,
                    source = patch.next.trailingIcon,
                    tint = patch.next.iconTint,
                    size = patch.next.iconSize,
                ),
                null,
            )
        }
        if (
            patch.previous.onClick != patch.next.onClick ||
            patch.previous.enabled != patch.next.enabled
        ) {
            view.setOnClickListener {
                if (patch.next.enabled) {
                    patch.next.onClick?.invoke()
                }
            }
        }
        if (patch.previous.textColor != patch.next.textColor) {
            view.setTextColor(patch.next.textColor)
        }
        if (patch.previous.textSizeSp != patch.next.textSizeSp) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, patch.next.textSizeSp.toFloat())
        }
        if (hasStyleChange(patch)) {
            ViewModifierApplier.applyStylePatch(
                view = view,
                backgroundColor = patch.next.backgroundColor,
                borderWidth = patch.next.borderWidth,
                borderColor = patch.next.borderColor,
                cornerRadius = patch.next.cornerRadius,
                rippleColor = patch.next.rippleColor,
                clickable = true,
            )
        }
        if (patch.previous.minHeight != patch.next.minHeight) {
            view.minimumHeight = patch.next.minHeight
        }
        if (
            patch.previous.paddingHorizontal != patch.next.paddingHorizontal ||
            patch.previous.paddingVertical != patch.next.paddingVertical
        ) {
            view.setPadding(
                patch.next.paddingHorizontal,
                patch.next.paddingVertical,
                patch.next.paddingHorizontal,
                patch.next.paddingVertical,
            )
        }
    }

    fun applyDividerPatch(
        view: View,
        patch: DividerNodePatch,
    ) {
        if (patch.previous.color != patch.next.color) {
            view.setBackgroundColor(patch.next.color)
        }
    }

    private fun hasStyleChange(patch: ButtonNodePatch): Boolean {
        return patch.previous.backgroundColor != patch.next.backgroundColor ||
            patch.previous.borderWidth != patch.next.borderWidth ||
            patch.previous.borderColor != patch.next.borderColor ||
            patch.previous.cornerRadius != patch.next.cornerRadius ||
            patch.previous.rippleColor != patch.next.rippleColor
    }
}
