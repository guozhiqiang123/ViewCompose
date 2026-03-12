package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.LazyListItem
import com.viewcompose.ui.node.policy.CollectionMotionPolicy
import com.viewcompose.ui.node.policy.CollectionReusePolicy
import com.viewcompose.ui.state.LazyListState

data class LazyVerticalGridNodeProps(
    val spanCount: Int,
    val contentPadding: Int,
    val horizontalSpacing: Int,
    val verticalSpacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState?,
    val reusePolicy: CollectionReusePolicy = CollectionReusePolicy(),
    val motionPolicy: CollectionMotionPolicy = CollectionMotionPolicy(),
    val focusFollowKeyboard: Boolean = false,
) : NodeSpec
