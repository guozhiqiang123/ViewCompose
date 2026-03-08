package com.viewcompose.renderer.view.tree

import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.ScrollableColumnNodeProps
import com.viewcompose.renderer.node.spec.ScrollableRowNodeProps
import com.viewcompose.renderer.node.spec.PullToRefreshNodeProps
import com.viewcompose.renderer.layout.HorizontalAlignment
import com.viewcompose.renderer.layout.MainAxisArrangement
import com.viewcompose.renderer.layout.VerticalAlignment
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
        val spec = node.spec as? ScrollableColumnNodeProps
            ?: ScrollableColumnNodeProps(
                spacing = 0,
                arrangement = MainAxisArrangement.Start,
                horizontalAlignment = HorizontalAlignment.Start,
            )
        return ContainerViewBinder.LinearSpec(
            spacing = spec.spacing,
            arrangement = spec.arrangement,
            gravity = with(ContainerViewSpecReader) { spec.horizontalAlignment.toGravity() },
        )
    }

    fun readScrollableRowSpec(node: VNode): ContainerViewBinder.LinearSpec {
        val spec = node.spec as? ScrollableRowNodeProps
            ?: ScrollableRowNodeProps(
                spacing = 0,
                arrangement = MainAxisArrangement.Start,
                verticalAlignment = VerticalAlignment.Top,
            )
        return ContainerViewBinder.LinearSpec(
            spacing = spec.spacing,
            arrangement = spec.arrangement,
            gravity = with(ContainerViewSpecReader) { spec.verticalAlignment.toGravity() },
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
