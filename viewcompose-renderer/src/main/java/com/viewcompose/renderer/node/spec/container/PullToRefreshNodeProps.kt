package com.viewcompose.renderer.node.spec

data class PullToRefreshNodeProps(
    val isRefreshing: Boolean,
    val onRefresh: (() -> Unit)?,
    val indicatorColor: Int,
) : NodeSpec
