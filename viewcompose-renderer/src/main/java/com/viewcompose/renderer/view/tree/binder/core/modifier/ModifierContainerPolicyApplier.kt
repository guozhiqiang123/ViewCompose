package com.viewcompose.renderer.view.tree

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.viewcompose.renderer.R
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.viewcompose.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.viewcompose.renderer.view.container.DeclarativeScrollableColumnLayout
import com.viewcompose.renderer.view.container.DeclarativeVerticalPagerLayout
import com.viewcompose.renderer.view.lazy.reuse.FrameworkRecyclerViewDefaults
import com.viewcompose.renderer.view.lazy.focus.LazyFocusFollowLayoutMonitor
import com.viewcompose.renderer.view.lazy.focus.ScrollableFocusFollowLayoutMonitor

internal object ModifierContainerPolicyApplier {
    private const val FOCUS_FOLLOW_TAG = "UIFocusFollow"

    fun applyScrollableContainerPolicies(
        view: View,
        node: VNode,
    ) {
        when (node.type) {
            NodeType.LazyColumn -> {
                val recyclerView = view as? RecyclerView ?: return
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                val motionPolicy = node.modifier.lazyContainerMotionPolicy()
                FrameworkRecyclerViewDefaults.applyLazyColumnDefaults(
                    recyclerView = recyclerView,
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                    animateInsert = motionPolicy.animateInsert,
                    animateRemove = motionPolicy.animateRemove,
                    animateMove = motionPolicy.animateMove,
                    animateChange = motionPolicy.animateChange,
                )
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                LazyFocusFollowLayoutMonitor.apply(
                    recyclerView = recyclerView,
                    enabled = focusPolicy.enabled,
                )
            }

            NodeType.LazyRow -> {
                val recyclerView = view as? RecyclerView ?: return
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                val motionPolicy = node.modifier.lazyContainerMotionPolicy()
                FrameworkRecyclerViewDefaults.applyLazyRowDefaults(
                    recyclerView = recyclerView,
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                    animateInsert = motionPolicy.animateInsert,
                    animateRemove = motionPolicy.animateRemove,
                    animateMove = motionPolicy.animateMove,
                    animateChange = motionPolicy.animateChange,
                )
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                if (focusPolicy.enabled) {
                    warnUnsupportedFocusFollowOnce(
                        view = recyclerView,
                        nodeType = node.type,
                    )
                }
                // Keyboard follow targets vertical overflow; LazyRow keeps horizontal-only semantics.
                LazyFocusFollowLayoutMonitor.apply(recyclerView, enabled = false)
            }

            NodeType.LazyVerticalGrid -> {
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                val motionPolicy = node.modifier.lazyContainerMotionPolicy()
                (view as? DeclarativeLazyVerticalGridLayout)?.applyRecyclerDefaults(
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                    animateInsert = motionPolicy.animateInsert,
                    animateRemove = motionPolicy.animateRemove,
                    animateMove = motionPolicy.animateMove,
                    animateChange = motionPolicy.animateChange,
                )
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                (view as? DeclarativeLazyVerticalGridLayout)?.setFocusFollowKeyboardEnabled(focusPolicy.enabled)
            }

            NodeType.HorizontalPager -> {
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                val motionPolicy = node.modifier.lazyContainerMotionPolicy()
                (view as? DeclarativeHorizontalPagerLayout)?.applyRecyclerDefaults(
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                    animateInsert = motionPolicy.animateInsert,
                    animateRemove = motionPolicy.animateRemove,
                    animateMove = motionPolicy.animateMove,
                    animateChange = motionPolicy.animateChange,
                )
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                if (focusPolicy.enabled) {
                    warnUnsupportedFocusFollowOnce(
                        view = view,
                        nodeType = node.type,
                    )
                }
            }

            NodeType.VerticalPager -> {
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                val motionPolicy = node.modifier.lazyContainerMotionPolicy()
                (view as? DeclarativeVerticalPagerLayout)?.applyRecyclerDefaults(
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                    animateInsert = motionPolicy.animateInsert,
                    animateRemove = motionPolicy.animateRemove,
                    animateMove = motionPolicy.animateMove,
                    animateChange = motionPolicy.animateChange,
                )
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                (view as? DeclarativeVerticalPagerLayout)?.setFocusFollowKeyboardEnabled(focusPolicy.enabled)
            }

            NodeType.ScrollableColumn -> {
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                (view as? DeclarativeScrollableColumnLayout)?.let { scrollView ->
                    ScrollableFocusFollowLayoutMonitor.apply(
                        scrollView = scrollView,
                        enabled = focusPolicy.enabled,
                    )
                }
            }

            NodeType.ScrollableRow -> {
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                if (focusPolicy.enabled) {
                    warnUnsupportedFocusFollowOnce(
                        view = view,
                        nodeType = node.type,
                    )
                }
            }

            else -> Unit
        }
    }

    private fun warnUnsupportedFocusFollowOnce(
        view: View,
        nodeType: NodeType,
    ) {
        if (view.getTag(R.id.viewcompose_focus_follow_warning_emitted) == true) {
            return
        }
        view.setTag(R.id.viewcompose_focus_follow_warning_emitted, true)
        Log.w(
            FOCUS_FOLLOW_TAG,
            "focusFollowKeyboard(enabled=true) is ignored for $nodeType because keyboard follow only targets vertical overflow containers.",
        )
    }
}
