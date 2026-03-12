package com.viewcompose.ui.node.spec

import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.MainAxisArrangement

data class ScrollableColumnNodeProps(
    val spacing: Int,
    val arrangement: MainAxisArrangement,
    val horizontalAlignment: HorizontalAlignment,
    val focusFollowKeyboard: Boolean = false,
) : NodeSpec
