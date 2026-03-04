package com.gzq.uiframework.renderer.view.tree

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.modifier.AlphaModifierElement
import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.BorderModifierElement
import com.gzq.uiframework.renderer.modifier.ClickableModifierElement
import com.gzq.uiframework.renderer.modifier.CornerRadiusModifierElement
import com.gzq.uiframework.renderer.modifier.MinHeightModifierElement
import com.gzq.uiframework.renderer.modifier.NativeViewElement
import com.gzq.uiframework.renderer.modifier.OffsetModifierElement
import com.gzq.uiframework.renderer.modifier.PaddingModifierElement
import com.gzq.uiframework.renderer.modifier.RippleColorModifierElement
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.VisibilityModifierElement
import com.gzq.uiframework.renderer.modifier.ZIndexModifierElement
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout

internal object ViewModifierApplier {
    fun bindView(
        view: View,
        node: VNode,
        defaultRippleColor: Int,
    ) {
        applyModifier(
            view = view,
            node = node,
            defaultRippleColor = defaultRippleColor,
        )
        NodeViewBinderRegistry.bind(view, node)
        applyNativeViewConfigs(view, node)
    }

    fun cacheOriginalBackground(view: View) {
        if (view.getTag(R.id.ui_framework_original_background) != null) {
            return
        }
        view.setTag(
            R.id.ui_framework_original_background,
            cloneDrawable(view.background),
        )
    }

    fun cacheOriginalForeground(view: View) {
        if (view.getTag(R.id.ui_framework_original_foreground) != null) {
            return
        }
        view.setTag(
            R.id.ui_framework_original_foreground,
            cloneDrawable(view.foreground),
        )
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
        applyBackgroundAndInteraction(
            view = view,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = cornerRadius,
            rippleColor = rippleColor,
            clickable = clickable,
        )
    }

    private fun applyModifier(
        view: View,
        node: VNode,
        defaultRippleColor: Int,
    ) {
        val alpha = node.modifier.elements
            .lastOrNull { it is AlphaModifierElement } as? AlphaModifierElement
        val backgroundColor = node.modifier.elements
            .lastOrNull { it is BackgroundColorModifierElement } as? BackgroundColorModifierElement
        val clickable = node.modifier.elements
            .lastOrNull { it is ClickableModifierElement } as? ClickableModifierElement
        val border = node.modifier.elements
            .lastOrNull { it is BorderModifierElement } as? BorderModifierElement
        val cornerRadius = node.modifier.elements
            .lastOrNull { it is CornerRadiusModifierElement } as? CornerRadiusModifierElement
        val offset = node.modifier.elements
            .lastOrNull { it is OffsetModifierElement } as? OffsetModifierElement
        val padding = node.modifier.elements.lastOrNull { it is PaddingModifierElement } as? PaddingModifierElement
        val minHeight = node.modifier.elements
            .lastOrNull { it is MinHeightModifierElement } as? MinHeightModifierElement
        val rippleColor = node.modifier.elements
            .lastOrNull { it is RippleColorModifierElement } as? RippleColorModifierElement
        val visibility = node.modifier.elements
            .lastOrNull { it is VisibilityModifierElement } as? VisibilityModifierElement
        val zIndex = node.modifier.elements
            .lastOrNull { it is ZIndexModifierElement } as? ZIndexModifierElement
        val resolvedAlpha = alpha?.alpha ?: readNodeAlpha(node) ?: 1f
        val resolvedBackgroundColor = backgroundColor?.color ?: readNodeBackgroundColor(node)
        val resolvedBorderWidth = border?.width ?: readNodeBorderWidth(node) ?: 0
        val resolvedBorderColor = border?.color ?: readNodeBorderColor(node) ?: Color.TRANSPARENT
        val resolvedCornerRadius = cornerRadius?.radius ?: readNodeCornerRadius(node) ?: 0
        val resolvedPadding = padding ?: readNodePadding(node)
        val resolvedMinHeight = minHeight?.minHeight ?: readNodeMinHeight(node) ?: 0
        val resolvedRippleColor = rippleColor?.color ?: readNodeRippleColor(node) ?: defaultRippleColor
        val textColor = readNodeTextColor(node)
        val textSizeSp = readNodeTextSize(node)
        val anchorId = readAnchorId(node)
        view.alpha = resolvedAlpha
        if (view is DeclarativeTextFieldLayout) {
            applyAnchorId(view, anchorId)
            view.visibility = when (visibility?.visibility ?: Visibility.Visible) {
                Visibility.Visible -> View.VISIBLE
                Visibility.Invisible -> View.INVISIBLE
                Visibility.Gone -> View.GONE
            }
            view.translationX = offset?.x ?: 0f
            view.translationY = offset?.y ?: 0f
            view.z = zIndex?.zIndex ?: 0f
            view.isClickable = false
            view.setOnClickListener(null)
            view.minimumHeight = 0
            view.setPadding(0, 0, 0, 0)
            applyTextFieldModifier(
                layout = view,
                backgroundColor = resolvedBackgroundColor,
                borderWidth = resolvedBorderWidth,
                borderColor = resolvedBorderColor,
                cornerRadius = resolvedCornerRadius,
                rippleColor = resolvedRippleColor,
                padding = resolvedPadding,
                minHeight = resolvedMinHeight,
                textColor = textColor,
                textSizeSp = textSizeSp,
            )
            return
        }
        applyBackgroundAndInteraction(
            view = view,
            backgroundColor = resolvedBackgroundColor,
            borderWidth = resolvedBorderWidth,
            borderColor = resolvedBorderColor,
            cornerRadius = resolvedCornerRadius,
            rippleColor = resolvedRippleColor,
            clickable = clickable != null,
        )
        applyAnchorId(view, anchorId)
        view.visibility = when (visibility?.visibility ?: Visibility.Visible) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
        view.translationX = offset?.x ?: 0f
        view.translationY = offset?.y ?: 0f
        view.z = zIndex?.zIndex ?: 0f
        view.minimumHeight = resolvedMinHeight
        view.isClickable = clickable != null
        view.setOnClickListener(
            if (clickable == null) {
                null
            } else {
                View.OnClickListener { clickable.onClick() }
            },
        )
        if (resolvedPadding == null) {
            view.setPadding(0, 0, 0, 0)
        } else {
            view.setPadding(
                resolvedPadding.left,
                resolvedPadding.top,
                resolvedPadding.right,
                resolvedPadding.bottom,
            )
        }
        if (view is TextView) {
            if (textColor != null) {
                view.setTextColor(textColor)
            }
            if (textSizeSp != null) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
            }
        }
    }

    private fun restoreOriginalBackground(view: View) {
        view.background = cloneDrawable(
            view.getTag(R.id.ui_framework_original_background) as? Drawable,
        )
    }

    private fun restoreOriginalForeground(view: View) {
        view.foreground = cloneDrawable(
            view.getTag(R.id.ui_framework_original_foreground) as? Drawable,
        )
    }

    private fun cloneDrawable(drawable: Drawable?): Drawable? {
        return drawable?.constantState?.newDrawable()?.mutate() ?: drawable?.mutate()
    }

    private fun applyAnchorId(
        view: View,
        anchorId: String?,
    ) {
        view.setTag(R.id.ui_framework_anchor_id, anchorId)
    }

    private fun applyBackgroundAndInteraction(
        view: View,
        backgroundColor: Int?,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: Int,
        rippleColor: Int,
        clickable: Boolean,
    ) {
        val hasCustomShape = backgroundColor != null || cornerRadius > 0 || borderWidth > 0
        if (hasCustomShape) {
            view.background = createBackgroundDrawable(
                backgroundColor = backgroundColor ?: Color.TRANSPARENT,
                borderWidth = borderWidth,
                borderColor = borderColor,
                cornerRadiusPx = cornerRadius,
                rippleColor = rippleColor,
                clickable = clickable,
            )
            view.foreground = null
        } else {
            restoreOriginalBackground(view)
            if (clickable) {
                view.foreground = RippleDrawable(
                    ColorStateList.valueOf(rippleColor),
                    null,
                    null,
                )
            } else {
                restoreOriginalForeground(view)
            }
        }
        applyCornerOutline(view, cornerRadius)
    }

    private fun createBackgroundDrawable(
        backgroundColor: Int,
        borderWidth: Int,
        borderColor: Int,
        cornerRadiusPx: Int,
        rippleColor: Int,
        clickable: Boolean,
    ): Drawable {
        val content = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            if (borderWidth > 0) {
                setStroke(borderWidth, borderColor)
            }
            cornerRadius = cornerRadiusPx.toFloat()
        }
        if (!clickable) {
            return content
        }
        return RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            content,
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                if (borderWidth > 0) {
                    setStroke(borderWidth, borderColor)
                }
                cornerRadius = cornerRadiusPx.toFloat()
            },
        )
    }

    private fun applyCornerOutline(
        view: View,
        cornerRadius: Int,
    ) {
        if (cornerRadius <= 0) {
            view.clipToOutline = false
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
            view.invalidateOutline()
            return
        }
        view.clipToOutline = true
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius.toFloat())
            }
        }
        view.invalidateOutline()
    }

    private fun applyTextFieldModifier(
        layout: DeclarativeTextFieldLayout,
        backgroundColor: Int?,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: Int,
        rippleColor: Int,
        padding: PaddingModifierElement?,
        minHeight: Int,
        textColor: Int?,
        textSizeSp: Int?,
    ) {
        applyBackgroundAndInteraction(
            view = layout.fieldContainer,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = cornerRadius,
            rippleColor = rippleColor,
            clickable = false,
        )
        if (padding == null) {
            layout.fieldContainer.setPadding(0, 0, 0, 0)
        } else {
            layout.fieldContainer.setPadding(
                padding.left,
                padding.top,
                padding.right,
                padding.bottom,
            )
        }
        layout.fieldContainer.minimumHeight = minHeight
        textColor?.let(layout.inputView::setTextColor)
        if (textSizeSp != null) {
            layout.inputView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
        }
    }

    private fun readNodeTextColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.textColor
        is TextNodeProps -> spec.textColor
        is TextFieldNodeProps -> spec.textColor
        is ToggleNodeProps -> spec.textColor
        else -> node.props[TypedPropKeys.TextColor]
    }

    private fun readNodeTextSize(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.textSizeSp
        is TextNodeProps -> spec.textSizeSp
        is TextFieldNodeProps -> spec.textSizeSp
        is ToggleNodeProps -> spec.textSizeSp
        else -> node.props[TypedPropKeys.TextSizeSp]
    }

    private fun readNodeAlpha(node: VNode): Float? {
        return node.props[TypedPropKeys.StyleAlpha]
    }

    private fun readNodeBackgroundColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.backgroundColor
        is TextFieldNodeProps -> spec.backgroundColor
        is IconButtonNodeProps -> spec.backgroundColor
        else -> node.props[TypedPropKeys.StyleBackgroundColor]
    }

    private fun readNodeBorderWidth(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderWidth
        is TextFieldNodeProps -> spec.borderWidth
        is IconButtonNodeProps -> spec.borderWidth
        else -> node.props[TypedPropKeys.StyleBorderWidth]
    }

    private fun readNodeBorderColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderColor
        is TextFieldNodeProps -> spec.borderColor
        is IconButtonNodeProps -> spec.borderColor
        else -> node.props[TypedPropKeys.StyleBorderColor]
    }

    private fun readNodeCornerRadius(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.cornerRadius
        is TextFieldNodeProps -> spec.cornerRadius
        is IconButtonNodeProps -> spec.cornerRadius
        else -> node.props[TypedPropKeys.StyleCornerRadius]
    }

    private fun readNodeRippleColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.rippleColor
        is TextFieldNodeProps -> spec.rippleColor
        is IconButtonNodeProps -> spec.rippleColor
        is ToggleNodeProps -> spec.rippleColor
        else -> node.props[TypedPropKeys.StyleRippleColor]
    }

    private fun readNodeMinHeight(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.minHeight
        is TextFieldNodeProps -> spec.minHeight
        else -> node.props[TypedPropKeys.StyleMinHeight]
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
        else -> {
            val left = node.props[TypedPropKeys.StylePaddingLeft] ?: return null
            val top = node.props[TypedPropKeys.StylePaddingTop] ?: return null
            val right = node.props[TypedPropKeys.StylePaddingRight] ?: return null
            val bottom = node.props[TypedPropKeys.StylePaddingBottom] ?: return null
            PaddingModifierElement(left = left, top = top, right = right, bottom = bottom)
        }
    }

    private fun readAnchorId(node: VNode): String? {
        return node.props[TypedPropKeys.AnchorId]
    }

    private fun applyNativeViewConfigs(view: View, node: VNode) {
        for (element in node.modifier.elements) {
            if (element is NativeViewElement) {
                element.configure(view)
            }
        }
    }
}
