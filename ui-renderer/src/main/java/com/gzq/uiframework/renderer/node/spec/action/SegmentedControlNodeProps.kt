package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.SegmentedControlItem

data class SegmentedControlNodeProps(
    val items: List<SegmentedControlItem>,
    val selectedIndex: Int,
    val onSelectionChange: ((Int) -> Unit)?,
    val enabled: Boolean,
    val backgroundColor: Int,
    val indicatorColor: Int,
    val cornerRadius: Int,
    val textColor: Int,
    val selectedTextColor: Int,
    val rippleColor: Int,
    val textSizeSp: Int,
    val paddingHorizontal: Int,
    val paddingVertical: Int,
) : NodeSpec
