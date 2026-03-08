package com.viewcompose.ui.node.spec

data class PullToRefreshNodeProps(
    val isRefreshing: Boolean,
    val onRefresh: (() -> Unit)?,
    val indicatorColor: Int,
) : NodeSpec
