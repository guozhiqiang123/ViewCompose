package com.viewcompose.widget.core

import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.node.LazyListItem
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.collection.TabRowTab
import com.viewcompose.renderer.node.spec.HorizontalPagerNodeProps
import com.viewcompose.renderer.node.spec.LazyColumnNodeProps
import com.viewcompose.renderer.node.spec.LazyRowNodeProps
import com.viewcompose.renderer.node.spec.LazyVerticalGridNodeProps
import com.viewcompose.renderer.node.spec.NodeSpec
import com.viewcompose.renderer.node.spec.TabRowNodeProps
import com.viewcompose.renderer.node.spec.VerticalPagerNodeProps

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
            inputs = listOf(
                spec,
                modifier,
                closureSensitiveSpecToken(spec),
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

    private fun closureSensitiveSpecToken(spec: NodeSpec): Any? {
        return when (spec) {
            is LazyColumnNodeProps -> spec.items.sessionIdentityRefs()
            is LazyRowNodeProps -> spec.items.sessionIdentityRefs()
            is LazyVerticalGridNodeProps -> spec.items.sessionIdentityRefs()
            is HorizontalPagerNodeProps -> spec.pages.sessionIdentityRefs()
            is VerticalPagerNodeProps -> spec.pages.sessionIdentityRefs()
            is TabRowNodeProps -> spec.tabs.tabSessionIdentityRefs()
            else -> null
        }
    }

    private fun List<TabRowTab>.tabSessionIdentityRefs(): List<SessionIdentityRefs> {
        return map { tab -> tab.item.toSessionIdentityRefs() }
    }

    private fun List<LazyListItem>.sessionIdentityRefs(): List<SessionIdentityRefs> {
        return map { item -> item.toSessionIdentityRefs() }
    }

    private fun LazyListItem.toSessionIdentityRefs(): SessionIdentityRefs {
        return SessionIdentityRefs(
            sessionFactory = sessionFactory,
            sessionUpdater = sessionUpdater,
        )
    }

    private data class SessionIdentityRefs(
        val sessionFactory: Any,
        val sessionUpdater: Any?,
    )
}

fun buildVNodeTree(content: UiTreeBuilder.() -> Unit): List<VNode> {
    return UiTreeBuilder().apply(content).build()
}
