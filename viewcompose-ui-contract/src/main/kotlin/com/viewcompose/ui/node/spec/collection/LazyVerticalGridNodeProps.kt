package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.LazyListItem
import com.viewcompose.ui.state.LazyListState

data class LazyVerticalGridNodeProps(
    val spanCount: Int,
    val contentPadding: Int,
    val horizontalSpacing: Int,
    val verticalSpacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState?,
) : NodeSpec
