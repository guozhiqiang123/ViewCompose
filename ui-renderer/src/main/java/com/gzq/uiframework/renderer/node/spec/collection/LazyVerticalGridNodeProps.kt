package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.view.lazy.LazyListState

data class LazyVerticalGridNodeProps(
    val spanCount: Int,
    val contentPadding: Int,
    val horizontalSpacing: Int,
    val verticalSpacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState?,
) : NodeSpec
