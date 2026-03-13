package com.viewcompose.widget.core

object NavigationBarDefaults {
    fun containerColor(): Int = Theme.colors.surface

    fun selectedIconColor(): Int = Theme.colors.primary

    fun unselectedIconColor(): Int = Theme.colors.textSecondary

    fun selectedLabelColor(): Int = Theme.colors.primary

    fun unselectedLabelColor(): Int = Theme.colors.textSecondary

    fun indicatorColor(): Int = Theme.colors.surfaceVariant

    fun rippleColor(): Int = Theme.colors.ripple

    fun height(): Int = Theme.controls.navigationBar.height

    fun iconSize(): Int = Theme.controls.navigationBar.iconSize

    fun labelSizeSp(): Int = Theme.controls.navigationBar.labelSizeSp

    fun badgeColor(): Int = Theme.colors.error

    fun badgeTextColor(): Int = contentColorFor(Theme.colors.error)
}
