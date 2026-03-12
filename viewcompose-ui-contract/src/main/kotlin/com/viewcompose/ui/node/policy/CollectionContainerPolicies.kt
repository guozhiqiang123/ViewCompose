package com.viewcompose.ui.node.policy

data class CollectionReusePolicy(
    val sharePool: Boolean = false,
)

data class CollectionMotionPolicy(
    val disableItemAnimator: Boolean = false,
    val animateInsert: Boolean = true,
    val animateRemove: Boolean = true,
    val animateMove: Boolean = true,
    val animateChange: Boolean = true,
)
