package com.gzq.uiframework.renderer.view.lazy

import android.graphics.Rect
import android.view.View
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
        val focused = recyclerView.findFocus() ?: return
        if (focused === recyclerView) {
            return
        }
        val rect = Rect()
        focused.getDrawingRect(rect)
        recyclerView.offsetDescendantRectToMyCoords(focused, rect)
        val isVerticallyVisible = rect.top >= recyclerView.paddingTop &&
            rect.bottom <= recyclerView.height - recyclerView.paddingBottom
        val isHorizontallyVisible = rect.left >= recyclerView.paddingLeft &&
            rect.right <= recyclerView.width - recyclerView.paddingRight
        val needsVerticalScroll = recyclerView.layoutManager?.canScrollVertically() == true &&
            !isVerticallyVisible
        val needsHorizontalScroll = recyclerView.layoutManager?.canScrollHorizontally() == true &&
            !isHorizontallyVisible
        if (needsVerticalScroll || needsHorizontalScroll) {
            recyclerView.requestChildRectangleOnScreen(focused, rect, true)
        }
    }
}
