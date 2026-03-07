package com.viewcompose.renderer.view.lazy

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.viewcompose.renderer.R

internal object LazyFocusFollowLayoutMonitor {
    private const val TAG = "UIFocusFollow"

    fun isEnabled(recyclerView: RecyclerView): Boolean {
        return recyclerView.getTag(R.id.ui_framework_focus_follow_enabled) as? Boolean == true
    }

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
            debugLog {
                "detach listeners rv=${recyclerView.hashCode()}"
            }
            recyclerView.setTag(R.id.ui_framework_focus_follow_enabled, false)
            return
        }
        if (existingLayoutListener == null) {
            val listener = View.OnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
                val target = view as? RecyclerView ?: return@OnLayoutChangeListener
                ensureFocusedChildVisible(target, trigger = "layoutChange")
            }
            recyclerView.addOnLayoutChangeListener(listener)
            recyclerView.setTag(R.id.ui_framework_focus_follow_layout_listener, listener)
        }
        if (existingGlobalFocusListener == null) {
            val globalFocusListener = ViewTreeObserver.OnGlobalFocusChangeListener { _, _ ->
                ensureFocusedChildVisible(recyclerView, trigger = "globalFocus")
            }
            recyclerView.viewTreeObserver.addOnGlobalFocusChangeListener(globalFocusListener)
            recyclerView.setTag(R.id.ui_framework_focus_follow_global_focus_listener, globalFocusListener)
        }
        if (existingGlobalLayoutListener == null) {
            val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                ensureFocusedChildVisible(recyclerView, trigger = "globalLayout")
            }
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
            recyclerView.setTag(R.id.ui_framework_focus_follow_global_layout_listener, globalLayoutListener)
        }
        debugLog {
            "attach listeners rv=${recyclerView.hashCode()} " +
                "layout=${existingLayoutListener != null} " +
                "focus=${existingGlobalFocusListener != null} " +
                "globalLayout=${existingGlobalLayoutListener != null}"
        }
        recyclerView.setTag(R.id.ui_framework_focus_follow_enabled, true)
        ensureFocusedChildVisible(recyclerView, trigger = "apply")
    }

    private fun ensureFocusedChildVisible(
        recyclerView: RecyclerView,
        trigger: String,
    ) {
        val focused = recyclerView.findFocus()
            ?.takeIf { it.onCheckIsTextEditor() }
            ?: return
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
                debugLog {
                    "skip vertical scroll (end reached) trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "holderPos=${recyclerView.findContainingViewHolder(focused)?.bindingAdapterPosition} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()} dy=$dy"
                }
                return
            }
            if (dy < 0 && !recyclerView.canScrollVertically(-1)) {
                debugLog {
                    "skip vertical scroll (start reached) trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "holderPos=${recyclerView.findContainingViewHolder(focused)?.bindingAdapterPosition} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()} dy=$dy"
                }
                return
            }
            if (dy != 0) {
                debugLog {
                    "scroll vertical trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "holderPos=${recyclerView.findContainingViewHolder(focused)?.bindingAdapterPosition} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()} dy=$dy " +
                        "bottomOverflow=$bottomOverflow topOverflow=$topOverflow"
                }
                recyclerView.scrollBy(0, dy)
            } else {
                debugLog {
                    "no vertical scroll trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "holderPos=${recyclerView.findContainingViewHolder(focused)?.bindingAdapterPosition} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()}"
                }
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
                debugLog {
                    "skip horizontal scroll (end reached) trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()} dx=$dx"
                }
                return
            }
            if (dx < 0 && !recyclerView.canScrollHorizontally(-1)) {
                debugLog {
                    "skip horizontal scroll (start reached) trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()} dx=$dx"
                }
                return
            }
            if (dx != 0) {
                debugLog {
                    "scroll horizontal trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()} dx=$dx"
                }
                recyclerView.scrollBy(dx, 0)
            } else {
                debugLog {
                    "no horizontal scroll trigger=$trigger rv=${recyclerView.hashCode()} " +
                        "focusedRect=${focusedRect.toShortString()} viewport=${viewport.toShortString()}"
                }
            }
        }
    }

    private fun resolveVisibleViewport(
        recyclerView: RecyclerView,
        fallback: Rect,
    ): Rect {
        return FocusFollowViewportResolver.resolve(
            view = recyclerView,
            fallback = fallback,
        )
    }

    private inline fun debugLog(message: () -> String) {
        if (!Log.isLoggable(TAG, Log.DEBUG)) {
            return
        }
        Log.d(TAG, message())
    }
}
