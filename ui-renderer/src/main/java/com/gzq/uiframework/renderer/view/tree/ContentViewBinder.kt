package com.gzq.uiframework.renderer.view.tree

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.TextOverflow

internal object ContentViewBinder {
    fun bindText(
        view: TextView,
        text: CharSequence?,
        maxLines: Int,
        overflow: TextOverflow,
        gravity: Int,
    ) {
        view.text = text
        view.maxLines = maxLines
        view.ellipsize = when (overflow) {
            TextOverflow.Clip -> null
            TextOverflow.Ellipsis -> TextUtils.TruncateAt.END
        }
        view.gravity = gravity
    }

    fun bindButton(
        view: Button,
        text: CharSequence?,
        enabled: Boolean,
        iconSpacing: Int,
        leadingIcon: ImageSource.Resource?,
        trailingIcon: ImageSource.Resource?,
        iconTint: Int,
        iconSize: Int,
        onClick: (() -> Unit)?,
    ) {
        view.text = text
        view.isEnabled = enabled
        view.isAllCaps = false
        view.setSingleLine(false)
        view.maxLines = 2
        view.ellipsize = TextUtils.TruncateAt.END
        view.gravity = Gravity.CENTER
        view.minimumWidth = 0
        view.minWidth = 0
        view.compoundDrawablePadding = iconSpacing
        view.setCompoundDrawablesRelative(
            resolveButtonIconDrawable(
                view = view,
                source = leadingIcon,
                tint = iconTint,
                size = iconSize,
            ),
            null,
            resolveButtonIconDrawable(
                view = view,
                source = trailingIcon,
                tint = iconTint,
                size = iconSize,
            ),
            null,
        )
        view.setOnClickListener {
            if (enabled) {
                onClick?.invoke()
            }
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
