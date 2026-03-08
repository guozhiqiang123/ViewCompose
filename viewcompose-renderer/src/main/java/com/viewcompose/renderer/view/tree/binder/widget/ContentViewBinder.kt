package com.viewcompose.renderer.view.tree

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.viewcompose.renderer.node.ImageSource
import com.viewcompose.renderer.node.TextDecoration
import com.viewcompose.renderer.node.TextOverflow
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.ButtonNodeProps
import com.viewcompose.renderer.node.spec.TextNodeProps

internal object ContentViewBinder {
    data class TextSpec(
        val text: CharSequence?,
        val maxLines: Int,
        val overflow: TextOverflow,
        val gravity: Int,
        val fontWeight: Int? = null,
        val fontFamily: Typeface? = null,
        val letterSpacingEm: Float? = null,
        val lineHeightSp: Int? = null,
        val includeFontPadding: Boolean = false,
        val textDecoration: TextDecoration = TextDecoration.None,
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
        applyTypeface(view, spec.fontWeight, spec.fontFamily)
        if (spec.letterSpacingEm != null) {
            view.letterSpacing = spec.letterSpacingEm
        }
        if (spec.lineHeightSp != null) {
            TextViewCompat.setLineHeight(
                view,
                TypedValue.COMPLEX_UNIT_SP,
                spec.lineHeightSp.toFloat(),
            )
        }
        view.includeFontPadding = spec.includeFontPadding
        applyTextDecoration(view, spec.textDecoration)
    }

    fun bindButton(
        view: Button,
        spec: ButtonSpec,
    ) {
        view.text = spec.text
        view.isEnabled = spec.enabled
        view.isAllCaps = false
        view.stateListAnimator = null
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
        val spec = node.spec as? TextNodeProps ?: TextNodeProps(
            text = null,
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Clip,
            textAlign = com.viewcompose.renderer.node.TextAlign.Start,
            textColor = 0xFF000000.toInt(),
            textSizeSp = 14,
        )
        return TextSpec(
            text = spec.text,
            maxLines = spec.maxLines,
            overflow = spec.overflow,
            gravity = spec.textAlign.toTextGravity(),
            fontWeight = spec.fontWeight,
            fontFamily = spec.fontFamily,
            letterSpacingEm = spec.letterSpacingEm,
            lineHeightSp = spec.lineHeightSp,
            includeFontPadding = spec.includeFontPadding,
            textDecoration = spec.textDecoration,
        )
    }

    fun readButtonSpec(node: VNode): ButtonSpec {
        val spec = node.spec as? ButtonNodeProps ?: ButtonNodeProps(
            text = "",
            enabled = true,
            onClick = null,
            textColor = 0xFF000000.toInt(),
            textSizeSp = 14,
            backgroundColor = android.graphics.Color.TRANSPARENT,
            borderWidth = 0,
            borderColor = android.graphics.Color.TRANSPARENT,
            cornerRadius = 0,
            rippleColor = 0x22000000,
            minHeight = 0,
            paddingHorizontal = 0,
            paddingVertical = 0,
            leadingIcon = null,
            trailingIcon = null,
            iconTint = 0xFF000000.toInt(),
            iconSize = 18,
            iconSpacing = 8,
        )
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

    internal fun toTextGravity(alignment: com.viewcompose.renderer.node.TextAlign): Int {
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

    private fun com.viewcompose.renderer.node.TextAlign.toTextGravity(): Int {
        return when (this) {
            com.viewcompose.renderer.node.TextAlign.Start -> Gravity.START or Gravity.CENTER_VERTICAL
            com.viewcompose.renderer.node.TextAlign.Center -> Gravity.CENTER
            com.viewcompose.renderer.node.TextAlign.End -> Gravity.END or Gravity.CENTER_VERTICAL
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

    internal fun applyTypeface(
        view: TextView,
        fontWeight: Int?,
        fontFamily: Typeface?,
    ) {
        if (fontWeight == null && fontFamily == null) return
        val base = fontFamily ?: view.typeface ?: Typeface.DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && fontWeight != null) {
            view.typeface = Typeface.create(base, fontWeight, false)
        } else {
            val style = when {
                fontWeight != null && fontWeight >= 700 -> Typeface.BOLD
                else -> Typeface.NORMAL
            }
            view.setTypeface(base, style)
        }
    }

    internal fun applyTextDecoration(view: TextView, decoration: TextDecoration) {
        val flags = view.paintFlags and
            (Paint.UNDERLINE_TEXT_FLAG or Paint.STRIKE_THRU_TEXT_FLAG).inv()
        view.paintFlags = flags or when (decoration) {
            TextDecoration.None -> 0
            TextDecoration.Underline -> Paint.UNDERLINE_TEXT_FLAG
            TextDecoration.LineThrough -> Paint.STRIKE_THRU_TEXT_FLAG
            TextDecoration.UnderlineLineThrough ->
                Paint.UNDERLINE_TEXT_FLAG or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }
}
