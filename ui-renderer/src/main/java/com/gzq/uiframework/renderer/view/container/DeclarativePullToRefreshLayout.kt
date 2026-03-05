package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

internal class DeclarativePullToRefreshLayout(
    context: Context,
) : ViewGroup(context) {
    internal val swipeRefreshLayout = SwipeRefreshLayout(context)
    private val scrollView = ScrollView(context)
    internal val innerLayout = DeclarativeLinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }

    init {
        scrollView.addView(
            innerLayout,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
        )
        scrollView.isFillViewport = true
        swipeRefreshLayout.addView(
            scrollView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
        )
        super.addView(
            swipeRefreshLayout,
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
        swipeRefreshLayout.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(swipeRefreshLayout.measuredWidth, swipeRefreshLayout.measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        swipeRefreshLayout.layout(0, 0, r - l, b - t)
    }
}
