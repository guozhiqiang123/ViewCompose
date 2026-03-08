package com.viewcompose.renderer.view.tree

import android.view.Gravity
import com.viewcompose.ui.layout.BoxAlignment
import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.AndroidViewNodeProps
import com.viewcompose.ui.node.spec.BoxNodeProps
import com.viewcompose.ui.node.spec.ColumnNodeProps
import com.viewcompose.ui.node.spec.DividerNodeProps
import com.viewcompose.ui.node.spec.FlowColumnNodeProps
import com.viewcompose.ui.node.spec.FlowRowNodeProps
import com.viewcompose.ui.node.spec.RowNodeProps

internal object ContainerViewSpecReader {
    fun readRowSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.requireSpec<RowNodeProps>()
        return ContainerViewBinder.LinearSpec(
            spacing = spec.spacing,
            arrangement = spec.arrangement,
            gravity = spec.verticalAlignment.toGravity(),
        )
    }

    fun readColumnSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.requireSpec<ColumnNodeProps>()
        return ContainerViewBinder.LinearSpec(
            spacing = spec.spacing,
            arrangement = spec.arrangement,
            gravity = spec.horizontalAlignment.toGravity(),
        )
    }

    fun readFlowRowSpec(node: VNode): ContainerViewBinder.FlowRowSpec {
        val spec = node.requireSpec<FlowRowNodeProps>()
        return ContainerViewBinder.FlowRowSpec(
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            maxItemsInEachRow = spec.maxItemsInEachRow,
        )
    }

    fun readFlowColumnSpec(node: VNode): ContainerViewBinder.FlowColumnSpec {
        val spec = node.requireSpec<FlowColumnNodeProps>()
        return ContainerViewBinder.FlowColumnSpec(
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            maxItemsInEachColumn = spec.maxItemsInEachColumn,
        )
    }

    fun readBoxSpec(node: VNode): ContainerViewBinder.BoxSpec {
        val spec = node.requireSpec<BoxNodeProps>()
        return ContainerViewBinder.BoxSpec(
            gravity = spec.contentAlignment.toGravity(),
        )
    }

    fun readDividerSpec(node: VNode): ContainerViewBinder.DividerSpec {
        val spec = node.requireSpec<DividerNodeProps>()
        return ContainerViewBinder.DividerSpec(
            color = spec.color,
            thickness = spec.thickness,
        )
    }

    fun readAndroidViewSpec(node: VNode): ContainerViewBinder.AndroidViewSpec {
        val spec = node.requireSpec<AndroidViewNodeProps>()
        return ContainerViewBinder.AndroidViewSpec(
            update = spec.update,
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
