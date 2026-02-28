package com.gzq.uiframework.renderer.node

sealed interface NodeType {
    data object Text : NodeType
    data object TextField : NodeType
    data object Checkbox : NodeType
    data object Switch : NodeType
    data object RadioButton : NodeType
    data object Slider : NodeType
    data object LinearProgressIndicator : NodeType
    data object CircularProgressIndicator : NodeType
    data object Button : NodeType
    data object IconButton : NodeType
    data object Row : NodeType
    data object Column : NodeType
    data object Box : NodeType
    data object Surface : NodeType
    data object Spacer : NodeType
    data object Divider : NodeType
    data object Image : NodeType
    data object AndroidView : NodeType
    data object LazyColumn : NodeType
    data object TabPager : NodeType
    data object SegmentedControl : NodeType
}
