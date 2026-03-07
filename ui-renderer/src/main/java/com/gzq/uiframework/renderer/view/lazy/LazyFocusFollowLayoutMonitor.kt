package com.gzq.uiframework.renderer.view.lazy

import android.graphics.Rect
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R

internal object LazyFocusFollowLayoutMonitor {
    fun apply(
        recyclerView: RecyclerView,
        enabled: Boolean,
    ) {
        val existing = recyclerView.getTag(R.id.ui_framework_focus_follow_layout_listener)
            as? View.OnLayoutChangeListener
        if (!enabled) {
            if (existing != null) {
                recyclerView.removeOnLayoutChangeListener(existing)
                recyclerView.setTag(R.id.ui_framework_focus_follow_layout_listener, null)
            }
            return
        }
        if (existing != null) {
            return
        }
        val listener = View.OnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            val target = view as? RecyclerView ?: return@OnLayoutChangeListener
            ensureFocusedChildVisible(target)
        }
        recyclerView.addOnLayoutChangeListener(listener)
        recyclerView.setTag(R.id.ui_framework_focus_follow_layout_listener, listener)
    }

    private fun ensureFocusedChildVisible(recyclerView: RecyclerView) {
        val focused = recyclerView.findFocus() as? EditText ?: return
        if (focused === recyclerView) {
            return
        }
        val localRect = Rect()
        focused.getDrawingRect(localRect)
        val visibleRect = Rect(localRect)
        recyclerView.offsetDescendantRectToMyCoords(focused, visibleRect)
        val isVerticallyVisible = visibleRect.top >= recyclerView.paddingTop &&
            visibleRect.bottom <= recyclerView.height - recyclerView.paddingBottom
        val isHorizontallyVisible = visibleRect.left >= recyclerView.paddingLeft &&
            visibleRect.right <= recyclerView.width - recyclerView.paddingRight
        val needsVerticalScroll = recyclerView.layoutManager?.canScrollVertically() == true &&
            !isVerticallyVisible
        val needsHorizontalScroll = recyclerView.layoutManager?.canScrollHorizontally() == true &&
            !isHorizontallyVisible
        if (needsVerticalScroll || needsHorizontalScroll) {
            focused.requestRectangleOnScreen(localRect, false)
        }
    }
}
