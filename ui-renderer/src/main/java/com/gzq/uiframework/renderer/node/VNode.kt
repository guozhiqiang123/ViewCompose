package com.gzq.uiframework.renderer.node

import com.gzq.uiframework.renderer.modifier.Modifier

data class VNode(
    val type: NodeType,
    val key: Any? = null,
    val props: Props = Props.Empty,
    val modifier: Modifier = Modifier.Empty,
    val children: List<VNode> = emptyList(),
)
