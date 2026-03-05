package com.gzq.uiframework.renderer.view.tree.patch

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeNavigationBarLayout
import com.gzq.uiframework.renderer.view.container.DeclarativePullToRefreshLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeVerticalPagerLayout
import com.gzq.uiframework.renderer.view.tree.BoxNodePatch
import com.gzq.uiframework.renderer.view.tree.ColumnNodePatch
import com.gzq.uiframework.renderer.view.tree.ContainerViewBinder
import com.gzq.uiframework.renderer.view.tree.FlowColumnNodePatch
import com.gzq.uiframework.renderer.view.tree.NavigationBarNodePatch
import com.gzq.uiframework.renderer.view.tree.FlowRowNodePatch
import com.gzq.uiframework.renderer.view.tree.HorizontalPagerNodePatch
import com.gzq.uiframework.renderer.view.tree.LazyColumnNodePatch
import com.gzq.uiframework.renderer.view.tree.LazyRowNodePatch
import com.gzq.uiframework.renderer.view.tree.LazyVerticalGridNodePatch
import com.gzq.uiframework.renderer.view.tree.PullToRefreshNodePatch
import com.gzq.uiframework.renderer.view.tree.RowNodePatch
import com.gzq.uiframework.renderer.view.tree.ScrollableColumnNodePatch
import com.gzq.uiframework.renderer.view.tree.ScrollableRowNodePatch
import com.gzq.uiframework.renderer.view.tree.SegmentedControlNodePatch
import com.gzq.uiframework.renderer.view.tree.TabPagerNodePatch
import com.gzq.uiframework.renderer.view.tree.TabRowNodePatch
import com.gzq.uiframework.renderer.view.tree.VerticalPagerNodePatch
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter

internal object ContainerNodePatchApplier {
    fun applyRowPatch(
        view: DeclarativeLinearLayout,
        patch: RowNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.spacing != next.spacing) {
            view.itemSpacing = next.spacing
        }
        if (previous.arrangement != next.arrangement) {
            view.mainAxisArrangement = next.arrangement
        }
        if (previous.verticalAlignment != next.verticalAlignment) {
            with(ContainerViewBinder) {
                view.gravity = next.verticalAlignment.toGravity()
            }
        }
    }

    fun applyColumnPatch(
        view: DeclarativeLinearLayout,
        patch: ColumnNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.spacing != next.spacing) {
            view.itemSpacing = next.spacing
        }
        if (previous.arrangement != next.arrangement) {
            view.mainAxisArrangement = next.arrangement
        }
        if (previous.horizontalAlignment != next.horizontalAlignment) {
            with(ContainerViewBinder) {
                view.gravity = next.horizontalAlignment.toGravity()
            }
        }
    }

    fun applyBoxPatch(
        view: DeclarativeBoxLayout,
        patch: BoxNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.contentAlignment != next.contentAlignment) {
            with(ContainerViewBinder) {
                view.contentGravity = next.contentAlignment.toGravity()
            }
        }
    }

    fun applyLazyColumnPatch(
        view: RecyclerView,
        patch: LazyColumnNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.contentPadding != next.contentPadding) {
            ContainerViewBinder.applyLazyListPadding(view, next.contentPadding)
        }
        if (previous.spacing != next.spacing) {
            ContainerViewBinder.applyLazyListSpacing(view, next.spacing, LinearLayoutManager.VERTICAL)
        }
        if (previous.items != next.items) {
            val adapter = view.adapter as? LazyColumnAdapter ?: LazyColumnAdapter().also {
                view.adapter = it
            }
            adapter.submitItems(next.items)
        }
        if (previous.state !== next.state) {
            previous.state?.recyclerView = null
            next.state?.recyclerView = view
        }
    }

    fun applyLazyRowPatch(
        view: RecyclerView,
        patch: LazyRowNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.contentPadding != next.contentPadding) {
            ContainerViewBinder.applyLazyListPadding(view, next.contentPadding)
        }
        if (previous.spacing != next.spacing) {
            ContainerViewBinder.applyLazyListSpacing(view, next.spacing, LinearLayoutManager.HORIZONTAL)
        }
        if (previous.items != next.items) {
            val adapter = view.adapter as? LazyColumnAdapter
                ?: LazyColumnAdapter(LinearLayoutManager.HORIZONTAL).also {
                    view.adapter = it
                }
            adapter.submitItems(next.items)
        }
        if (previous.state !== next.state) {
            previous.state?.recyclerView = null
            next.state?.recyclerView = view
        }
    }

    fun applyTabPagerPatch(
        view: DeclarativeTabPagerLayout,
        patch: TabPagerNodePatch,
    ) {
        ContainerViewBinder.bindTabPager(
            view = view,
            spec = ContainerViewBinder.TabPagerSpec(
                pages = patch.next.pages,
                selectedTabIndex = patch.next.selectedTabIndex,
                onTabSelected = patch.next.onTabSelected,
                backgroundColor = patch.next.backgroundColor,
                indicatorColor = patch.next.indicatorColor,
                cornerRadius = patch.next.cornerRadius,
                indicatorHeight = patch.next.indicatorHeight,
                tabPaddingHorizontal = patch.next.tabPaddingHorizontal,
                tabPaddingVertical = patch.next.tabPaddingVertical,
                selectedTextColor = patch.next.selectedTextColor,
                unselectedTextColor = patch.next.unselectedTextColor,
                rippleColor = patch.next.rippleColor,
            ),
        )
    }

    fun applySegmentedControlPatch(
        view: DeclarativeSegmentedControlLayout,
        patch: SegmentedControlNodePatch,
    ) {
        ContainerViewBinder.bindSegmentedControl(
            view = view,
            spec = ContainerViewBinder.SegmentedControlSpec(
                items = patch.next.items,
                selectedIndex = patch.next.selectedIndex,
                onSelectionChange = patch.next.onSelectionChange,
                enabled = patch.next.enabled,
                backgroundColor = patch.next.backgroundColor,
                indicatorColor = patch.next.indicatorColor,
                cornerRadius = patch.next.cornerRadius,
                textColor = patch.next.textColor,
                selectedTextColor = patch.next.selectedTextColor,
                rippleColor = patch.next.rippleColor,
                textSizeSp = patch.next.textSizeSp,
                paddingHorizontal = patch.next.paddingHorizontal,
                paddingVertical = patch.next.paddingVertical,
            ),
        )
    }

    fun applyScrollableColumnPatch(
        view: DeclarativeScrollableColumnLayout,
        patch: ScrollableColumnNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.spacing != next.spacing) {
            view.innerLayout.itemSpacing = next.spacing
        }
        if (previous.arrangement != next.arrangement) {
            view.innerLayout.mainAxisArrangement = next.arrangement
        }
        if (previous.horizontalAlignment != next.horizontalAlignment) {
            with(ContainerViewBinder) {
                view.innerLayout.gravity = next.horizontalAlignment.toGravity()
            }
        }
    }

    fun applyScrollableRowPatch(
        view: DeclarativeScrollableRowLayout,
        patch: ScrollableRowNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.spacing != next.spacing) {
            view.innerLayout.itemSpacing = next.spacing
        }
        if (previous.arrangement != next.arrangement) {
            view.innerLayout.mainAxisArrangement = next.arrangement
        }
        if (previous.verticalAlignment != next.verticalAlignment) {
            with(ContainerViewBinder) {
                view.innerLayout.gravity = next.verticalAlignment.toGravity()
            }
        }
    }

    fun applyFlowRowPatch(
        view: DeclarativeFlowRowLayout,
        patch: FlowRowNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.horizontalSpacing != next.horizontalSpacing) {
            view.horizontalSpacing = next.horizontalSpacing
        }
        if (previous.verticalSpacing != next.verticalSpacing) {
            view.verticalSpacing = next.verticalSpacing
        }
        if (previous.maxItemsInEachRow != next.maxItemsInEachRow) {
            view.maxItemsInEachRow = next.maxItemsInEachRow
        }
    }

    fun applyFlowColumnPatch(
        view: DeclarativeFlowColumnLayout,
        patch: FlowColumnNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.horizontalSpacing != next.horizontalSpacing) {
            view.horizontalSpacing = next.horizontalSpacing
        }
        if (previous.verticalSpacing != next.verticalSpacing) {
            view.verticalSpacing = next.verticalSpacing
        }
        if (previous.maxItemsInEachColumn != next.maxItemsInEachColumn) {
            view.maxItemsInEachColumn = next.maxItemsInEachColumn
        }
    }

    fun applyNavigationBarPatch(
        view: DeclarativeNavigationBarLayout,
        patch: NavigationBarNodePatch,
    ) {
        ContainerViewBinder.bindNavigationBar(
            view = view,
            spec = ContainerViewBinder.NavigationBarSpec(
                items = patch.next.items,
                selectedIndex = patch.next.selectedIndex,
                onItemSelected = patch.next.onItemSelected,
                containerColor = patch.next.containerColor,
                selectedIconColor = patch.next.selectedIconColor,
                unselectedIconColor = patch.next.unselectedIconColor,
                selectedLabelColor = patch.next.selectedLabelColor,
                unselectedLabelColor = patch.next.unselectedLabelColor,
                indicatorColor = patch.next.indicatorColor,
                rippleColor = patch.next.rippleColor,
                iconSize = patch.next.iconSize,
                labelSizeSp = patch.next.labelSizeSp,
                badgeColor = patch.next.badgeColor,
                badgeTextColor = patch.next.badgeTextColor,
            ),
        )
    }

    fun applyHorizontalPagerPatch(
        view: DeclarativeHorizontalPagerLayout,
        patch: HorizontalPagerNodePatch,
    ) {
        ContainerViewBinder.bindHorizontalPager(
            view = view,
            spec = ContainerViewBinder.HorizontalPagerSpec(
                pages = patch.next.pages,
                currentPage = patch.next.currentPage,
                onPageChanged = patch.next.onPageChanged,
                offscreenPageLimit = patch.next.offscreenPageLimit,
                pagerState = patch.next.pagerState,
                userScrollEnabled = patch.next.userScrollEnabled,
            ),
        )
    }

    fun applyTabRowPatch(
        view: DeclarativeTabRowLayout,
        patch: TabRowNodePatch,
    ) {
        ContainerViewBinder.bindTabRow(
            view = view,
            spec = ContainerViewBinder.TabRowSpec(
                tabs = patch.next.tabs,
                selectedIndex = patch.next.selectedIndex,
                onTabSelected = patch.next.onTabSelected,
                pagerState = patch.next.pagerState,
                indicatorColor = patch.next.indicatorColor,
                indicatorHeight = patch.next.indicatorHeight,
                indicatorCornerRadius = patch.next.indicatorCornerRadius,
                indicatorPosition = patch.next.indicatorPosition,
                indicatorWidthMode = patch.next.indicatorWidthMode,
                indicatorFixedWidth = patch.next.indicatorFixedWidth,
                containerColor = patch.next.containerColor,
                scrollable = patch.next.scrollable,
                equalWidth = patch.next.equalWidth,
                rippleColor = patch.next.rippleColor,
                itemSpacing = patch.next.itemSpacing,
                itemPaddingHorizontal = patch.next.itemPaddingHorizontal,
                itemPaddingVertical = patch.next.itemPaddingVertical,
                minItemWidth = patch.next.minItemWidth,
            ),
        )
    }

    fun applyVerticalPagerPatch(
        view: DeclarativeVerticalPagerLayout,
        patch: VerticalPagerNodePatch,
    ) {
        ContainerViewBinder.bindVerticalPager(
            view = view,
            spec = ContainerViewBinder.VerticalPagerSpec(
                pages = patch.next.pages,
                currentPage = patch.next.currentPage,
                onPageChanged = patch.next.onPageChanged,
                offscreenPageLimit = patch.next.offscreenPageLimit,
                pagerState = patch.next.pagerState,
                userScrollEnabled = patch.next.userScrollEnabled,
            ),
        )
    }

    fun applyLazyVerticalGridPatch(
        view: DeclarativeLazyVerticalGridLayout,
        patch: LazyVerticalGridNodePatch,
    ) {
        ContainerViewBinder.bindLazyVerticalGrid(
            view = view,
            spec = ContainerViewBinder.LazyVerticalGridSpec(
                spanCount = patch.next.spanCount,
                contentPadding = patch.next.contentPadding,
                horizontalSpacing = patch.next.horizontalSpacing,
                verticalSpacing = patch.next.verticalSpacing,
                items = patch.next.items,
                state = patch.next.state,
            ),
        )
    }

    fun applyPullToRefreshPatch(
        view: DeclarativePullToRefreshLayout,
        patch: PullToRefreshNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.isRefreshing != next.isRefreshing) {
            view.swipeRefreshLayout.isRefreshing = next.isRefreshing
        }
        if (previous.onRefresh !== next.onRefresh) {
            view.swipeRefreshLayout.setOnRefreshListener { next.onRefresh?.invoke() }
        }
        if (previous.indicatorColor != next.indicatorColor) {
            view.swipeRefreshLayout.setColorSchemeColors(next.indicatorColor)
        }
        if (previous.spacing != next.spacing) {
            view.innerLayout.itemSpacing = next.spacing
        }
        if (previous.arrangement != next.arrangement) {
            view.innerLayout.mainAxisArrangement = next.arrangement
        }
        if (previous.horizontalAlignment != next.horizontalAlignment) {
            with(ContainerViewBinder) {
                view.innerLayout.gravity = next.horizontalAlignment.toGravity()
            }
        }
    }
}
