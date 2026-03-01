package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.LazyListItem

data class LazyColumnNodeProps(
    val contentPadding: Int,
    val spacing: Int,
    val items: List<LazyListItem>,
) : NodeSpec
