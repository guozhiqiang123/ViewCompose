package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.node.LazyListItem
import com.viewcompose.renderer.view.lazy.LazyListState

data class LazyVerticalGridNodeProps(
    val spanCount: Int,
    val contentPadding: Int,
    val horizontalSpacing: Int,
    val verticalSpacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState?,
) : NodeSpec
