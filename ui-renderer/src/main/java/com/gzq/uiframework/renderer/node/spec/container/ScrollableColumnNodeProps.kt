package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement

data class ScrollableColumnNodeProps(
    val spacing: Int,
    val arrangement: MainAxisArrangement,
    val horizontalAlignment: HorizontalAlignment,
) : NodeSpec
