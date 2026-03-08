package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.layout.BoxAlignment

data class BoxNodeProps(
    val contentAlignment: BoxAlignment,
    val rippleColor: Int? = null,
) : NodeSpec
