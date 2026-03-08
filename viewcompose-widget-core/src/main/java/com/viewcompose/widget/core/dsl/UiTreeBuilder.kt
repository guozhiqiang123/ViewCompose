package com.viewcompose.widget.core

import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.NodeSpec

@UiDslMarker
open class UiTreeBuilder {
    private val children = mutableListOf<VNode>()

    fun emit(
        type: NodeType,
        key: Any? = null,
        spec: NodeSpec,
        modifier: Modifier = Modifier,
        content: (UiTreeBuilder.() -> Unit)? = null,
    ) {
        val composer = ComposerContext.currentComposer()
        if (composer == null) {
            val nestedChildren = if (content == null) {
                emptyList()
            } else {
                UiTreeBuilder().apply(content).build()
            }
            emitResolved(
                type = type,
                key = key,
                spec = spec,
                modifier = modifier,
                children = nestedChildren,
            )
            return
        }
        val parentSnapshot = LocalContext.snapshot()
        val node = composer.runGroup(
            signature = EmitGroupSignature(
                type = type,
                key = key,
                hasContent = content != null,
            ),
        ) { scope ->
            val restoreSnapshot = (scope.localSnapshotOrNull() as? LocalSnapshot) ?: parentSnapshot
            var nextNode: VNode? = null
            LocalContext.withSnapshot(restoreSnapshot) {
                val nestedChildren = if (content == null) {
                    emptyList()
                } else {
                    UiTreeBuilder().apply(content).build()
                }
                nextNode = VNode(
                    type = type,
                    key = key,
                    spec = spec,
                    modifier = modifier,
                    children = nestedChildren,
                )
                scope.updateLocalSnapshot(LocalContext.snapshot())
            }
            checkNotNull(nextNode)
        }
        children += node
    }

    internal fun emitResolved(
        type: NodeType,
        key: Any? = null,
        spec: NodeSpec,
        modifier: Modifier = Modifier,
        children: List<VNode> = emptyList(),
    ) {
        this.children += VNode(
            type = type,
            key = key,
            spec = spec,
            modifier = modifier,
            children = children,
        )
    }

    internal fun build(): List<VNode> = children.toList()

    private data class EmitGroupSignature(
        val type: NodeType,
        val key: Any?,
        val hasContent: Boolean,
    )
}

fun buildVNodeTree(content: UiTreeBuilder.() -> Unit): List<VNode> {
    return UiTreeBuilder().apply(content).build()
}
