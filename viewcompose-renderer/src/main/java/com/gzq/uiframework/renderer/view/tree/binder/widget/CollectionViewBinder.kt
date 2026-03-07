package com.gzq.uiframework.renderer.view.tree

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.NavigationBarItem
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyRowNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyVerticalGridNodeProps
import com.gzq.uiframework.renderer.node.spec.NavigationBarNodeProps
import com.gzq.uiframework.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeNavigationBarLayout
import com.gzq.uiframework.renderer.view.lazy.LazyListAdapter
import com.gzq.uiframework.renderer.view.lazy.LazyListState

internal object CollectionViewBinder {
    data class LazyColumnSpec(
        val contentPadding: Int,
        val spacing: Int,
        val items: List<LazyListItem>,
        val state: LazyListState? = null,
    )

    data class NavigationBarSpec(
        val items: List<NavigationBarItem>,
        val selectedIndex: Int,
        val onItemSelected: ((Int) -> Unit)?,
        val containerColor: Int,
        val selectedIconColor: Int,
        val unselectedIconColor: Int,
        val selectedLabelColor: Int,
        val unselectedLabelColor: Int,
        val indicatorColor: Int,
        val rippleColor: Int,
        val iconSize: Int,
        val labelSizeSp: Int,
        val badgeColor: Int,
        val badgeTextColor: Int,
    )

    data class LazyVerticalGridSpec(
        val spanCount: Int,
        val contentPadding: Int,
        val horizontalSpacing: Int,
        val verticalSpacing: Int,
        val items: List<LazyListItem>,
        val state: LazyListState?,
    )

    fun bindLazyColumn(
        view: RecyclerView,
        spec: LazyColumnSpec,
    ) {
        val adapter = view.adapter as? LazyListAdapter ?: LazyListAdapter().also {
            view.adapter = it
        }
        ContainerViewBinder.applyLazyListPadding(view, spec.contentPadding)
        ContainerViewBinder.applyLazyListSpacing(view, spec.spacing, LinearLayoutManager.VERTICAL)
        adapter.submitItems(spec.items)
        spec.state?.recyclerView = view
    }

    fun bindLazyRow(
        view: RecyclerView,
        spec: LazyColumnSpec,
    ) {
        val adapter = view.adapter as? LazyListAdapter
            ?: LazyListAdapter(LinearLayoutManager.HORIZONTAL).also {
                view.adapter = it
            }
        ContainerViewBinder.applyLazyListPadding(view, spec.contentPadding)
        ContainerViewBinder.applyLazyListSpacing(view, spec.spacing, LinearLayoutManager.HORIZONTAL)
        adapter.submitItems(spec.items)
        spec.state?.recyclerView = view
    }

    fun bindNavigationBar(
        view: DeclarativeNavigationBarLayout,
        spec: NavigationBarSpec,
    ) {
        view.bind(
            items = spec.items,
            selectedIndex = spec.selectedIndex,
            onItemSelected = spec.onItemSelected,
            containerColor = spec.containerColor,
            selectedIconColor = spec.selectedIconColor,
            unselectedIconColor = spec.unselectedIconColor,
            selectedLabelColor = spec.selectedLabelColor,
            unselectedLabelColor = spec.unselectedLabelColor,
            indicatorColor = spec.indicatorColor,
            rippleColor = spec.rippleColor,
            iconSize = spec.iconSize,
            labelSizeSp = spec.labelSizeSp,
            badgeColor = spec.badgeColor,
            badgeTextColor = spec.badgeTextColor,
        )
    }

    fun bindLazyVerticalGrid(
        view: DeclarativeLazyVerticalGridLayout,
        spec: LazyVerticalGridSpec,
    ) {
        view.bind(
            spanCount = spec.spanCount,
            contentPadding = spec.contentPadding,
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            items = spec.items,
            state = spec.state,
        )
    }

    fun readLazyColumnSpec(node: VNode): LazyColumnSpec {
        val spec = node.spec as? LazyColumnNodeProps
        if (spec != null) {
            return LazyColumnSpec(
                contentPadding = spec.contentPadding,
                spacing = spec.spacing,
                items = spec.items,
                state = spec.state,
            )
        }
        return LazyColumnSpec(
            contentPadding = node.props[TypedPropKeys.LazyContentPadding] ?: 0,
            spacing = node.props[TypedPropKeys.LazySpacing] ?: 0,
            items = node.props[TypedPropKeys.LazyItems] ?: emptyList(),
        )
    }

    fun readLazyRowSpec(node: VNode): LazyColumnSpec {
        val spec = node.spec as? LazyRowNodeProps
        if (spec != null) {
            return LazyColumnSpec(
                contentPadding = spec.contentPadding,
                spacing = spec.spacing,
                items = spec.items,
                state = spec.state,
            )
        }
        return LazyColumnSpec(
            contentPadding = node.props[TypedPropKeys.LazyContentPadding] ?: 0,
            spacing = node.props[TypedPropKeys.LazySpacing] ?: 0,
            items = node.props[TypedPropKeys.LazyItems] ?: emptyList(),
        )
    }

    fun readNavigationBarSpec(node: VNode): NavigationBarSpec {
        val spec = node.spec as? NavigationBarNodeProps
            ?: return NavigationBarSpec(
                items = emptyList(),
                selectedIndex = 0,
                onItemSelected = null,
                containerColor = 0,
                selectedIconColor = 0,
                unselectedIconColor = 0,
                selectedLabelColor = 0,
                unselectedLabelColor = 0,
                indicatorColor = 0,
                rippleColor = 0,
                iconSize = 0,
                labelSizeSp = 12,
                badgeColor = 0,
                badgeTextColor = 0,
            )
        return NavigationBarSpec(
            items = spec.items,
            selectedIndex = spec.selectedIndex,
            onItemSelected = spec.onItemSelected,
            containerColor = spec.containerColor,
            selectedIconColor = spec.selectedIconColor,
            unselectedIconColor = spec.unselectedIconColor,
            selectedLabelColor = spec.selectedLabelColor,
            unselectedLabelColor = spec.unselectedLabelColor,
            indicatorColor = spec.indicatorColor,
            rippleColor = spec.rippleColor,
            iconSize = spec.iconSize,
            labelSizeSp = spec.labelSizeSp,
            badgeColor = spec.badgeColor,
            badgeTextColor = spec.badgeTextColor,
        )
    }

    fun readLazyVerticalGridSpec(node: VNode): LazyVerticalGridSpec {
        val spec = node.spec as? LazyVerticalGridNodeProps
            ?: return LazyVerticalGridSpec(
                spanCount = 2,
                contentPadding = 0,
                horizontalSpacing = 0,
                verticalSpacing = 0,
                items = emptyList(),
                state = null,
            )
        return LazyVerticalGridSpec(
            spanCount = spec.spanCount,
            contentPadding = spec.contentPadding,
            horizontalSpacing = spec.horizontalSpacing,
            verticalSpacing = spec.verticalSpacing,
            items = spec.items,
            state = spec.state,
        )
    }
}
