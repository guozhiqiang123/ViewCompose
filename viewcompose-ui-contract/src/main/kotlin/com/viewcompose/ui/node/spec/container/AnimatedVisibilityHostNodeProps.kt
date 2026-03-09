package com.viewcompose.ui.node.spec

data class AnimatedVisibilityHostNodeProps(
    val alpha: Float,
    val widthScale: Float,
    val heightScale: Float,
    val clipToBounds: Boolean,
) : NodeSpec
