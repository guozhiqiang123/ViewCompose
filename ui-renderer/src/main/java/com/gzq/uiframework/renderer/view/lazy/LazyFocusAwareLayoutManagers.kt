package com.gzq.uiframework.renderer.view.lazy

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class LazyLinearLayoutManager(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    var focusAutoScrollEnabled: Boolean = false,
) : LinearLayoutManager(context, orientation, reverseLayout) {
    override fun requestChildRectangleOnScreen(
        parent: RecyclerView,
        child: View,
        rect: Rect,
        immediate: Boolean,
        focusedChildVisible: Boolean,
    ): Boolean {
        if (!focusAutoScrollEnabled && focusedChildVisible) {
            return false
        }
        return super.requestChildRectangleOnScreen(
            parent,
            child,
            rect,
            immediate,
            focusedChildVisible,
        )
    }
}

internal class LazyGridLayoutManager(
    context: Context,
    spanCount: Int,
    var focusAutoScrollEnabled: Boolean = false,
) : GridLayoutManager(context, spanCount) {
    override fun requestChildRectangleOnScreen(
        parent: RecyclerView,
        child: View,
        rect: Rect,
        immediate: Boolean,
        focusedChildVisible: Boolean,
    ): Boolean {
        if (!focusAutoScrollEnabled && focusedChildVisible) {
            return false
        }
        return super.requestChildRectangleOnScreen(
            parent,
            child,
            rect,
            immediate,
            focusedChildVisible,
        )
    }
}
