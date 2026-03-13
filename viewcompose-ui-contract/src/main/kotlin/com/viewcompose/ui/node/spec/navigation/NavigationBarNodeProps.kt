package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.NavigationBarItem

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
    val labelFontWeight: Int? = null,
    val labelFontFamily: UiFontFamily? = null,
    val labelLetterSpacingEm: Float? = null,
    val labelLineHeightSp: Int? = null,
    val labelIncludeFontPadding: Boolean = false,
    val badgeColor: Int,
    val badgeTextColor: Int,
) : NodeSpec
