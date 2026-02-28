package com.gzq.uiframework.renderer.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.VNode

object ViewTreeRenderer {
    fun renderInto(container: ViewGroup, nodes: List<VNode>) {
        container.removeAllViews()
        nodes.forEach { node ->
            container.addView(
                createView(container.context, node),
                createLayoutParams(container, node),
            )
        }
    }

    private fun createView(context: Context, node: VNode): View {
        val view = when (node.type) {
            NodeType.Text -> TextView(context).apply {
                text = node.props.values[PropKeys.TEXT] as? CharSequence
            }

            NodeType.Button -> TextView(context).apply {
                text = node.props.values[PropKeys.TEXT] as? CharSequence
            }

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

        if (view is ViewGroup) {
            node.children.forEach { child ->
                view.addView(
                    createView(context, child),
                    createLayoutParams(view, child),
                )
            }
        }
        return view
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
}
