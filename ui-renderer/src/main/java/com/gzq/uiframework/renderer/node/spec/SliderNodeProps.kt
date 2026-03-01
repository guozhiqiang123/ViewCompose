package com.gzq.uiframework.renderer.node.spec

data class SliderNodeProps(
    val min: Int,
    val max: Int,
    val value: Int,
    val enabled: Boolean,
    val tintColor: Int,
    val onValueChange: ((Int) -> Unit)?,
) : NodeSpec
