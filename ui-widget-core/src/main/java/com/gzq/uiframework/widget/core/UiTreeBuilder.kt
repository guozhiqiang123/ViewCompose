package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode

@UiDslMarker
open class UiTreeBuilder {
    private val children = mutableListOf<VNode>()

    fun emit(
        type: NodeType,
        key: Any? = null,
        props: Props = Props.Empty,
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
            modifier = modifier,
            children = nestedChildren,
        )
    }

    internal fun emitResolved(
        type: NodeType,
        key: Any? = null,
        props: Props = Props.Empty,
        modifier: Modifier = Modifier,
        children: List<VNode> = emptyList(),
    ) {
        this.children += VNode(
            type = type,
            key = key,
            props = props,
            modifier = modifier,
            children = children,
        )
    }

    internal fun build(): List<VNode> = children.toList()
}

fun buildVNodeTree(content: UiTreeBuilder.() -> Unit): List<VNode> {
    return UiTreeBuilder().apply(content).build()
}
