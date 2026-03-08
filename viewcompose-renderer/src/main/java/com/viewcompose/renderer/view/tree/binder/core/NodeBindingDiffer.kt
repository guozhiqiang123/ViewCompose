package com.viewcompose.renderer.view.tree

import com.viewcompose.renderer.node.LazyListItem
import com.viewcompose.renderer.node.collection.TabRowTab
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.BoxNodeProps
import com.viewcompose.renderer.node.spec.HorizontalPagerNodeProps
import com.viewcompose.renderer.node.spec.LazyColumnNodeProps
import com.viewcompose.renderer.node.spec.LazyRowNodeProps
import com.viewcompose.renderer.node.spec.LazyVerticalGridNodeProps
import com.viewcompose.renderer.node.spec.NodeSpec
import com.viewcompose.renderer.node.spec.TabRowNodeProps
import com.viewcompose.renderer.node.spec.VerticalPagerNodeProps

internal object NodeBindingDiffer {
    private val patchFactories by lazy { NodeBinderDescriptors.patchFactoriesBySpec() }

    fun plan(
        previous: VNode,
        next: VNode,
    ): NodeBindingPlan {
        if (previous === next) {
            return NodeBindingPlan.SkipSubtree
        }
        if (previous.type != next.type) {
            return NodeBindingPlan.Rebind
        }
        val modifierChanged = previous.modifier != next.modifier
        val prevSpec = previous.spec
        val nextSpec = next.spec
        val sessionContentChanged = hasSessionBackedContentChange(prevSpec, nextSpec)
        if (prevSpec == nextSpec && !sessionContentChanged) {
            return if (modifierChanged) {
                NodeBindingPlan.Rebind
            } else {
                if (previous.children == next.children) {
                    NodeBindingPlan.SkipSubtree
                } else {
                    NodeBindingPlan.SkipSelfOnly
                }
            }
        }
        if (prevSpec::class != nextSpec::class) {
            return NodeBindingPlan.Rebind
        }
        if (
            prevSpec is BoxNodeProps &&
            nextSpec is BoxNodeProps &&
            prevSpec.rippleColor != nextSpec.rippleColor
        ) {
            // Container ripple is resolved from NodeSpec, so this change must re-run modifier/style binding.
            return NodeBindingPlan.Rebind
        }
        val factory = patchFactories[prevSpec::class]
        if (factory != null) {
            return NodeBindingPlan.Patch(
                patch = factory(prevSpec, nextSpec),
                modifierChanged = modifierChanged,
            )
        }
        return NodeBindingPlan.Rebind
    }

    private fun hasSessionBackedContentChange(
        previous: NodeSpec,
        next: NodeSpec,
    ): Boolean {
        return when {
            previous is LazyColumnNodeProps && next is LazyColumnNodeProps -> previous.items.hasSessionIdentityChange(next.items)
            previous is LazyRowNodeProps && next is LazyRowNodeProps -> previous.items.hasSessionIdentityChange(next.items)
            previous is LazyVerticalGridNodeProps && next is LazyVerticalGridNodeProps -> previous.items.hasSessionIdentityChange(next.items)
            previous is HorizontalPagerNodeProps && next is HorizontalPagerNodeProps -> previous.pages.hasSessionIdentityChange(next.pages)
            previous is VerticalPagerNodeProps && next is VerticalPagerNodeProps -> previous.pages.hasSessionIdentityChange(next.pages)
            previous is TabRowNodeProps && next is TabRowNodeProps -> previous.tabs.hasTabSessionIdentityChange(next.tabs)
            else -> false
        }
    }

    private fun List<LazyListItem>.hasSessionIdentityChange(next: List<LazyListItem>): Boolean {
        if (size != next.size) return true
        for (index in indices) {
            val previous = this[index]
            val current = next[index]
            if (previous.sessionFactory !== current.sessionFactory) return true
            if (previous.sessionUpdater !== current.sessionUpdater) return true
        }
        return false
    }

    private fun List<TabRowTab>.hasTabSessionIdentityChange(next: List<TabRowTab>): Boolean {
        if (size != next.size) return true
        for (index in indices) {
            val previous = this[index].item
            val current = next[index].item
            if (previous.sessionFactory !== current.sessionFactory) return true
            if (previous.sessionUpdater !== current.sessionUpdater) return true
        }
        return false
    }
}
