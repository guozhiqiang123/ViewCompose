package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.view.lazy.PagerState

data class HorizontalPagerNodeProps(
    val pages: List<LazyListItem>,
    val currentPage: Int,
    val onPageChanged: ((Int) -> Unit)?,
    val offscreenPageLimit: Int,
    val pagerState: PagerState?,
    val userScrollEnabled: Boolean,
) : NodeSpec
