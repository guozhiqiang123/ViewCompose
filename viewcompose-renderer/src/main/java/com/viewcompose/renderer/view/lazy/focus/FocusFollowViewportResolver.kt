package com.viewcompose.renderer.view.lazy

import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max
import kotlin.math.min

internal object FocusFollowViewportResolver {
    fun resolve(
        view: View,
        fallback: Rect,
    ): Rect {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        var viewport = Rect(fallback)

        val globalVisibleRect = Rect()
        if (view.getGlobalVisibleRect(globalVisibleRect)) {
            val globalViewport = Rect(
                globalVisibleRect.left - location[0],
                globalVisibleRect.top - location[1],
                globalVisibleRect.right - location[0],
                globalVisibleRect.bottom - location[1],
            )
            viewport = intersectViewport(
                current = viewport,
                candidate = globalViewport,
                fallback = fallback,
            )
        }

        val windowVisibleFrame = Rect()
        view.getWindowVisibleDisplayFrame(windowVisibleFrame)
        if (!windowVisibleFrame.isEmpty) {
            val windowViewport = Rect(
                windowVisibleFrame.left - location[0],
                windowVisibleFrame.top - location[1],
                windowVisibleFrame.right - location[0],
                windowVisibleFrame.bottom - location[1],
            )
            viewport = intersectViewport(
                current = viewport,
                candidate = windowViewport,
                fallback = fallback,
            )
        }

        val rootInsets = ViewCompat.getRootWindowInsets(view)
        if (rootInsets?.isVisible(WindowInsetsCompat.Type.ime()) == true) {
            val imeBottomInset = rootInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            if (imeBottomInset > 0) {
                val rootHeight = view.rootView?.height ?: 0
                if (rootHeight > 0) {
                    val imeTopInWindow = rootHeight - imeBottomInset
                    val imeTopInView = imeTopInWindow - location[1]
                    val boundedImeTop = imeTopInView.coerceIn(fallback.top, fallback.bottom)
                    if (boundedImeTop < viewport.bottom) {
                        viewport.bottom = boundedImeTop
                    }
                }
            }
        }

        if (viewport.right <= viewport.left || viewport.bottom <= viewport.top) {
            return fallback
        }
        return viewport
    }

    private fun intersectViewport(
        current: Rect,
        candidate: Rect,
        fallback: Rect,
    ): Rect {
        val bounded = Rect(
            candidate.left.coerceIn(fallback.left, fallback.right),
            candidate.top.coerceIn(fallback.top, fallback.bottom),
            candidate.right.coerceIn(fallback.left, fallback.right),
            candidate.bottom.coerceIn(fallback.top, fallback.bottom),
        )
        val intersected = Rect(
            max(current.left, bounded.left),
            max(current.top, bounded.top),
            min(current.right, bounded.right),
            min(current.bottom, bounded.bottom),
        )
        if (intersected.right <= intersected.left || intersected.bottom <= intersected.top) {
            return current
        }
        return intersected
    }
}
