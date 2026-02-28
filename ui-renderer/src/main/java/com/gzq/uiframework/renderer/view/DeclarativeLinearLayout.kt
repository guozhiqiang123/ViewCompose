package com.gzq.uiframework.renderer.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import kotlin.math.max

internal class DeclarativeLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    var itemSpacing: Int = 0
        set(value) {
            field = value
            updateSpacingDivider()
            requestLayout()
        }

    var mainAxisArrangement: MainAxisArrangement = MainAxisArrangement.Start
        set(value) {
            field = value
            requestLayout()
        }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val visibleChildren = (0 until childCount)
            .map(::getChildAt)
            .filter { child -> child.visibility != View.GONE }
        if (visibleChildren.isEmpty()) {
            return
        }
        if (orientation == HORIZONTAL) {
            layoutHorizontally(visibleChildren)
        } else {
            layoutVertically(visibleChildren)
        }
    }

    private fun layoutHorizontally(
        children: List<View>,
    ) {
        val innerWidth = width - paddingLeft - paddingRight
        val innerHeight = height - paddingTop - paddingBottom
        val totalChildrenWidth = children.sumOf { child ->
            val params = child.layoutParams as MarginLayoutParams
            child.measuredWidth + params.leftMargin + params.rightMargin
        }
        val baseSpacing = if (children.size > 1) itemSpacing * (children.size - 1) else 0
        val consumedWidth = totalChildrenWidth + baseSpacing
        val extraSpace = max(0, innerWidth - consumedWidth)
        val gap = when {
            children.size <= 1 -> itemSpacing
            mainAxisArrangement == MainAxisArrangement.SpaceBetween -> {
                itemSpacing + extraSpace / (children.size - 1)
            }
            else -> itemSpacing
        }
        var currentLeft = paddingLeft + when (mainAxisArrangement) {
            MainAxisArrangement.Start,
            MainAxisArrangement.SpaceBetween,
            -> 0

            MainAxisArrangement.Center -> extraSpace / 2
            MainAxisArrangement.End -> extraSpace
        }
        children.forEachIndexed { index, child ->
            val params = child.layoutParams as MarginLayoutParams
            currentLeft += params.leftMargin
            val childTop = paddingTop + resolveVerticalGravity(
                child = child,
                params = params,
                innerHeight = innerHeight,
            )
            val childRight = currentLeft + child.measuredWidth
            val childBottom = childTop + child.measuredHeight
            child.layout(currentLeft, childTop, childRight, childBottom)
            currentLeft = childRight + params.rightMargin
            if (index != children.lastIndex) {
                currentLeft += gap
            }
        }
    }

    private fun layoutVertically(
        children: List<View>,
    ) {
        val innerWidth = width - paddingLeft - paddingRight
        val innerHeight = height - paddingTop - paddingBottom
        val totalChildrenHeight = children.sumOf { child ->
            val params = child.layoutParams as MarginLayoutParams
            child.measuredHeight + params.topMargin + params.bottomMargin
        }
        val baseSpacing = if (children.size > 1) itemSpacing * (children.size - 1) else 0
        val consumedHeight = totalChildrenHeight + baseSpacing
        val extraSpace = max(0, innerHeight - consumedHeight)
        val gap = when {
            children.size <= 1 -> itemSpacing
            mainAxisArrangement == MainAxisArrangement.SpaceBetween -> {
                itemSpacing + extraSpace / (children.size - 1)
            }
            else -> itemSpacing
        }
        var currentTop = paddingTop + when (mainAxisArrangement) {
            MainAxisArrangement.Start,
            MainAxisArrangement.SpaceBetween,
            -> 0

            MainAxisArrangement.Center -> extraSpace / 2
            MainAxisArrangement.End -> extraSpace
        }
        children.forEachIndexed { index, child ->
            val params = child.layoutParams as MarginLayoutParams
            currentTop += params.topMargin
            val childLeft = paddingLeft + resolveHorizontalGravity(
                child = child,
                params = params,
                innerWidth = innerWidth,
            )
            val childRight = childLeft + child.measuredWidth
            val childBottom = currentTop + child.measuredHeight
            child.layout(childLeft, currentTop, childRight, childBottom)
            currentTop = childBottom + params.bottomMargin
            if (index != children.lastIndex) {
                currentTop += gap
            }
        }
    }

    private fun resolveVerticalGravity(
        child: View,
        params: MarginLayoutParams,
        innerHeight: Int,
    ): Int {
        val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val consumedHeight = child.measuredHeight + params.topMargin + params.bottomMargin
        val extra = max(0, innerHeight - consumedHeight)
        return when (verticalGravity) {
            Gravity.CENTER_VERTICAL -> extra / 2 + params.topMargin
            Gravity.BOTTOM -> extra + params.topMargin
            else -> params.topMargin
        }
    }

    private fun resolveHorizontalGravity(
        child: View,
        params: MarginLayoutParams,
        innerWidth: Int,
    ): Int {
        val horizontalGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
        val consumedWidth = child.measuredWidth + params.leftMargin + params.rightMargin
        val extra = max(0, innerWidth - consumedWidth)
        return when (horizontalGravity) {
            Gravity.CENTER_HORIZONTAL -> extra / 2 + params.leftMargin
            Gravity.END -> extra + params.leftMargin
            else -> params.leftMargin
        }
    }

    private fun updateSpacingDivider() {
        if (itemSpacing <= 0) {
            showDividers = SHOW_DIVIDER_NONE
            dividerDrawable = null
            return
        }
        showDividers = SHOW_DIVIDER_MIDDLE
        dividerDrawable = if (orientation == HORIZONTAL) {
            SpacingDrawable(
                width = itemSpacing,
                height = 0,
            )
        } else {
            SpacingDrawable(
                width = 0,
                height = itemSpacing,
            )
        }
    }

    private class SpacingDrawable(
        private val width: Int,
        private val height: Int,
    ) : Drawable() {
        override fun draw(canvas: Canvas) = Unit

        override fun setAlpha(alpha: Int) = Unit

        override fun setColorFilter(colorFilter: ColorFilter?) = Unit

        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = PixelFormat.TRANSPARENT

        override fun getIntrinsicWidth(): Int = width

        override fun getIntrinsicHeight(): Int = height
    }
}
