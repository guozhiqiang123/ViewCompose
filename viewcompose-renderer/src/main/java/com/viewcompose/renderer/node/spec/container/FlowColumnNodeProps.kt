package com.viewcompose.renderer.node.spec

data class FlowColumnNodeProps(
    val horizontalSpacing: Int,
    val verticalSpacing: Int,
    val maxItemsInEachColumn: Int,
) : NodeSpec
