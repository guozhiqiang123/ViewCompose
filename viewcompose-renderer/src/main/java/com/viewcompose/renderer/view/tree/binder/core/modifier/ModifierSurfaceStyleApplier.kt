package com.viewcompose.renderer.view.tree

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.content.res.AppCompatResources
import com.viewcompose.renderer.R
import com.viewcompose.ui.modifier.CornerRadiusModifierElement
import com.viewcompose.renderer.modifier.ResolvedModifiers

internal object ModifierSurfaceStyleApplier {
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
        val corners = if (cornerRadius > 0) {
            CornerRadiusModifierElement(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        } else {
            null
        }
        applyBackgroundAndInteraction(
            view = view,
            backgroundDrawableResId = null,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = corners,
            rippleColor = rippleColor,
            clickable = clickable,
        )
    }

    fun applySurfaceStyle(
        view: View,
        resolved: ResolvedModifiers,
        nodeStyle: NodeStyle,
    ) {
        applyBackgroundAndInteraction(
            view = view,
            backgroundDrawableResId = nodeStyle.backgroundDrawableResId,
            backgroundColor = nodeStyle.backgroundColor,
            borderWidth = nodeStyle.borderWidth,
            borderColor = nodeStyle.borderColor,
            cornerRadius = nodeStyle.cornerRadius,
            rippleColor = nodeStyle.rippleColor,
            clickable = nodeStyle.clickable,
            forceClip = resolved.clip?.clip ?: false,
        )
    }

    fun applyBackgroundAndInteraction(
        view: View,
        backgroundDrawableResId: Int?,
        backgroundColor: Int?,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: CornerRadiusModifierElement?,
        rippleColor: Int,
        clickable: Boolean,
        forceClip: Boolean = false,
    ) {
        val hasCorner = cornerRadius != null &&
            (cornerRadius.topStart > 0 || cornerRadius.topEnd > 0 ||
                cornerRadius.bottomEnd > 0 || cornerRadius.bottomStart > 0)
        val backgroundDrawable = backgroundDrawableResId
            ?.let { loadBackgroundDrawable(view, it) }
        val hasCustomShape = backgroundDrawable != null || backgroundColor != null || hasCorner || borderWidth > 0
        if (hasCustomShape) {
            view.background = if (backgroundDrawable != null) {
                createDrawableResourceBackground(
                    backgroundDrawable = backgroundDrawable,
                    borderWidth = borderWidth,
                    borderColor = borderColor,
                    cornerRadius = cornerRadius,
                    rippleColor = rippleColor,
                    clickable = clickable,
                )
            } else {
                createBackgroundDrawable(
                    backgroundColor = backgroundColor ?: Color.TRANSPARENT,
                    borderWidth = borderWidth,
                    borderColor = borderColor,
                    cornerRadius = cornerRadius,
                    rippleColor = rippleColor,
                    clickable = clickable,
                )
            }
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
        applyCornerOutline(view, cornerRadius, forceClip)
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

    private fun createBackgroundDrawable(
        backgroundColor: Int,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: CornerRadiusModifierElement?,
        rippleColor: Int,
        clickable: Boolean,
    ): Drawable {
        val content = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            if (borderWidth > 0) {
                setStroke(borderWidth, borderColor)
            }
            applyCornerRadiusToDrawable(this, cornerRadius)
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
                applyCornerRadiusToDrawable(this, cornerRadius)
            },
        )
    }

    private fun createDrawableResourceBackground(
        backgroundDrawable: Drawable,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: CornerRadiusModifierElement?,
        rippleColor: Int,
        clickable: Boolean,
    ): Drawable {
        val layeredContent = if (borderWidth > 0) {
            LayerDrawable(
                arrayOf(
                    backgroundDrawable,
                    GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(Color.TRANSPARENT)
                        setStroke(borderWidth, borderColor)
                        applyCornerRadiusToDrawable(this, cornerRadius)
                    },
                ),
            )
        } else {
            backgroundDrawable
        }
        if (!clickable) {
            return layeredContent
        }
        return RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            layeredContent,
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                if (borderWidth > 0) {
                    setStroke(borderWidth, borderColor)
                }
                applyCornerRadiusToDrawable(this, cornerRadius)
            },
        )
    }

    private fun applyCornerRadiusToDrawable(
        drawable: GradientDrawable,
        cornerRadius: CornerRadiusModifierElement?,
    ) {
        if (cornerRadius == null) return
        if (cornerRadius.isUniform) {
            drawable.cornerRadius = cornerRadius.topStart.toFloat()
        } else {
            val tl = cornerRadius.topStart.toFloat()
            val tr = cornerRadius.topEnd.toFloat()
            val br = cornerRadius.bottomEnd.toFloat()
            val bl = cornerRadius.bottomStart.toFloat()
            drawable.cornerRadii = floatArrayOf(tl, tl, tr, tr, br, br, bl, bl)
        }
    }

    private fun applyCornerOutline(
        view: View,
        cornerRadius: CornerRadiusModifierElement?,
        forceClip: Boolean = false,
    ) {
        val hasCorner = cornerRadius != null &&
            (cornerRadius.topStart > 0 || cornerRadius.topEnd > 0 ||
                cornerRadius.bottomEnd > 0 || cornerRadius.bottomStart > 0)
        if (!hasCorner && !forceClip) {
            view.clipToOutline = false
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
            view.invalidateOutline()
            return
        }
        if (cornerRadius != null && hasCorner) {
            if (cornerRadius.isUniform) {
                val r = cornerRadius.topStart.toFloat()
                view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, r)
                    }
                }
            } else {
                val r = maxOf(
                    cornerRadius.topStart,
                    cornerRadius.topEnd,
                    cornerRadius.bottomEnd,
                    cornerRadius.bottomStart,
                ).toFloat()
                view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, r)
                    }
                }
            }
        } else {
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
        }
        // Apply rounded outline for shadow, but only clip content when clip() is explicitly requested.
        view.clipToOutline = forceClip
        view.invalidateOutline()
    }

    private fun loadBackgroundDrawable(
        view: View,
        backgroundDrawableResId: Int,
    ): Drawable? {
        return AppCompatResources.getDrawable(view.context, backgroundDrawableResId)
            ?.mutate()
    }
}
