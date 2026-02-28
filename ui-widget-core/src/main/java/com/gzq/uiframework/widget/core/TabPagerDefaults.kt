package com.gzq.uiframework.widget.core

object TabPagerDefaults {
    fun backgroundColor(): Int = Theme.colors.surfaceVariant

    fun indicatorColor(): Int = Theme.colors.primary

    fun cornerRadius(): Int = Theme.shapes.cardCornerRadius

    fun indicatorHeight(): Int = 4.dp

    fun tabPaddingHorizontal(): Int = 16.dp

    fun tabPaddingVertical(): Int = 10.dp

    fun selectedTextColor(): Int = Theme.colors.textPrimary

    fun unselectedTextColor(): Int = Theme.colors.textSecondary

    fun rippleColor(): Int = Theme.interactions.pressedOverlay
}
