package com.viewcompose.ui.node.spec

import com.viewcompose.ui.graphics.DrawBlock

data class CanvasNodeProps(
    val onDraw: DrawBlock,
) : NodeSpec
