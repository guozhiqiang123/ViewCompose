package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.LazyListItem
import com.viewcompose.ui.node.policy.CollectionMotionPolicy
import com.viewcompose.ui.node.policy.CollectionReusePolicy
import com.viewcompose.ui.state.PagerState

data class VerticalPagerNodeProps(
    val pages: List<LazyListItem>,
    val currentPage: Int,
    val onPageChanged: ((Int) -> Unit)?,
    val offscreenPageLimit: Int,
    val pagerState: PagerState?,
    val userScrollEnabled: Boolean,
    val reusePolicy: CollectionReusePolicy = CollectionReusePolicy(),
    val motionPolicy: CollectionMotionPolicy = CollectionMotionPolicy(),
    val focusFollowKeyboard: Boolean = false,
) : NodeSpec
