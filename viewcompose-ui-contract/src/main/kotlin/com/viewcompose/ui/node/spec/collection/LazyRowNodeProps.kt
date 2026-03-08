package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.LazyListItem
import com.viewcompose.ui.state.LazyListState

data class LazyRowNodeProps(
    val contentPadding: Int,
    val spacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState? = null,
) : NodeSpec
