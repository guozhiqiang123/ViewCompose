package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.gzq.uiframework.renderer.view.tree.LayoutPassTracker

internal class DeclarativeBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    companion object {
        const val UNSET_GRAVITY: Int = -1
    }

    var contentGravity: Int = Gravity.TOP or Gravity.START
        set(value) {
            field = value
            requestLayout()
        }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        val startNs = System.nanoTime()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LayoutPassTracker.recordMeasure(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        applyGravityToChild(child)
    }

    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val startNs = System.nanoTime()
        applyGravityToChildren()
        super.onLayout(changed, left, top, right, bottom)
        LayoutPassTracker.recordLayout(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    private fun applyGravityToChildren() {
        (0 until childCount).forEach { index ->
            applyGravityToChild(getChildAt(index))
        }
    }

    private fun applyGravityToChild(
        child: View,
    ) {
        val params = child.layoutParams as? LayoutParams ?: return
        if (params.gravity == UNSET_GRAVITY) {
            params.gravity = contentGravity
            child.layoutParams = params
        }
    }
}
