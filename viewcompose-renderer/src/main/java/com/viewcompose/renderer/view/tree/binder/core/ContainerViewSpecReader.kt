package com.viewcompose.renderer.view.tree

import android.view.Gravity
import com.viewcompose.renderer.layout.BoxAlignment
import com.viewcompose.renderer.layout.HorizontalAlignment
import com.viewcompose.renderer.layout.MainAxisArrangement
import com.viewcompose.renderer.layout.VerticalAlignment
import com.viewcompose.renderer.node.TypedPropKeys
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.AndroidViewNodeProps
import com.viewcompose.renderer.node.spec.BoxNodeProps
import com.viewcompose.renderer.node.spec.ColumnNodeProps
import com.viewcompose.renderer.node.spec.DividerNodeProps
import com.viewcompose.renderer.node.spec.FlowColumnNodeProps
import com.viewcompose.renderer.node.spec.FlowRowNodeProps
import com.viewcompose.renderer.node.spec.RowNodeProps

internal object ContainerViewSpecReader {
    fun readRowSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.spec as? RowNodeProps
        if (spec != null) {
            return ContainerViewBinder.LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = spec.verticalAlignment.toGravity(),
            )
        }
        return ContainerViewBinder.LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.RowMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = (node.props[TypedPropKeys.RowVerticalAlignment] ?: VerticalAlignment.Top).toGravity(),
        )
    }

    fun readColumnSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.spec as? ColumnNodeProps
        if (spec != null) {
            return ContainerViewBinder.LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = spec.horizontalAlignment.toGravity(),
            )
        }
        return ContainerViewBinder.LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.ColumnMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = (node.props[TypedPropKeys.ColumnHorizontalAlignment] ?: HorizontalAlignment.Start).toGravity(),
        )
    }

    fun readFlowRowSpec(node: VNode): ContainerViewBinder.FlowRowSpec {
        val spec = node.spec as? FlowRowNodeProps
            ?: return ContainerViewBinder.FlowRowSpec(
                horizontalSpacing = 0,
                verticalSpacing = 0,
                maxItemsInEachRow = Int.MAX_VALUE,
            )
        return ContainerViewBinder.FlowRowSpec(
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            maxItemsInEachRow = spec.maxItemsInEachRow,
        )
    }

    fun readFlowColumnSpec(node: VNode): ContainerViewBinder.FlowColumnSpec {
        val spec = node.spec as? FlowColumnNodeProps
            ?: return ContainerViewBinder.FlowColumnSpec(
                horizontalSpacing = 0,
                verticalSpacing = 0,
                maxItemsInEachColumn = Int.MAX_VALUE,
            )
        return ContainerViewBinder.FlowColumnSpec(
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            maxItemsInEachColumn = spec.maxItemsInEachColumn,
        )
    }

    fun readBoxSpec(node: VNode): ContainerViewBinder.BoxSpec {
        val spec = node.spec as? BoxNodeProps
        if (spec != null) {
            return ContainerViewBinder.BoxSpec(
                gravity = spec.contentAlignment.toGravity(),
            )
        }
        return ContainerViewBinder.BoxSpec(
            gravity = (node.props[TypedPropKeys.BoxAlignment] ?: BoxAlignment.TopStart).toGravity(),
        )
    }

    fun readDividerSpec(node: VNode): ContainerViewBinder.DividerSpec {
        val spec = node.spec as? DividerNodeProps
        if (spec != null) {
            return ContainerViewBinder.DividerSpec(
                color = spec.color,
                thickness = spec.thickness,
            )
        }
        return ContainerViewBinder.DividerSpec(
            color = node.props[TypedPropKeys.DividerColor] ?: android.graphics.Color.BLACK,
            thickness = node.props[TypedPropKeys.DividerThickness] ?: 1,
        )
    }

    fun readAndroidViewSpec(node: VNode): ContainerViewBinder.AndroidViewSpec {
        val spec = node.spec as? AndroidViewNodeProps
        if (spec != null) {
            return ContainerViewBinder.AndroidViewSpec(update = spec.update)
        }
        return ContainerViewBinder.AndroidViewSpec(
            update = node.props[TypedPropKeys.ViewUpdate],
        )
    }

    internal fun VerticalAlignment.toGravity(): Int {
        return when (this) {
            VerticalAlignment.Top -> Gravity.TOP
            VerticalAlignment.Center -> Gravity.CENTER_VERTICAL
            VerticalAlignment.Bottom -> Gravity.BOTTOM
        }
    }

    internal fun HorizontalAlignment.toGravity(): Int {
        return when (this) {
            HorizontalAlignment.Start -> Gravity.START
            HorizontalAlignment.Center -> Gravity.CENTER_HORIZONTAL
            HorizontalAlignment.End -> Gravity.END
        }
    }

    internal fun BoxAlignment.toGravity(): Int {
        return when (this) {
            BoxAlignment.TopStart -> Gravity.TOP or Gravity.START
            BoxAlignment.TopCenter -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            BoxAlignment.TopEnd -> Gravity.TOP or Gravity.END
            BoxAlignment.CenterStart -> Gravity.CENTER_VERTICAL or Gravity.START
            BoxAlignment.Center -> Gravity.CENTER
            BoxAlignment.CenterEnd -> Gravity.CENTER_VERTICAL or Gravity.END
            BoxAlignment.BottomStart -> Gravity.BOTTOM or Gravity.START
            BoxAlignment.BottomCenter -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            BoxAlignment.BottomEnd -> Gravity.BOTTOM or Gravity.END
        }
    }
}
