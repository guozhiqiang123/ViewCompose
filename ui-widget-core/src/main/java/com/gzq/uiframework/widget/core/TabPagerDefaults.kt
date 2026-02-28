package com.gzq.uiframework.widget.core

object TabPagerDefaults {
    fun backgroundColor(): Int = Theme.colors.surfaceVariant

    fun indicatorColor(): Int = Theme.colors.primary

    fun selectedTextColor(): Int = Theme.colors.textPrimary

    fun unselectedTextColor(): Int = Theme.colors.textSecondary

    fun rippleColor(): Int = Theme.interactions.pressedOverlay
}
