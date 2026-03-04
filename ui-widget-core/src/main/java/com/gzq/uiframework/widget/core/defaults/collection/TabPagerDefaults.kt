package com.gzq.uiframework.widget.core

object TabPagerDefaults {
    fun backgroundColor(): Int {
        val override = LocalContext.current(LocalTabPagerColors)
        return override?.background ?: Theme.colors.surfaceVariant
    }

    fun indicatorColor(): Int {
        val override = LocalContext.current(LocalTabPagerColors)
        return override?.indicator ?: Theme.colors.primary
    }

    fun cornerRadius(): Int = Theme.shapes.cardCornerRadius

    fun indicatorHeight(): Int = 4.dp

    fun tabPaddingHorizontal(): Int = 16.dp

    fun tabPaddingVertical(): Int = 10.dp

    fun selectedTextColor(): Int {
        val override = LocalContext.current(LocalTabPagerColors)
        return override?.selectedText ?: Theme.colors.textPrimary
    }

    fun unselectedTextColor(): Int {
        val override = LocalContext.current(LocalTabPagerColors)
        return override?.text ?: Theme.colors.textSecondary
    }

    fun rippleColor(): Int = pressedOverlayColorFor(Theme.colors.textPrimary)
}
