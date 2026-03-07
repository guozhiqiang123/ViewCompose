package com.viewcompose.widget.core

import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.Props
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.NodeSpec

@UiDslMarker
open class UiTreeBuilder {
    private val children = mutableListOf<VNode>()

    fun emit(
        type: NodeType,
        key: Any? = null,
        props: Props = Props.Empty,
        spec: NodeSpec? = null,
        modifier: Modifier = Modifier,
        content: (UiTreeBuilder.() -> Unit)? = null,
    ) {
        val nestedChildren = if (content == null) {
            emptyList()
        } else {
            UiTreeBuilder().apply(content).build()
        }
        emitResolved(
            type = type,
            key = key,
            props = props,
            spec = spec,
            modifier = modifier,
            children = nestedChildren,
        )
    }

    internal fun emitResolved(
        type: NodeType,
        key: Any? = null,
        props: Props = Props.Empty,
        spec: NodeSpec? = null,
        modifier: Modifier = Modifier,
        children: List<VNode> = emptyList(),
    ) {
        this.children += VNode(
            type = type,
            key = key,
            props = props,
            spec = spec,
            modifier = modifier,
            children = children,
        )
    }

    internal fun build(): List<VNode> = children.toList()
}

fun buildVNodeTree(content: UiTreeBuilder.() -> Unit): List<VNode> {
    return UiTreeBuilder().apply(content).build()
}
