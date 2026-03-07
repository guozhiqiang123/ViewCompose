package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

internal class DeclarativeScrollableColumnLayout(
    context: Context,
) : ScrollView(context), ChildHostViewGroup {
    internal val innerLayout = DeclarativeLinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }
    override val childHost: ViewGroup
        get() = innerLayout

    init {
        super.addView(
            innerLayout,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT),
        )
        isFillViewport = true
    }
}
