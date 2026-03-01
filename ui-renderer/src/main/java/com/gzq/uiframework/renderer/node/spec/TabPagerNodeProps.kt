package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.TabPage

data class TabPagerNodeProps(
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
) : NodeSpec
