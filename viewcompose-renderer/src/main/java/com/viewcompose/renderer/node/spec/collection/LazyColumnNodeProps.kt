package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.node.LazyListItem
import com.viewcompose.renderer.view.lazy.LazyListState

data class LazyColumnNodeProps(
    val contentPadding: Int,
    val spacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState? = null,
) : NodeSpec
