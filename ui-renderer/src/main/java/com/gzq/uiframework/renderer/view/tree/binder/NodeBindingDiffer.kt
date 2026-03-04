package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ImageNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.ProgressIndicatorNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.SliderNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps

internal object NodeBindingDiffer {
    fun plan(
        previous: VNode,
        next: VNode,
    ): NodeBindingPlan {
        if (previous.type != next.type) {
            return NodeBindingPlan.Rebind
        }
        if (previous.modifier != next.modifier) {
            return NodeBindingPlan.Rebind
        }
        if (previous.spec != null || next.spec != null) {
            if (previous.spec == next.spec) {
                if (previous.props != next.props) {
                    return NodeBindingPlan.Rebind
                }
                return NodeBindingPlan.Skip
            }
            val previousButton = previous.spec as? ButtonNodeProps
            val nextButton = next.spec as? ButtonNodeProps
            if (previousButton != null && nextButton != null) {
                return NodeBindingPlan.Patch(
                    patch = ButtonNodePatch(
                        previous = previousButton,
                        next = nextButton,
                    ),
                )
            }
            val previousText = previous.spec as? TextNodeProps
            val nextText = next.spec as? TextNodeProps
            if (previousText != null && nextText != null) {
                return NodeBindingPlan.Patch(
                    patch = TextNodePatch(
                        previous = previousText,
                        next = nextText,
                    ),
                )
            }
            val previousTextField = previous.spec as? TextFieldNodeProps
            val nextTextField = next.spec as? TextFieldNodeProps
            if (previousTextField != null && nextTextField != null) {
                return NodeBindingPlan.Patch(
                    patch = TextFieldNodePatch(
                        previous = previousTextField,
                        next = nextTextField,
                    ),
                )
            }
            val previousTabPager = previous.spec as? TabPagerNodeProps
            val nextTabPager = next.spec as? TabPagerNodeProps
            if (previousTabPager != null && nextTabPager != null) {
                return NodeBindingPlan.Patch(
                    patch = TabPagerNodePatch(
                        previous = previousTabPager,
                        next = nextTabPager,
                    ),
                )
            }
            val previousSegmentedControl = previous.spec as? SegmentedControlNodeProps
            val nextSegmentedControl = next.spec as? SegmentedControlNodeProps
            if (previousSegmentedControl != null && nextSegmentedControl != null) {
                return NodeBindingPlan.Patch(
                    patch = SegmentedControlNodePatch(
                        previous = previousSegmentedControl,
                        next = nextSegmentedControl,
                    ),
                )
            }
            val previousLazyColumn = previous.spec as? LazyColumnNodeProps
            val nextLazyColumn = next.spec as? LazyColumnNodeProps
            if (previousLazyColumn != null && nextLazyColumn != null) {
                return NodeBindingPlan.Patch(
                    patch = LazyColumnNodePatch(
                        previous = previousLazyColumn,
                        next = nextLazyColumn,
                    ),
                )
            }
            val previousToggle = previous.spec as? ToggleNodeProps
            val nextToggle = next.spec as? ToggleNodeProps
            if (previousToggle != null && nextToggle != null) {
                return NodeBindingPlan.Patch(
                    patch = ToggleNodePatch(
                        previous = previousToggle,
                        next = nextToggle,
                    ),
                )
            }
            val previousSlider = previous.spec as? SliderNodeProps
            val nextSlider = next.spec as? SliderNodeProps
            if (previousSlider != null && nextSlider != null) {
                return NodeBindingPlan.Patch(
                    patch = SliderNodePatch(
                        previous = previousSlider,
                        next = nextSlider,
                    ),
                )
            }
            val previousProgress = previous.spec as? ProgressIndicatorNodeProps
            val nextProgress = next.spec as? ProgressIndicatorNodeProps
            if (previousProgress != null && nextProgress != null) {
                return NodeBindingPlan.Patch(
                    patch = ProgressIndicatorNodePatch(
                        previous = previousProgress,
                        next = nextProgress,
                    ),
                )
            }
            val previousRow = previous.spec as? RowNodeProps
            val nextRow = next.spec as? RowNodeProps
            if (previousRow != null && nextRow != null) {
                return NodeBindingPlan.Patch(
                    patch = RowNodePatch(
                        previous = previousRow,
                        next = nextRow,
                    ),
                )
            }
            val previousColumn = previous.spec as? ColumnNodeProps
            val nextColumn = next.spec as? ColumnNodeProps
            if (previousColumn != null && nextColumn != null) {
                return NodeBindingPlan.Patch(
                    patch = ColumnNodePatch(
                        previous = previousColumn,
                        next = nextColumn,
                    ),
                )
            }
            val previousBox = previous.spec as? BoxNodeProps
            val nextBox = next.spec as? BoxNodeProps
            if (previousBox != null && nextBox != null) {
                return NodeBindingPlan.Patch(
                    patch = BoxNodePatch(
                        previous = previousBox,
                        next = nextBox,
                    ),
                )
            }
            val previousImage = previous.spec as? ImageNodeProps
            val nextImage = next.spec as? ImageNodeProps
            if (previousImage != null && nextImage != null) {
                return NodeBindingPlan.Patch(
                    patch = ImageNodePatch(
                        previous = previousImage,
                        next = nextImage,
                    ),
                )
            }
            val previousIconButton = previous.spec as? IconButtonNodeProps
            val nextIconButton = next.spec as? IconButtonNodeProps
            if (previousIconButton != null && nextIconButton != null) {
                return NodeBindingPlan.Patch(
                    patch = IconButtonNodePatch(
                        previous = previousIconButton,
                        next = nextIconButton,
                    ),
                )
            }
            val previousDivider = previous.spec as? DividerNodeProps
            val nextDivider = next.spec as? DividerNodeProps
            if (previousDivider != null && nextDivider != null) {
                return NodeBindingPlan.Patch(
                    patch = DividerNodePatch(
                        previous = previousDivider,
                        next = nextDivider,
                    ),
                )
            }
            return NodeBindingPlan.Rebind
        }
        if (previous.props != next.props) {
            return NodeBindingPlan.Rebind
        }
        return NodeBindingPlan.Skip
    }
}
