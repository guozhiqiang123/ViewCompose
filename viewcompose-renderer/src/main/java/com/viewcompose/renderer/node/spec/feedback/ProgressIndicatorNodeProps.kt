package com.viewcompose.renderer.node.spec

data class ProgressIndicatorNodeProps(
    val enabled: Boolean,
    val progress: Float?,
    val indicatorColor: Int,
    val trackColor: Int,
    val trackThickness: Int,
    val indicatorSize: Int,
) : NodeSpec
