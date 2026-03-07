package com.gzq.uiframework.renderer.view.lazy

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R

internal object LazyFocusFollowLayoutMonitor {
    fun apply(
        recyclerView: RecyclerView,
        enabled: Boolean,
    ) {
        val existingLayoutListener = recyclerView.getTag(R.id.ui_framework_focus_follow_layout_listener)
            as? View.OnLayoutChangeListener
        val existingGlobalFocusListener = recyclerView.getTag(R.id.ui_framework_focus_follow_global_focus_listener)
            as? ViewTreeObserver.OnGlobalFocusChangeListener
        if (!enabled) {
            if (existingLayoutListener != null) {
                recyclerView.removeOnLayoutChangeListener(existingLayoutListener)
                recyclerView.setTag(R.id.ui_framework_focus_follow_layout_listener, null)
            }
            if (existingGlobalFocusListener != null) {
                val viewTreeObserver = recyclerView.viewTreeObserver
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalFocusChangeListener(existingGlobalFocusListener)
                }
                recyclerView.setTag(R.id.ui_framework_focus_follow_global_focus_listener, null)
            }
            return
        }
        if (existingLayoutListener != null && existingGlobalFocusListener != null) {
            return
        }
        val listener = View.OnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            val target = view as? RecyclerView ?: return@OnLayoutChangeListener
            ensureFocusedChildVisible(target)
        }
        val globalFocusListener = ViewTreeObserver.OnGlobalFocusChangeListener { _, _ ->
            ensureFocusedChildVisible(recyclerView)
        }
        recyclerView.addOnLayoutChangeListener(listener)
        recyclerView.viewTreeObserver.addOnGlobalFocusChangeListener(globalFocusListener)
        recyclerView.setTag(R.id.ui_framework_focus_follow_layout_listener, listener)
        recyclerView.setTag(R.id.ui_framework_focus_follow_global_focus_listener, globalFocusListener)
        ensureFocusedChildVisible(recyclerView)
    }

    private fun ensureFocusedChildVisible(recyclerView: RecyclerView) {
        val focused = recyclerView.findFocus() as? EditText ?: return
        if (focused === recyclerView) {
            return
        }
        val layoutManager = recyclerView.layoutManager ?: return
        val focusedRect = Rect().also { rect ->
            focused.getDrawingRect(rect)
            recyclerView.offsetDescendantRectToMyCoords(focused, rect)
        }
        if (layoutManager.canScrollVertically()) {
            val bottomOverflow = focusedRect.bottom - (recyclerView.height - recyclerView.paddingBottom)
            val topOverflow = focusedRect.top - recyclerView.paddingTop
            val dy = when {
                bottomOverflow > 0 -> bottomOverflow
                topOverflow < 0 -> topOverflow
                else -> 0
            }
            if (dy > 0 && !recyclerView.canScrollVertically(1)) {
                return
            }
            if (dy < 0 && !recyclerView.canScrollVertically(-1)) {
                return
            }
            if (dy != 0) {
                recyclerView.scrollBy(0, dy)
            }
            return
        }
        if (layoutManager.canScrollHorizontally()) {
            val rightOverflow = focusedRect.right - (recyclerView.width - recyclerView.paddingRight)
            val leftOverflow = focusedRect.left - recyclerView.paddingLeft
            val dx = when {
                rightOverflow > 0 -> rightOverflow
                leftOverflow < 0 -> leftOverflow
                else -> 0
            }
            if (dx > 0 && !recyclerView.canScrollHorizontally(1)) {
                return
            }
            if (dx < 0 && !recyclerView.canScrollHorizontally(-1)) {
                return
            }
            if (dx != 0) {
                recyclerView.scrollBy(dx, 0)
            }
        }
    }
}
