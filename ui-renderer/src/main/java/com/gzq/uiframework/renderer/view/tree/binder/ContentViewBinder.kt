package com.gzq.uiframework.renderer.view.tree

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps

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
        val spec = node.spec as? TextNodeProps
        if (spec != null) {
            return TextSpec(
                text = spec.text,
                maxLines = spec.maxLines,
                overflow = spec.overflow,
                gravity = spec.textAlign.toTextGravity(),
            )
        }
        return TextSpec(
            text = node.props[TypedPropKeys.Text],
            maxLines = node.props[TypedPropKeys.TextMaxLines] ?: Int.MAX_VALUE,
            overflow = node.props[TypedPropKeys.TextOverflow] ?: TextOverflow.Clip,
            gravity = (node.props[TypedPropKeys.TextAlign]
                ?: com.gzq.uiframework.renderer.node.TextAlign.Start).toTextGravity(),
        )
    }

    fun readButtonSpec(node: VNode, contentColor: Int): ButtonSpec {
        val spec = node.spec as? ButtonNodeProps
        if (spec != null) {
            return ButtonSpec(
                text = spec.text,
                enabled = spec.enabled,
                iconSpacing = spec.iconSpacing,
                leadingIcon = spec.leadingIcon,
                trailingIcon = spec.trailingIcon,
                iconTint = spec.iconTint,
                iconSize = spec.iconSize,
                onClick = spec.onClick,
            )
        }
        return ButtonSpec(
            text = node.props[TypedPropKeys.Text],
            enabled = node.props[TypedPropKeys.Enabled] ?: true,
            iconSpacing = node.props[TypedPropKeys.ButtonIconSpacing] ?: 8,
            leadingIcon = node.props[TypedPropKeys.ButtonLeadingIcon],
            trailingIcon = node.props[TypedPropKeys.ButtonTrailingIcon],
            iconTint = contentColor,
            iconSize = node.props[TypedPropKeys.ButtonIconSize] ?: 18,
            onClick = node.props[TypedPropKeys.OnClick],
        )
    }

    internal fun toTextGravity(alignment: com.gzq.uiframework.renderer.node.TextAlign): Int {
        return alignment.toTextGravity()
    }

    internal fun resolveButtonIconDrawable(
        view: TextView,
        source: ImageSource.Resource?,
        tint: Int,
        size: Int,
    ): Drawable? {
        return createButtonIconDrawable(
            view = view,
            source = source,
            tint = tint,
            size = size,
        )
    }

    private fun com.gzq.uiframework.renderer.node.TextAlign.toTextGravity(): Int {
        return when (this) {
            com.gzq.uiframework.renderer.node.TextAlign.Start -> Gravity.START or Gravity.CENTER_VERTICAL
            com.gzq.uiframework.renderer.node.TextAlign.Center -> Gravity.CENTER
            com.gzq.uiframework.renderer.node.TextAlign.End -> Gravity.END or Gravity.CENTER_VERTICAL
        }
    }

    private fun createButtonIconDrawable(
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
