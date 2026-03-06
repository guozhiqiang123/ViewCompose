package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.ScrollableColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.ScrollableRowNodeProps
import com.gzq.uiframework.renderer.node.spec.PullToRefreshNodeProps
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableRowLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

internal object ScrollableViewBinder {
    data class PullToRefreshSpec(
        val isRefreshing: Boolean,
        val onRefresh: (() -> Unit)?,
        val indicatorColor: Int,
    )

    fun bindScrollableColumn(
        view: DeclarativeScrollableColumnLayout,
        spec: ContainerViewBinder.LinearSpec,
    ) {
        ContainerViewBinder.bindColumn(view.innerLayout, spec)
    }

    fun bindScrollableRow(
        view: DeclarativeScrollableRowLayout,
        spec: ContainerViewBinder.LinearSpec,
    ) {
        ContainerViewBinder.bindRow(view.innerLayout, spec)
    }

    fun bindPullToRefresh(
        view: SwipeRefreshLayout,
        spec: PullToRefreshSpec,
    ) {
        view.isRefreshing = spec.isRefreshing
        view.setOnRefreshListener { spec.onRefresh?.invoke() }
        view.setColorSchemeColors(spec.indicatorColor)
    }

    fun readScrollableColumnSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.spec as? ScrollableColumnNodeProps
        if (spec != null) {
            return ContainerViewBinder.LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = with(ContainerViewSpecReader) { spec.horizontalAlignment.toGravity() },
            )
        }
        return ContainerViewBinder.LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.ColumnMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = with(ContainerViewSpecReader) {
                (node.props[TypedPropKeys.ColumnHorizontalAlignment] ?: HorizontalAlignment.Start).toGravity()
            },
        )
    }

    fun readScrollableRowSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.spec as? ScrollableRowNodeProps
        if (spec != null) {
            return ContainerViewBinder.LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = with(ContainerViewSpecReader) { spec.verticalAlignment.toGravity() },
            )
        }
        return ContainerViewBinder.LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.RowMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = with(ContainerViewSpecReader) {
                (node.props[TypedPropKeys.RowVerticalAlignment] ?: VerticalAlignment.Top).toGravity()
            },
        )
    }

    fun readPullToRefreshSpec(node: VNode): PullToRefreshSpec {
        val spec = node.spec as? PullToRefreshNodeProps
            ?: return PullToRefreshSpec(
                isRefreshing = false,
                onRefresh = null,
                indicatorColor = 0,
            )
        return PullToRefreshSpec(
            isRefreshing = spec.isRefreshing,
            onRefresh = spec.onRefresh,
            indicatorColor = spec.indicatorColor,
        )
    }
}
