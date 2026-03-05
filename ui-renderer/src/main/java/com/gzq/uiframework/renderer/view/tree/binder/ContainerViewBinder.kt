package com.gzq.uiframework.renderer.view.tree

import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.NavigationBarItem
import com.gzq.uiframework.renderer.node.collection.TabIndicatorPosition
import com.gzq.uiframework.renderer.node.collection.TabIndicatorWidthMode
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeNavigationBarLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeVerticalPagerLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter
import com.gzq.uiframework.renderer.view.lazy.LazyItemSpacingDecoration
import com.gzq.uiframework.renderer.view.lazy.LazyListState
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowRowNodeProps
import com.gzq.uiframework.renderer.node.spec.HorizontalPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyRowNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyVerticalGridNodeProps
import com.gzq.uiframework.renderer.node.spec.NavigationBarNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.ScrollableColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.ScrollableRowNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.TabRowNodeProps
import com.gzq.uiframework.renderer.node.spec.VerticalPagerNodeProps
import android.view.Gravity

internal object ContainerViewBinder {
    data class LinearSpec(
        val spacing: Int,
        val arrangement: MainAxisArrangement,
        val gravity: Int,
    )

    data class BoxSpec(
        val gravity: Int,
    )

    data class LazyColumnSpec(
        val contentPadding: Int,
        val spacing: Int,
        val items: List<LazyListItem>,
        val state: LazyListState? = null,
    )

    data class TabPagerSpec(
        val pages: List<TabPage>,
        val selectedTabIndex: Int,
        val onTabSelected: ((Int) -> Unit)?,
        val backgroundColor: Int,
        val indicatorColor: Int,
        val cornerRadius: Int,
        val indicatorHeight: Int,
        val tabPaddingHorizontal: Int,
        val tabPaddingVertical: Int,
        val selectedTextColor: Int,
        val unselectedTextColor: Int,
        val rippleColor: Int,
    )

    data class SegmentedControlSpec(
        val items: List<SegmentedControlItem>,
        val selectedIndex: Int,
        val onSelectionChange: ((Int) -> Unit)?,
        val enabled: Boolean,
        val backgroundColor: Int,
        val indicatorColor: Int,
        val cornerRadius: Int,
        val textColor: Int,
        val selectedTextColor: Int,
        val rippleColor: Int,
        val textSizeSp: Int,
        val paddingHorizontal: Int,
        val paddingVertical: Int,
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

    data class HorizontalPagerSpec(
        val pages: List<LazyListItem>,
        val currentPage: Int,
        val onPageChanged: ((Int) -> Unit)?,
        val offscreenPageLimit: Int,
        val pagerState: com.gzq.uiframework.renderer.view.lazy.PagerState?,
        val userScrollEnabled: Boolean,
    )

    data class VerticalPagerSpec(
        val pages: List<LazyListItem>,
        val currentPage: Int,
        val onPageChanged: ((Int) -> Unit)?,
        val offscreenPageLimit: Int,
        val pagerState: com.gzq.uiframework.renderer.view.lazy.PagerState?,
        val userScrollEnabled: Boolean,
    )

    data class LazyVerticalGridSpec(
        val spanCount: Int,
        val contentPadding: Int,
        val horizontalSpacing: Int,
        val verticalSpacing: Int,
        val items: List<LazyListItem>,
        val state: com.gzq.uiframework.renderer.view.lazy.LazyListState?,
    )

    data class TabRowSpec(
        val tabs: List<com.gzq.uiframework.renderer.node.collection.TabRowTab>,
        val selectedIndex: Int,
        val onTabSelected: ((Int) -> Unit)?,
        val pagerState: com.gzq.uiframework.renderer.view.lazy.PagerState?,
        val indicatorColor: Int,
        val indicatorHeight: Int,
        val indicatorCornerRadius: Int,
        val indicatorPosition: TabIndicatorPosition,
        val indicatorWidthMode: TabIndicatorWidthMode,
        val indicatorFixedWidth: Int,
        val containerColor: Int,
        val scrollable: Boolean,
        val equalWidth: Boolean,
        val rippleColor: Int,
        val itemSpacing: Int,
        val itemPaddingHorizontal: Int,
        val itemPaddingVertical: Int,
        val minItemWidth: Int,
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

    fun bindScrollableColumn(
        view: DeclarativeScrollableColumnLayout,
        spec: LinearSpec,
    ) {
        bindColumn(view.innerLayout, spec)
    }

    fun bindScrollableRow(
        view: DeclarativeScrollableRowLayout,
        spec: LinearSpec,
    ) {
        bindRow(view.innerLayout, spec)
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

    fun bindLazyColumn(
        view: RecyclerView,
        spec: LazyColumnSpec,
    ) {
        val adapter = view.adapter as? LazyColumnAdapter ?: LazyColumnAdapter().also {
            view.adapter = it
        }
        applyLazyListPadding(view, spec.contentPadding)
        applyLazyListSpacing(view, spec.spacing, LinearLayoutManager.VERTICAL)
        adapter.submitItems(spec.items)
        spec.state?.recyclerView = view
    }

    fun bindLazyRow(
        view: RecyclerView,
        spec: LazyColumnSpec,
    ) {
        val adapter = view.adapter as? LazyColumnAdapter
            ?: LazyColumnAdapter(LinearLayoutManager.HORIZONTAL).also {
                view.adapter = it
            }
        applyLazyListPadding(view, spec.contentPadding)
        applyLazyListSpacing(view, spec.spacing, LinearLayoutManager.HORIZONTAL)
        adapter.submitItems(spec.items)
        spec.state?.recyclerView = view
    }

    fun bindTabPager(
        view: DeclarativeTabPagerLayout,
        spec: TabPagerSpec,
    ) {
        view.bind(
            pages = spec.pages,
            selectedTabIndex = spec.selectedTabIndex,
            onTabSelected = spec.onTabSelected,
            backgroundColor = spec.backgroundColor,
            indicatorColor = spec.indicatorColor,
            cornerRadius = spec.cornerRadius,
            indicatorHeight = spec.indicatorHeight,
            tabPaddingHorizontal = spec.tabPaddingHorizontal,
            tabPaddingVertical = spec.tabPaddingVertical,
            selectedTextColor = spec.selectedTextColor,
            unselectedTextColor = spec.unselectedTextColor,
            rippleColor = spec.rippleColor,
        )
    }

    fun bindSegmentedControl(
        view: DeclarativeSegmentedControlLayout,
        spec: SegmentedControlSpec,
    ) {
        view.bind(
            items = spec.items,
            selectedIndex = spec.selectedIndex,
            onSelectionChange = spec.onSelectionChange,
            enabled = spec.enabled,
            backgroundColor = spec.backgroundColor,
            indicatorColor = spec.indicatorColor,
            cornerRadius = spec.cornerRadius,
            textColor = spec.textColor,
            selectedTextColor = spec.selectedTextColor,
            rippleColor = spec.rippleColor,
            textSizeSp = spec.textSizeSp,
            paddingHorizontal = spec.paddingHorizontal,
            paddingVertical = spec.paddingVertical,
        )
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

    fun readScrollableColumnSpec(node: VNode): LinearSpec {
        val spec = node.spec as? ScrollableColumnNodeProps
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

    fun readScrollableRowSpec(node: VNode): LinearSpec {
        val spec = node.spec as? ScrollableRowNodeProps
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

    fun readTabPagerSpec(node: VNode, defaultRippleColor: Int): TabPagerSpec {
        val spec = node.spec as? TabPagerNodeProps
        if (spec != null) {
            return TabPagerSpec(
                pages = spec.pages,
                selectedTabIndex = spec.selectedTabIndex,
                onTabSelected = spec.onTabSelected,
                backgroundColor = spec.backgroundColor,
                indicatorColor = spec.indicatorColor,
                cornerRadius = spec.cornerRadius,
                indicatorHeight = spec.indicatorHeight,
                tabPaddingHorizontal = spec.tabPaddingHorizontal,
                tabPaddingVertical = spec.tabPaddingVertical,
                selectedTextColor = spec.selectedTextColor,
                unselectedTextColor = spec.unselectedTextColor,
                rippleColor = spec.rippleColor,
            )
        }
        return TabPagerSpec(
            pages = node.props[TypedPropKeys.TabPages] ?: emptyList(),
            selectedTabIndex = node.props[TypedPropKeys.SelectedTabIndex] ?: 0,
            onTabSelected = node.props[TypedPropKeys.OnTabSelected],
            backgroundColor = node.props[TypedPropKeys.TabBackgroundColor] ?: 0,
            indicatorColor = node.props[TypedPropKeys.TabIndicatorColor] ?: 0,
            cornerRadius = node.props[TypedPropKeys.TabCornerRadius] ?: 0,
            indicatorHeight = node.props[TypedPropKeys.TabIndicatorHeight] ?: 0,
            tabPaddingHorizontal = node.props[TypedPropKeys.TabContentPaddingHorizontal] ?: 0,
            tabPaddingVertical = node.props[TypedPropKeys.TabContentPaddingVertical] ?: 0,
            selectedTextColor = node.props[TypedPropKeys.TabSelectedTextColor] ?: 0,
            unselectedTextColor = node.props[TypedPropKeys.TabUnselectedTextColor] ?: 0,
            rippleColor = node.props[TypedPropKeys.TabRippleColor] ?: defaultRippleColor,
        )
    }

    fun readSegmentedControlSpec(node: VNode, defaultRippleColor: Int): SegmentedControlSpec {
        val spec = node.spec as? SegmentedControlNodeProps
        if (spec != null) {
            return SegmentedControlSpec(
                items = spec.items,
                selectedIndex = spec.selectedIndex,
                onSelectionChange = spec.onSelectionChange,
                enabled = spec.enabled,
                backgroundColor = spec.backgroundColor,
                indicatorColor = spec.indicatorColor,
                cornerRadius = spec.cornerRadius,
                textColor = spec.textColor,
                selectedTextColor = spec.selectedTextColor,
                rippleColor = spec.rippleColor,
                textSizeSp = spec.textSizeSp,
                paddingHorizontal = spec.paddingHorizontal,
                paddingVertical = spec.paddingVertical,
            )
        }
        return SegmentedControlSpec(
            items = node.props[TypedPropKeys.SegmentItems] ?: emptyList(),
            selectedIndex = node.props[TypedPropKeys.SegmentSelectedIndex] ?: 0,
            onSelectionChange = node.props[TypedPropKeys.OnSegmentSelected],
            enabled = node.props[TypedPropKeys.Enabled] ?: true,
            backgroundColor = node.props[TypedPropKeys.SegmentBackgroundColor] ?: android.graphics.Color.TRANSPARENT,
            indicatorColor = node.props[TypedPropKeys.SegmentIndicatorColor] ?: android.graphics.Color.TRANSPARENT,
            cornerRadius = node.props[TypedPropKeys.SegmentCornerRadius] ?: 0,
            textColor = node.props[TypedPropKeys.SegmentTextColor] ?: android.graphics.Color.BLACK,
            selectedTextColor = node.props[TypedPropKeys.SegmentSelectedTextColor] ?: android.graphics.Color.WHITE,
            rippleColor = node.props[TypedPropKeys.SegmentRippleColor] ?: defaultRippleColor,
            textSizeSp = node.props[TypedPropKeys.SegmentTextSizeSp] ?: 14,
            paddingHorizontal = node.props[TypedPropKeys.SegmentContentPaddingHorizontal] ?: 0,
            paddingVertical = node.props[TypedPropKeys.SegmentContentPaddingVertical] ?: 0,
        )
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

    fun bindHorizontalPager(
        view: DeclarativeHorizontalPagerLayout,
        spec: HorizontalPagerSpec,
    ) {
        view.bind(
            pages = spec.pages,
            currentPage = spec.currentPage,
            onPageChanged = spec.onPageChanged,
            offscreenPageLimit = spec.offscreenPageLimit,
            pagerState = spec.pagerState,
            userScrollEnabled = spec.userScrollEnabled,
        )
    }

    fun readHorizontalPagerSpec(node: VNode): HorizontalPagerSpec {
        val spec = node.spec as? HorizontalPagerNodeProps
            ?: return HorizontalPagerSpec(
                pages = emptyList(),
                currentPage = 0,
                onPageChanged = null,
                offscreenPageLimit = 1,
                pagerState = null,
                userScrollEnabled = true,
            )
        return HorizontalPagerSpec(
            pages = spec.pages,
            currentPage = spec.currentPage,
            onPageChanged = spec.onPageChanged,
            offscreenPageLimit = spec.offscreenPageLimit,
            pagerState = spec.pagerState,
            userScrollEnabled = spec.userScrollEnabled,
        )
    }

    fun bindTabRow(
        view: DeclarativeTabRowLayout,
        spec: TabRowSpec,
    ) {
        view.bind(
            tabs = spec.tabs,
            selectedIndex = spec.selectedIndex,
            onTabSelected = spec.onTabSelected,
            pagerState = spec.pagerState,
            indicatorColor = spec.indicatorColor,
            indicatorHeight = spec.indicatorHeight,
            indicatorCornerRadius = spec.indicatorCornerRadius,
            indicatorPosition = spec.indicatorPosition,
            indicatorWidthMode = spec.indicatorWidthMode,
            indicatorFixedWidth = spec.indicatorFixedWidth,
            containerColor = spec.containerColor,
            scrollable = spec.scrollable,
            equalWidth = spec.equalWidth,
            rippleColor = spec.rippleColor,
            itemSpacing = spec.itemSpacing,
            itemPaddingHorizontal = spec.itemPaddingHorizontal,
            itemPaddingVertical = spec.itemPaddingVertical,
            minItemWidth = spec.minItemWidth,
        )
    }

    fun readTabRowSpec(node: VNode): TabRowSpec {
        val spec = node.spec as? TabRowNodeProps
            ?: return TabRowSpec(
                tabs = emptyList(),
                selectedIndex = 0,
                onTabSelected = null,
                pagerState = null,
                indicatorColor = 0,
                indicatorHeight = 0,
                indicatorCornerRadius = 0,
                indicatorPosition = TabIndicatorPosition.Bottom,
                indicatorWidthMode = TabIndicatorWidthMode.MatchItem,
                indicatorFixedWidth = 0,
                containerColor = 0,
                scrollable = false,
                equalWidth = true,
                rippleColor = 0,
                itemSpacing = 0,
                itemPaddingHorizontal = 0,
                itemPaddingVertical = 0,
                minItemWidth = 0,
            )
        return TabRowSpec(
            tabs = spec.tabs,
            selectedIndex = spec.selectedIndex,
            onTabSelected = spec.onTabSelected,
            pagerState = spec.pagerState,
            indicatorColor = spec.indicatorColor,
            indicatorHeight = spec.indicatorHeight,
            indicatorCornerRadius = spec.indicatorCornerRadius,
            indicatorPosition = spec.indicatorPosition,
            indicatorWidthMode = spec.indicatorWidthMode,
            indicatorFixedWidth = spec.indicatorFixedWidth,
            containerColor = spec.containerColor,
            scrollable = spec.scrollable,
            equalWidth = spec.equalWidth,
            rippleColor = spec.rippleColor,
            itemSpacing = spec.itemSpacing,
            itemPaddingHorizontal = spec.itemPaddingHorizontal,
            itemPaddingVertical = spec.itemPaddingVertical,
            minItemWidth = spec.minItemWidth,
        )
    }

    fun bindVerticalPager(
        view: DeclarativeVerticalPagerLayout,
        spec: VerticalPagerSpec,
    ) {
        view.bind(
            pages = spec.pages,
            currentPage = spec.currentPage,
            onPageChanged = spec.onPageChanged,
            offscreenPageLimit = spec.offscreenPageLimit,
            pagerState = spec.pagerState,
            userScrollEnabled = spec.userScrollEnabled,
        )
    }

    fun readVerticalPagerSpec(node: VNode): VerticalPagerSpec {
        val spec = node.spec as? VerticalPagerNodeProps
            ?: return VerticalPagerSpec(
                pages = emptyList(),
                currentPage = 0,
                onPageChanged = null,
                offscreenPageLimit = 1,
                pagerState = null,
                userScrollEnabled = true,
            )
        return VerticalPagerSpec(
            pages = spec.pages,
            currentPage = spec.currentPage,
            onPageChanged = spec.onPageChanged,
            offscreenPageLimit = spec.offscreenPageLimit,
            pagerState = spec.pagerState,
            userScrollEnabled = spec.userScrollEnabled,
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
