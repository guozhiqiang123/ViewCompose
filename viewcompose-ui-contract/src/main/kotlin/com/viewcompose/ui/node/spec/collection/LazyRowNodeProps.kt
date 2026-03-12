package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.LazyListItem
import com.viewcompose.ui.node.policy.CollectionMotionPolicy
import com.viewcompose.ui.node.policy.CollectionReusePolicy
import com.viewcompose.ui.state.LazyListState

data class LazyRowNodeProps(
    val contentPadding: Int,
    val spacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState? = null,
    val reusePolicy: CollectionReusePolicy = CollectionReusePolicy(),
    val motionPolicy: CollectionMotionPolicy = CollectionMotionPolicy(),
) : NodeSpec
