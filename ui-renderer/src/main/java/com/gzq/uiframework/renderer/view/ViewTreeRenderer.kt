package com.gzq.uiframework.renderer.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.VNode

object ViewTreeRenderer {
    fun renderInto(
        container: ViewGroup,
        previous: List<MountedNode>,
        nodes: List<VNode>,
    ): List<MountedNode> {
        val nextMounted = mutableListOf<MountedNode>()
        nodes.forEachIndexed { index, node ->
            val previousNode = previous.getOrNull(index)
            if (previousNode != null && canReuse(previousNode.vnode, node)) {
                bindView(previousNode.view, node)
                previousNode.view.layoutParams = createLayoutParams(container, node)
                previousNode.children = reconcileChildren(previousNode.view, previousNode.children, node)
                previousNode.vnode = node
                nextMounted += previousNode
            } else {
                val mountedNode = mountNode(container.context, node)
                if (previousNode == null) {
                    container.addView(
                        mountedNode.view,
                        createLayoutParams(container, node),
                    )
                } else {
                    container.removeViewAt(index)
                    container.addView(
                        mountedNode.view,
                        index,
                        createLayoutParams(container, node),
                    )
                }
                nextMounted += mountedNode
            }
        }

        for (index in previous.lastIndex downTo nodes.size) {
            container.removeViewAt(index)
        }

        return nextMounted
    }

    private fun reconcileChildren(
        view: View,
        previousChildren: List<MountedNode>,
        node: VNode,
    ): List<MountedNode> {
        val viewGroup = view as? ViewGroup ?: return emptyList()
        return renderInto(
            container = viewGroup,
            previous = previousChildren,
            nodes = node.children,
        )
    }

    private fun mountNode(context: Context, node: VNode): MountedNode {
        val view = when (node.type) {
            NodeType.Text -> TextView(context)
            NodeType.Button -> Button(context)
            NodeType.Row -> LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            NodeType.Column -> LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }

            NodeType.Box -> FrameLayout(context)
            NodeType.Image -> View(context)
            NodeType.AndroidView -> View(context)
            NodeType.LazyColumn -> FrameLayout(context)
        }

        bindView(view, node)
        val children = if (view is ViewGroup) {
            renderInto(
                container = view,
                previous = emptyList(),
                nodes = node.children,
            )
        } else {
            emptyList()
        }
        return MountedNode(
            vnode = node,
            view = view,
            children = children,
        )
    }

    private fun bindView(view: View, node: VNode) {
        when (node.type) {
            NodeType.Text -> {
                (view as TextView).text = node.props.values[PropKeys.TEXT] as? CharSequence
            }

            NodeType.Button -> {
                (view as Button).apply {
                    text = node.props.values[PropKeys.TEXT] as? CharSequence
                    setOnClickListener {
                        readOnClick(node)?.invoke()
                    }
                }
            }

            NodeType.Row -> (view as LinearLayout).orientation = LinearLayout.HORIZONTAL
            NodeType.Column -> (view as LinearLayout).orientation = LinearLayout.VERTICAL
            NodeType.Box,
            NodeType.Image,
            NodeType.AndroidView,
            NodeType.LazyColumn,
            -> Unit
        }
    }

    private fun canReuse(previous: VNode, next: VNode): Boolean {
        if (previous.type != next.type) {
            return false
        }
        return previous.key == next.key
    }

    private fun createLayoutParams(parent: ViewGroup, node: VNode): ViewGroup.LayoutParams {
        val width = if (node.type == NodeType.Text || node.type == NodeType.Button) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        return when (parent) {
            is LinearLayout -> LinearLayout.LayoutParams(width, height)
            is FrameLayout -> FrameLayout.LayoutParams(width, height)
            else -> ViewGroup.LayoutParams(width, height)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnClick(node: VNode): (() -> Unit)? {
        return node.props.values[PropKeys.ON_CLICK] as? (() -> Unit)
    }
}
