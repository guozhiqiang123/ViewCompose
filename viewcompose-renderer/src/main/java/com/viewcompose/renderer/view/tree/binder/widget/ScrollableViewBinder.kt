package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.ScrollableColumnNodeProps
import com.viewcompose.ui.node.spec.ScrollableRowNodeProps
import com.viewcompose.ui.node.spec.PullToRefreshNodeProps
import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.MainAxisArrangement
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.renderer.view.container.DeclarativeScrollableColumnLayout
import com.viewcompose.renderer.view.container.DeclarativeScrollableRowLayout
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
        val spec = node.requireSpec<ScrollableColumnNodeProps>()
        return ContainerViewBinder.LinearSpec(
            spacing = spec.spacing,
            arrangement = spec.arrangement,
            gravity = with(ContainerViewSpecReader) { spec.horizontalAlignment.toGravity() },
        )
    }

    fun readScrollableRowSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.requireSpec<ScrollableRowNodeProps>()
        return ContainerViewBinder.LinearSpec(
            spacing = spec.spacing,
            arrangement = spec.arrangement,
            gravity = with(ContainerViewSpecReader) { spec.verticalAlignment.toGravity() },
        )
    }

    fun readPullToRefreshSpec(node: VNode): PullToRefreshSpec {
        val spec = node.requireSpec<PullToRefreshNodeProps>()
        return PullToRefreshSpec(
            isRefreshing = spec.isRefreshing,
            onRefresh = spec.onRefresh,
            indicatorColor = spec.indicatorColor,
        )
    }
}
