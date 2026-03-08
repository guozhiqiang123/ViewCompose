package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.node.LazyListItem
import com.viewcompose.ui.state.PagerState

data class HorizontalPagerNodeProps(
    val pages: List<LazyListItem>,
    val currentPage: Int,
    val onPageChanged: ((Int) -> Unit)?,
    val offscreenPageLimit: Int,
    val pagerState: PagerState?,
    val userScrollEnabled: Boolean,
) : NodeSpec
