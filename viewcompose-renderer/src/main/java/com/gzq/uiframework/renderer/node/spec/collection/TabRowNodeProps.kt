package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.collection.TabIndicatorPosition
import com.gzq.uiframework.renderer.node.collection.TabIndicatorWidthMode
import com.gzq.uiframework.renderer.node.collection.TabRowTab
import com.gzq.uiframework.renderer.view.lazy.PagerState

data class TabRowNodeProps(
    val tabs: List<TabRowTab>,
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
) : NodeSpec
