package com.viewcompose.widget.core

object NavigationBarDefaults {
    fun containerColor(): Int = Theme.colors.surface

    fun selectedIconColor(): Int = Theme.colors.primary

    fun unselectedIconColor(): Int = Theme.colors.textSecondary

    fun selectedLabelColor(): Int = Theme.colors.primary

    fun unselectedLabelColor(): Int = Theme.colors.textSecondary

    fun indicatorColor(): Int = Theme.colors.surfaceVariant

    fun rippleColor(): Int = Theme.colors.ripple

    fun height(): Int = 80.dp

    fun iconSize(): Int = 24.dp

    fun labelSizeSp(): Int = 12

    fun badgeColor(): Int = 0xFFFF3B30.toInt()

    fun badgeTextColor(): Int = 0xFFFFFFFF.toInt()
}
