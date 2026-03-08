package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.layout.MainAxisArrangement
import com.viewcompose.renderer.layout.VerticalAlignment

data class RowNodeProps(
    val spacing: Int,
    val arrangement: MainAxisArrangement,
    val verticalAlignment: VerticalAlignment,
    val rippleColor: Int? = null,
) : NodeSpec
