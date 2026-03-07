package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

internal class DeclarativeScrollableRowLayout(
    context: Context,
) : HorizontalScrollView(context), ChildHostViewGroup {
    internal val innerLayout = DeclarativeLinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
    }
    override val childHost: ViewGroup
        get() = innerLayout

    init {
        super.addView(
            innerLayout,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT),
        )
        isFillViewport = true
    }
}
