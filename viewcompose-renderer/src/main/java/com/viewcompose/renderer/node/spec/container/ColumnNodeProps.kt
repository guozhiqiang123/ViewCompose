package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.layout.HorizontalAlignment
import com.viewcompose.renderer.layout.MainAxisArrangement

data class ColumnNodeProps(
    val spacing: Int,
    val arrangement: MainAxisArrangement,
    val horizontalAlignment: HorizontalAlignment,
    val rippleColor: Int? = null,
) : NodeSpec
