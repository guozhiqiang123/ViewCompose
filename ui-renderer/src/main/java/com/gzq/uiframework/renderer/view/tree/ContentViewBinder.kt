package com.gzq.uiframework.renderer.view.tree

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.node.VNode

internal object ContentViewBinder {
    data class TextSpec(
        val text: CharSequence?,
        val maxLines: Int,
        val overflow: TextOverflow,
        val gravity: Int,
    )

    data class ButtonSpec(
        val text: CharSequence?,
        val enabled: Boolean,
        val iconSpacing: Int,
        val leadingIcon: ImageSource.Resource?,
        val trailingIcon: ImageSource.Resource?,
        val iconTint: Int,
        val iconSize: Int,
        val onClick: (() -> Unit)?,
    )

    fun bindText(
        view: TextView,
        spec: TextSpec,
    ) {
        view.text = spec.text
        view.maxLines = spec.maxLines
        view.ellipsize = when (spec.overflow) {
            TextOverflow.Clip -> null
            TextOverflow.Ellipsis -> TextUtils.TruncateAt.END
        }
        view.gravity = spec.gravity
    }

    fun bindButton(
        view: Button,
        spec: ButtonSpec,
    ) {
        view.text = spec.text
        view.isEnabled = spec.enabled
        view.isAllCaps = false
        view.setSingleLine(false)
        view.maxLines = 2
        view.ellipsize = TextUtils.TruncateAt.END
        view.gravity = Gravity.CENTER
        view.minimumWidth = 0
        view.minWidth = 0
        view.compoundDrawablePadding = spec.iconSpacing
        view.setCompoundDrawablesRelative(
            resolveButtonIconDrawable(
                view = view,
                source = spec.leadingIcon,
                tint = spec.iconTint,
                size = spec.iconSize,
            ),
            null,
            resolveButtonIconDrawable(
                view = view,
                source = spec.trailingIcon,
                tint = spec.iconTint,
                size = spec.iconSize,
            ),
            null,
        )
        view.setOnClickListener {
            if (spec.enabled) {
                spec.onClick?.invoke()
            }
        }
    }

    fun readTextSpec(node: VNode): TextSpec {
        return TextSpec(
            text = node.props.values[PropKeys.TEXT] as? CharSequence,
            maxLines = node.props.values[PropKeys.TEXT_MAX_LINES] as? Int ?: Int.MAX_VALUE,
            overflow = node.props.values[PropKeys.TEXT_OVERFLOW] as? TextOverflow ?: TextOverflow.Clip,
            gravity = (node.props.values[PropKeys.TEXT_ALIGN] as? com.gzq.uiframework.renderer.node.TextAlign
                ?: com.gzq.uiframework.renderer.node.TextAlign.Start).toTextGravity(),
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun readButtonSpec(node: VNode, contentColor: Int): ButtonSpec {
        return ButtonSpec(
            text = node.props.values[PropKeys.TEXT] as? CharSequence,
            enabled = node.props.values[PropKeys.ENABLED] as? Boolean ?: true,
            iconSpacing = node.props.values[PropKeys.BUTTON_ICON_SPACING] as? Int ?: 8,
            leadingIcon = node.props.values[PropKeys.BUTTON_LEADING_ICON] as? ImageSource.Resource,
            trailingIcon = node.props.values[PropKeys.BUTTON_TRAILING_ICON] as? ImageSource.Resource,
            iconTint = contentColor,
            iconSize = node.props.values[PropKeys.BUTTON_ICON_SIZE] as? Int ?: 18,
            onClick = node.props.values[PropKeys.ON_CLICK] as? (() -> Unit),
        )
    }

    private fun com.gzq.uiframework.renderer.node.TextAlign.toTextGravity(): Int {
        return when (this) {
            com.gzq.uiframework.renderer.node.TextAlign.Start -> Gravity.START or Gravity.CENTER_VERTICAL
            com.gzq.uiframework.renderer.node.TextAlign.Center -> Gravity.CENTER
            com.gzq.uiframework.renderer.node.TextAlign.End -> Gravity.END or Gravity.CENTER_VERTICAL
        }
    }

    private fun resolveButtonIconDrawable(
        view: TextView,
        source: ImageSource.Resource?,
        tint: Int,
        size: Int,
    ): Drawable? {
        val drawable = source
            ?.let { ContextCompat.getDrawable(view.context, it.resId) }
            ?.mutate()
            ?: return null
        drawable.setTint(tint)
        drawable.setBounds(0, 0, size, size)
        return drawable
    }
}
