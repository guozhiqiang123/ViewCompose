package com.viewcompose.renderer.view.container

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.viewcompose.renderer.view.tree.LayoutPassTracker
import kotlin.math.roundToInt

internal class DeclarativeAnimatedVisibilityHostLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    var widthScale: Float = 1f
        set(value) {
            val clamped = value.coerceAtLeast(0f)
            if (field == clamped) return
            field = clamped
            requestLayout()
        }

    var heightScale: Float = 1f
        set(value) {
            val clamped = value.coerceAtLeast(0f)
            if (field == clamped) return
            field = clamped
            requestLayout()
        }

    var clipToBounds: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            clipChildren = value
            clipToPadding = value
            invalidate()
        }

    init {
        clipChildren = true
        clipToPadding = true
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        val startNs = System.nanoTime()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val scaledWidth = (measuredWidth * widthScale).roundToInt().coerceAtLeast(0)
        val scaledHeight = (measuredHeight * heightScale).roundToInt().coerceAtLeast(0)
        setMeasuredDimension(
            resolveSize(scaledWidth, widthMeasureSpec),
            resolveSize(scaledHeight, heightMeasureSpec),
        )
        LayoutPassTracker.recordMeasure(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val startNs = System.nanoTime()
        super.onLayout(changed, left, top, right, bottom)
        LayoutPassTracker.recordLayout(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }
}
