package com.viewcompose.renderer.view.tree

import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.BoxNodeProps
import com.viewcompose.renderer.node.spec.ButtonNodeProps
import com.viewcompose.renderer.node.spec.ColumnNodeProps
import com.viewcompose.renderer.node.spec.DividerNodeProps
import com.viewcompose.renderer.node.spec.FlowColumnNodeProps
import com.viewcompose.renderer.node.spec.FlowRowNodeProps
import com.viewcompose.renderer.node.spec.HorizontalPagerNodeProps
import com.viewcompose.renderer.node.spec.IconButtonNodeProps
import com.viewcompose.renderer.node.spec.ImageNodeProps
import com.viewcompose.renderer.node.spec.LazyColumnNodeProps
import com.viewcompose.renderer.node.spec.LazyRowNodeProps
import com.viewcompose.renderer.node.spec.LazyVerticalGridNodeProps
import com.viewcompose.renderer.node.spec.NavigationBarNodeProps
import com.viewcompose.renderer.node.spec.NodeSpec
import com.viewcompose.renderer.node.spec.ProgressIndicatorNodeProps
import com.viewcompose.renderer.node.spec.PullToRefreshNodeProps
import com.viewcompose.renderer.node.spec.RowNodeProps
import com.viewcompose.renderer.node.spec.ScrollableColumnNodeProps
import com.viewcompose.renderer.node.spec.ScrollableRowNodeProps
import com.viewcompose.renderer.node.spec.SegmentedControlNodeProps
import com.viewcompose.renderer.node.spec.SliderNodeProps
import com.viewcompose.renderer.node.spec.TabRowNodeProps
import com.viewcompose.renderer.node.spec.TextFieldNodeProps
import com.viewcompose.renderer.node.spec.TextNodeProps
import com.viewcompose.renderer.node.spec.ToggleNodeProps
import com.viewcompose.renderer.node.spec.VerticalPagerNodeProps
import kotlin.reflect.KClass

private typealias PatchFactory = (previous: NodeSpec, next: NodeSpec) -> NodeViewPatch

internal object NodeBindingDiffer {
    private val patchFactories: Map<KClass<out NodeSpec>, PatchFactory> = mapOf(
        ButtonNodeProps::class to { p, n -> ButtonNodePatch(p as ButtonNodeProps, n as ButtonNodeProps) },
        TextNodeProps::class to { p, n -> TextNodePatch(p as TextNodeProps, n as TextNodeProps) },
        TextFieldNodeProps::class to { p, n -> TextFieldNodePatch(p as TextFieldNodeProps, n as TextFieldNodeProps) },
        SegmentedControlNodeProps::class to { p, n -> SegmentedControlNodePatch(p as SegmentedControlNodeProps, n as SegmentedControlNodeProps) },
        LazyColumnNodeProps::class to { p, n -> LazyColumnNodePatch(p as LazyColumnNodeProps, n as LazyColumnNodeProps) },
        LazyRowNodeProps::class to { p, n -> LazyRowNodePatch(p as LazyRowNodeProps, n as LazyRowNodeProps) },
        ToggleNodeProps::class to { p, n -> ToggleNodePatch(p as ToggleNodeProps, n as ToggleNodeProps) },
        SliderNodeProps::class to { p, n -> SliderNodePatch(p as SliderNodeProps, n as SliderNodeProps) },
        ProgressIndicatorNodeProps::class to { p, n -> ProgressIndicatorNodePatch(p as ProgressIndicatorNodeProps, n as ProgressIndicatorNodeProps) },
        RowNodeProps::class to { p, n -> RowNodePatch(p as RowNodeProps, n as RowNodeProps) },
        ColumnNodeProps::class to { p, n -> ColumnNodePatch(p as ColumnNodeProps, n as ColumnNodeProps) },
        BoxNodeProps::class to { p, n -> BoxNodePatch(p as BoxNodeProps, n as BoxNodeProps) },
        ImageNodeProps::class to { p, n -> ImageNodePatch(p as ImageNodeProps, n as ImageNodeProps) },
        IconButtonNodeProps::class to { p, n -> IconButtonNodePatch(p as IconButtonNodeProps, n as IconButtonNodeProps) },
        DividerNodeProps::class to { p, n -> DividerNodePatch(p as DividerNodeProps, n as DividerNodeProps) },
        ScrollableColumnNodeProps::class to { p, n -> ScrollableColumnNodePatch(p as ScrollableColumnNodeProps, n as ScrollableColumnNodeProps) },
        ScrollableRowNodeProps::class to { p, n -> ScrollableRowNodePatch(p as ScrollableRowNodeProps, n as ScrollableRowNodeProps) },
        FlowRowNodeProps::class to { p, n -> FlowRowNodePatch(p as FlowRowNodeProps, n as FlowRowNodeProps) },
        FlowColumnNodeProps::class to { p, n -> FlowColumnNodePatch(p as FlowColumnNodeProps, n as FlowColumnNodeProps) },
        NavigationBarNodeProps::class to { p, n -> NavigationBarNodePatch(p as NavigationBarNodeProps, n as NavigationBarNodeProps) },
        HorizontalPagerNodeProps::class to { p, n -> HorizontalPagerNodePatch(p as HorizontalPagerNodeProps, n as HorizontalPagerNodeProps) },
        TabRowNodeProps::class to { p, n -> TabRowNodePatch(p as TabRowNodeProps, n as TabRowNodeProps) },
        VerticalPagerNodeProps::class to { p, n -> VerticalPagerNodePatch(p as VerticalPagerNodeProps, n as VerticalPagerNodeProps) },
        LazyVerticalGridNodeProps::class to { p, n -> LazyVerticalGridNodePatch(p as LazyVerticalGridNodeProps, n as LazyVerticalGridNodeProps) },
        PullToRefreshNodeProps::class to { p, n -> PullToRefreshNodePatch(p as PullToRefreshNodeProps, n as PullToRefreshNodeProps) },
    )

    fun plan(
        previous: VNode,
        next: VNode,
    ): NodeBindingPlan {
        if (previous.type != next.type) {
            return NodeBindingPlan.Rebind
        }
        val modifierChanged = previous.modifier != next.modifier
        if (previous.spec != null || next.spec != null) {
            if (previous.spec == next.spec) {
                return if (modifierChanged) {
                    NodeBindingPlan.Rebind
                } else if (previous.props != next.props) {
                    NodeBindingPlan.Rebind
                } else {
                    if (previous.children == next.children) {
                        NodeBindingPlan.SkipSubtree
                    } else {
                        NodeBindingPlan.SkipSelfOnly
                    }
                }
            }
            val prevSpec = previous.spec
            val nextSpec = next.spec
            if (prevSpec != null && nextSpec != null) {
                val factory = patchFactories[prevSpec::class]
                if (factory != null) {
                    return NodeBindingPlan.Patch(
                        patch = factory(prevSpec, nextSpec),
                        modifierChanged = modifierChanged,
                    )
                }
            }
            return NodeBindingPlan.Rebind
        }
        if (previous.props != next.props || modifierChanged) {
            return NodeBindingPlan.Rebind
        }
        return if (previous.children == next.children) {
            NodeBindingPlan.SkipSubtree
        } else {
            NodeBindingPlan.SkipSelfOnly
        }
    }
}
