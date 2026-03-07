package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.layout.HorizontalAlignment
import com.viewcompose.renderer.layout.MainAxisArrangement

data class ScrollableColumnNodeProps(
    val spacing: Int,
    val arrangement: MainAxisArrangement,
    val horizontalAlignment: HorizontalAlignment,
) : NodeSpec
