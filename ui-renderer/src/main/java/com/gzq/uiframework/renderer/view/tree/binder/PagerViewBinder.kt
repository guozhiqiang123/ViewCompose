package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.collection.TabIndicatorPosition
import com.gzq.uiframework.renderer.node.collection.TabIndicatorWidthMode
import com.gzq.uiframework.renderer.node.spec.HorizontalPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.TabRowNodeProps
import com.gzq.uiframework.renderer.node.spec.VerticalPagerNodeProps
import com.gzq.uiframework.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeVerticalPagerLayout
import com.gzq.uiframework.renderer.view.lazy.PagerState

internal object PagerViewBinder {
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

    data class HorizontalPagerSpec(
        val pages: List<LazyListItem>,
        val currentPage: Int,
        val onPageChanged: ((Int) -> Unit)?,
        val offscreenPageLimit: Int,
        val pagerState: PagerState?,
        val userScrollEnabled: Boolean,
    )

    data class VerticalPagerSpec(
        val pages: List<LazyListItem>,
        val currentPage: Int,
        val onPageChanged: ((Int) -> Unit)?,
        val offscreenPageLimit: Int,
        val pagerState: PagerState?,
        val userScrollEnabled: Boolean,
    )

    data class TabRowSpec(
        val tabs: List<com.gzq.uiframework.renderer.node.collection.TabRowTab>,
        val selectedIndex: Int,
        val onTabSelected: ((Int) -> Unit)?,
        val pagerState: PagerState?,
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
}
