package com.viewcompose.renderer.view.tree

import com.viewcompose.renderer.node.LazyListItem
import com.viewcompose.renderer.node.SegmentedControlItem
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.collection.TabIndicatorPosition
import com.viewcompose.renderer.node.collection.TabIndicatorWidthMode
import com.viewcompose.renderer.node.spec.HorizontalPagerNodeProps
import com.viewcompose.renderer.node.spec.SegmentedControlNodeProps
import com.viewcompose.renderer.node.spec.TabRowNodeProps
import com.viewcompose.renderer.node.spec.VerticalPagerNodeProps
import com.viewcompose.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.viewcompose.renderer.view.container.DeclarativeSegmentedControlLayout
import com.viewcompose.renderer.view.container.DeclarativeTabRowLayout
import com.viewcompose.renderer.view.container.DeclarativeVerticalPagerLayout
import com.viewcompose.renderer.view.lazy.PagerState

internal object PagerViewBinder {
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
        val tabs: List<com.viewcompose.renderer.node.collection.TabRowTab>,
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

    fun readSegmentedControlSpec(node: VNode, defaultRippleColor: Int): SegmentedControlSpec {
        val spec = node.spec as? SegmentedControlNodeProps ?: SegmentedControlNodeProps(
            items = emptyList(),
            selectedIndex = 0,
            onSelectionChange = null,
            enabled = true,
            backgroundColor = android.graphics.Color.TRANSPARENT,
            indicatorColor = android.graphics.Color.TRANSPARENT,
            cornerRadius = 0,
            textColor = android.graphics.Color.BLACK,
            selectedTextColor = android.graphics.Color.WHITE,
            rippleColor = defaultRippleColor,
            textSizeSp = 14,
            paddingHorizontal = 0,
            paddingVertical = 0,
        )
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
