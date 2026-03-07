package com.gzq.uiframework.renderer.node

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.spec.NodeSpec

data class VNode(
    val type: NodeType,
    val key: Any? = null,
    val props: Props = Props.Empty,
    val spec: NodeSpec? = null,
    val modifier: Modifier = Modifier,
    val children: List<VNode> = emptyList(),
)
