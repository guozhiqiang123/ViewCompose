package com.viewcompose.renderer.node

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.renderer.node.spec.NodeSpec

data class VNode(
    val type: NodeType,
    val key: Any? = null,
    val spec: NodeSpec,
    val modifier: Modifier = Modifier,
    val children: List<VNode> = emptyList(),
)
