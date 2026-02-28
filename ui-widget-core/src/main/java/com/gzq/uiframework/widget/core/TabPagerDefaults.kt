package com.gzq.uiframework.widget.core

object TabPagerDefaults {
    fun backgroundColor(): Int = Theme.components.tabPager.background

    fun indicatorColor(): Int = Theme.components.tabPager.indicator

    fun cornerRadius(): Int = Theme.shapes.cardCornerRadius

    fun indicatorHeight(): Int = 4.dp

    fun tabPaddingHorizontal(): Int = 16.dp

    fun tabPaddingVertical(): Int = 10.dp

    fun selectedTextColor(): Int = Theme.components.tabPager.selectedText

    fun unselectedTextColor(): Int = Theme.components.tabPager.text

    fun rippleColor(): Int = Theme.interactions.pressedOverlay
}
