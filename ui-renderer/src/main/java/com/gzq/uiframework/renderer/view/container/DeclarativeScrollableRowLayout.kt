package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

internal class DeclarativeScrollableRowLayout(
    context: Context,
) : ViewGroup(context) {
    private val scrollView = HorizontalScrollView(context)
    internal val innerLayout = DeclarativeLinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    init {
        scrollView.addView(
            innerLayout,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT),
        )
        scrollView.isFillViewport = true
        super.addView(
            scrollView,
            -1,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
        )
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        innerLayout.addView(child, index, params)
    }

    override fun removeView(view: View?) {
        innerLayout.removeView(view)
    }

    override fun removeViewAt(index: Int) {
        innerLayout.removeViewAt(index)
    }

    override fun removeAllViews() {
        innerLayout.removeAllViews()
    }

    override fun getChildCount(): Int = innerLayout.childCount

    override fun getChildAt(index: Int): View? = innerLayout.getChildAt(index)

    override fun indexOfChild(child: View?): Int = innerLayout.indexOfChild(child)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        scrollView.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(scrollView.measuredWidth, scrollView.measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        scrollView.layout(0, 0, r - l, b - t)
    }
}
