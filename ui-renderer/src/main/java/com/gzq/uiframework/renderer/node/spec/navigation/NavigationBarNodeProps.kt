package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.NavigationBarItem

data class NavigationBarNodeProps(
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
) : NodeSpec
