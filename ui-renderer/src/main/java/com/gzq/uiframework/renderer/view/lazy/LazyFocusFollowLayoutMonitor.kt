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
        val existingGlobalLayoutListener = recyclerView.getTag(R.id.ui_framework_focus_follow_global_layout_listener)
            as? ViewTreeObserver.OnGlobalLayoutListener
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
            if (existingGlobalLayoutListener != null) {
                val viewTreeObserver = recyclerView.viewTreeObserver
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalLayoutListener(existingGlobalLayoutListener)
                }
                recyclerView.setTag(R.id.ui_framework_focus_follow_global_layout_listener, null)
            }
            return
        }
        if (existingLayoutListener == null) {
            val listener = View.OnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
                val target = view as? RecyclerView ?: return@OnLayoutChangeListener
                ensureFocusedChildVisible(target)
            }
            recyclerView.addOnLayoutChangeListener(listener)
            recyclerView.setTag(R.id.ui_framework_focus_follow_layout_listener, listener)
        }
        if (existingGlobalFocusListener == null) {
            val globalFocusListener = ViewTreeObserver.OnGlobalFocusChangeListener { _, _ ->
                ensureFocusedChildVisible(recyclerView)
            }
            recyclerView.viewTreeObserver.addOnGlobalFocusChangeListener(globalFocusListener)
            recyclerView.setTag(R.id.ui_framework_focus_follow_global_focus_listener, globalFocusListener)
        }
        if (existingGlobalLayoutListener == null) {
            val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                ensureFocusedChildVisible(recyclerView)
            }
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
            recyclerView.setTag(R.id.ui_framework_focus_follow_global_layout_listener, globalLayoutListener)
        }
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
        val viewport = resolveVisibleViewport(
            recyclerView = recyclerView,
            fallback = Rect(
                recyclerView.paddingLeft,
                recyclerView.paddingTop,
                recyclerView.width - recyclerView.paddingRight,
                recyclerView.height - recyclerView.paddingBottom,
            ),
        )
        if (layoutManager.canScrollVertically()) {
            val bottomOverflow = focusedRect.bottom - viewport.bottom
            val topOverflow = focusedRect.top - viewport.top
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
            val rightOverflow = focusedRect.right - viewport.right
            val leftOverflow = focusedRect.left - viewport.left
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

    private fun resolveVisibleViewport(
        recyclerView: RecyclerView,
        fallback: Rect,
    ): Rect {
        val globalVisibleRect = Rect()
        if (!recyclerView.getGlobalVisibleRect(globalVisibleRect)) {
            return fallback
        }
        val location = IntArray(2)
        recyclerView.getLocationOnScreen(location)
        return Rect(
            (globalVisibleRect.left - location[0]).coerceAtLeast(fallback.left),
            (globalVisibleRect.top - location[1]).coerceAtLeast(fallback.top),
            (globalVisibleRect.right - location[0]).coerceAtMost(fallback.right),
            (globalVisibleRect.bottom - location[1]).coerceAtMost(fallback.bottom),
        )
    }
}
