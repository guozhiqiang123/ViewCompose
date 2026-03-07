package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment

data class ScrollableRowNodeProps(
    val spacing: Int,
    val arrangement: MainAxisArrangement,
    val verticalAlignment: VerticalAlignment,
) : NodeSpec
