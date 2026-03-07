package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.view.lazy.LazyListState

data class LazyRowNodeProps(
    val contentPadding: Int,
    val spacing: Int,
    val items: List<LazyListItem>,
    val state: LazyListState? = null,
) : NodeSpec
