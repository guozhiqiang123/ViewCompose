package com.gzq.uiframework.renderer.view.tree

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.modifier.PaddingModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.ChildReconciler
import com.gzq.uiframework.renderer.reconcile.ReconcileNode

object ViewTreeRenderer {
    private const val DEFAULT_RIPPLE_COLOR: Int = 0x22000000
    private const val WARNING_TAG: String = "UIFramework"
    private val emittedModifierWarnings = mutableSetOf<String>()
    private val emittedStructureWarnings = mutableSetOf<String>()

    init {
        NodeViewBinderRegistry.initialize(
            defaultRippleColor = DEFAULT_RIPPLE_COLOR,
        )
    }

    fun disposeMounted(
        container: ViewGroup,
        mountedNodes: List<MountedNode>,
    ) {
        mountedNodes.forEach { mountedNode ->
            ViewTreeDisposer.disposeMountedNode(mountedNode)
            container.removeView(mountedNode.view)
        }
    }

    fun renderInto(
        container: ViewGroup,
        previous: List<MountedNode>,
        nodes: List<VNode>,
        onReconcile: ((RenderTreeResult) -> Unit)? = null,
    ): RenderTreeResult {
        val reconcileResult = ChildReconciler.reconcile(
            previous = previous.map { mountedNode ->
                ReconcileNode(
                    vnode = mountedNode.vnode,
                    payload = mountedNode,
                )
            },
            nodes = nodes,
        )
        val pipelineResult = ViewTreePatchPipeline.execute(
            container = container,
            reconcileResult = reconcileResult,
            defaultRippleColor = DEFAULT_RIPPLE_COLOR,
            warningTag = WARNING_TAG,
            emittedModifierWarnings = emittedModifierWarnings,
            renderChildren = { childContainer, childPrevious, childNodes ->
                renderInto(
                    container = childContainer,
                    previous = childPrevious,
                    nodes = childNodes,
                )
            },
        )
        val structure = RenderStructureStats.from(
            nodes = nodes,
            mountedNodes = pipelineResult.mountedNodes,
        )
        val warnings = if (onReconcile == null) {
            emptyList()
        } else {
            collectRenderWarnings(
                nodes = nodes,
                structure = structure,
                stats = pipelineResult.stats,
            )
        }
        return RenderTreeResult(
            mountedNodes = pipelineResult.mountedNodes,
            reconcileResult = reconcileResult,
            stats = pipelineResult.stats,
            structure = structure,
            warnings = warnings,
        ).also { onReconcile?.invoke(it) }
    }

    private fun collectRenderWarnings(
        nodes: List<VNode>,
        structure: RenderStructureStats,
        stats: RenderStats,
    ): List<String> {
        val warnings = RenderWarningCollector.collect(
            nodes = nodes,
            structure = structure,
            stats = stats,
        )
        warnings.forEach { warning ->
            val key = "structure|$warning"
            if (emittedStructureWarnings.add(key)) {
                Log.w(WARNING_TAG, warning)
            }
        }
        return warnings
    }

    private fun readViewFactory(node: VNode): ((Context) -> android.view.View)? {
        return (node.spec as? com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps)?.factory
            ?: node.props[com.gzq.uiframework.renderer.node.TypedPropKeys.ViewFactory]
    }
}
