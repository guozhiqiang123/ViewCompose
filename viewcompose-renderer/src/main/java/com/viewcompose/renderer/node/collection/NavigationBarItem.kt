package com.viewcompose.renderer.node

data class NavigationBarItem(
    val label: String,
    val icon: ImageSource.Resource,
    val selectedIcon: ImageSource.Resource? = null,
    val badgeCount: Int? = null,
    val key: Any? = label,
)
