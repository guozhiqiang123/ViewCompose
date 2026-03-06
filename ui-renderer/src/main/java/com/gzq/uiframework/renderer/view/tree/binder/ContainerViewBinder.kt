package com.gzq.uiframework.renderer.view.tree

import android.view.Gravity
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowRowNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.lazy.LazyItemSpacingDecoration

internal object ContainerViewBinder {
    data class LinearSpec(
        val spacing: Int,
        val arrangement: MainAxisArrangement,
        val gravity: Int,
    )

    data class BoxSpec(
        val gravity: Int,
    )

    data class DividerSpec(
        val color: Int,
        val thickness: Int,
    )

    data class AndroidViewSpec(
        val update: ((android.view.View) -> Unit)?,
    )

    data class FlowRowSpec(
        val horizontalSpacing: Int,
        val verticalSpacing: Int,
        val maxItemsInEachRow: Int,
    )

    data class FlowColumnSpec(
        val horizontalSpacing: Int,
        val verticalSpacing: Int,
        val maxItemsInEachColumn: Int,
    )

    fun bindRow(
        view: DeclarativeLinearLayout,
        spec: LinearSpec,
    ) {
        view.orientation = LinearLayout.HORIZONTAL
        view.itemSpacing = spec.spacing
        view.mainAxisArrangement = spec.arrangement
        view.gravity = spec.gravity
    }

    fun bindColumn(
        view: DeclarativeLinearLayout,
        spec: LinearSpec,
    ) {
        view.orientation = LinearLayout.VERTICAL
        view.itemSpacing = spec.spacing
        view.mainAxisArrangement = spec.arrangement
        view.gravity = spec.gravity
    }

    fun bindBox(
        view: DeclarativeBoxLayout,
        spec: BoxSpec,
    ) {
        view.contentGravity = spec.gravity
    }

    fun bindFlowRow(
        view: DeclarativeFlowRowLayout,
        spec: FlowRowSpec,
    ) {
        view.horizontalSpacing = spec.horizontalSpacing
        view.verticalSpacing = spec.verticalSpacing
        view.maxItemsInEachRow = spec.maxItemsInEachRow
    }

    fun bindFlowColumn(
        view: DeclarativeFlowColumnLayout,
        spec: FlowColumnSpec,
    ) {
        view.horizontalSpacing = spec.horizontalSpacing
        view.verticalSpacing = spec.verticalSpacing
        view.maxItemsInEachColumn = spec.maxItemsInEachColumn
    }

    fun readRowSpec(node: VNode): LinearSpec {
        val spec = node.spec as? RowNodeProps
        if (spec != null) {
            return LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = spec.verticalAlignment.toGravity(),
            )
        }
        return LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.RowMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = (node.props[TypedPropKeys.RowVerticalAlignment] ?: VerticalAlignment.Top).toGravity(),
        )
    }

    fun readColumnSpec(node: VNode): LinearSpec {
        val spec = node.spec as? ColumnNodeProps
        if (spec != null) {
            return LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = spec.horizontalAlignment.toGravity(),
            )
        }
        return LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.ColumnMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = (node.props[TypedPropKeys.ColumnHorizontalAlignment] ?: HorizontalAlignment.Start).toGravity(),
        )
    }

    fun readFlowRowSpec(node: VNode): FlowRowSpec {
        val spec = node.spec as? FlowRowNodeProps
            ?: return FlowRowSpec(
                horizontalSpacing = 0,
                verticalSpacing = 0,
                maxItemsInEachRow = Int.MAX_VALUE,
            )
        return FlowRowSpec(
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            maxItemsInEachRow = spec.maxItemsInEachRow,
        )
    }

    fun readFlowColumnSpec(node: VNode): FlowColumnSpec {
        val spec = node.spec as? FlowColumnNodeProps
            ?: return FlowColumnSpec(
                horizontalSpacing = 0,
                verticalSpacing = 0,
                maxItemsInEachColumn = Int.MAX_VALUE,
            )
        return FlowColumnSpec(
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            maxItemsInEachColumn = spec.maxItemsInEachColumn,
        )
    }

    fun readBoxSpec(node: VNode): BoxSpec {
        val spec = node.spec as? BoxNodeProps
        if (spec != null) {
            return BoxSpec(
                gravity = spec.contentAlignment.toGravity(),
            )
        }
        return BoxSpec(
            gravity = (node.props[TypedPropKeys.BoxAlignment] ?: BoxAlignment.TopStart).toGravity(),
        )
    }

    fun readDividerSpec(node: VNode): DividerSpec {
        val spec = node.spec as? DividerNodeProps
        if (spec != null) {
            return DividerSpec(
                color = spec.color,
                thickness = spec.thickness,
            )
        }
        return DividerSpec(
            color = node.props[TypedPropKeys.DividerColor] ?: android.graphics.Color.BLACK,
            thickness = node.props[TypedPropKeys.DividerThickness] ?: 1,
        )
    }

    fun readAndroidViewSpec(node: VNode): AndroidViewSpec {
        val spec = node.spec as? AndroidViewNodeProps
        if (spec != null) {
            return AndroidViewSpec(update = spec.update)
        }
        return AndroidViewSpec(
            update = node.props[TypedPropKeys.ViewUpdate],
        )
    }

    internal fun applyLazyListPadding(
        recyclerView: RecyclerView,
        padding: Int,
    ) {
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = padding == 0
    }

    internal fun applyLazyListSpacing(
        recyclerView: RecyclerView,
        spacing: Int,
        orientation: Int = LinearLayoutManager.VERTICAL,
    ) {
        val existing = recyclerView.getTag(R.id.ui_framework_lazy_spacing_decoration) as? LazyItemSpacingDecoration
        if (existing != null) {
            existing.updateSpacing(spacing)
            recyclerView.invalidateItemDecorations()
            return
        }
        val decoration = LazyItemSpacingDecoration(spacing, orientation)
        recyclerView.setTag(R.id.ui_framework_lazy_spacing_decoration, decoration)
        recyclerView.addItemDecoration(decoration)
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
