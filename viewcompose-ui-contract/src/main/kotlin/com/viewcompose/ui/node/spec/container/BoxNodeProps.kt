package com.viewcompose.ui.node.spec

import com.viewcompose.ui.layout.BoxAlignment

data class BoxNodeProps(
    val contentAlignment: BoxAlignment,
    val rippleColor: Int? = null,
) : NodeSpec
