package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ImageNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyRowNodeProps
import com.gzq.uiframework.renderer.node.spec.ProgressIndicatorNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.ScrollableColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.ScrollableRowNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.SliderNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps

internal sealed interface NodeBindingPlan {
    data object Skip : NodeBindingPlan

    data object Rebind : NodeBindingPlan

    data class Patch(
        val patch: NodeViewPatch,
    ) : NodeBindingPlan
}

internal sealed interface NodeViewPatch

internal data class ButtonNodePatch(
    val previous: ButtonNodeProps,
    val next: ButtonNodeProps,
) : NodeViewPatch

internal data class TextNodePatch(
    val previous: TextNodeProps,
    val next: TextNodeProps,
) : NodeViewPatch

internal data class TextFieldNodePatch(
    val previous: TextFieldNodeProps,
    val next: TextFieldNodeProps,
) : NodeViewPatch

internal data class TabPagerNodePatch(
    val previous: TabPagerNodeProps,
    val next: TabPagerNodeProps,
) : NodeViewPatch

internal data class SegmentedControlNodePatch(
    val previous: SegmentedControlNodeProps,
    val next: SegmentedControlNodeProps,
) : NodeViewPatch

internal data class LazyColumnNodePatch(
    val previous: LazyColumnNodeProps,
    val next: LazyColumnNodeProps,
) : NodeViewPatch

internal data class LazyRowNodePatch(
    val previous: LazyRowNodeProps,
    val next: LazyRowNodeProps,
) : NodeViewPatch

internal data class ToggleNodePatch(
    val previous: ToggleNodeProps,
    val next: ToggleNodeProps,
) : NodeViewPatch

internal data class SliderNodePatch(
    val previous: SliderNodeProps,
    val next: SliderNodeProps,
) : NodeViewPatch

internal data class ProgressIndicatorNodePatch(
    val previous: ProgressIndicatorNodeProps,
    val next: ProgressIndicatorNodeProps,
) : NodeViewPatch

internal data class RowNodePatch(
    val previous: RowNodeProps,
    val next: RowNodeProps,
) : NodeViewPatch

internal data class ColumnNodePatch(
    val previous: ColumnNodeProps,
    val next: ColumnNodeProps,
) : NodeViewPatch

internal data class BoxNodePatch(
    val previous: BoxNodeProps,
    val next: BoxNodeProps,
) : NodeViewPatch

internal data class ImageNodePatch(
    val previous: ImageNodeProps,
    val next: ImageNodeProps,
) : NodeViewPatch

internal data class IconButtonNodePatch(
    val previous: IconButtonNodeProps,
    val next: IconButtonNodeProps,
) : NodeViewPatch

internal data class DividerNodePatch(
    val previous: DividerNodeProps,
    val next: DividerNodeProps,
) : NodeViewPatch

internal data class ScrollableColumnNodePatch(
    val previous: ScrollableColumnNodeProps,
    val next: ScrollableColumnNodeProps,
) : NodeViewPatch

internal data class ScrollableRowNodePatch(
    val previous: ScrollableRowNodeProps,
    val next: ScrollableRowNodeProps,
) : NodeViewPatch
