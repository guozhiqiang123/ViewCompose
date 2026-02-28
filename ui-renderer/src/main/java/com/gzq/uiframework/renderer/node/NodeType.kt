package com.gzq.uiframework.renderer.node

sealed interface NodeType {
    data object Text : NodeType
    data object Button : NodeType
    data object Row : NodeType
    data object Column : NodeType
    data object Box : NodeType
    data object Spacer : NodeType
    data object Image : NodeType
    data object AndroidView : NodeType
    data object LazyColumn : NodeType
}
