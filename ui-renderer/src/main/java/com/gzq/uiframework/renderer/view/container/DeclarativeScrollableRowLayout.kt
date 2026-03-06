package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

internal class DeclarativeScrollableRowLayout(
    context: Context,
) : HorizontalScrollView(context) {
    internal val innerLayout = DeclarativeLinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    init {
        super.addView(
            innerLayout,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT),
        )
        isFillViewport = true
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
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
}
