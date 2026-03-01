package com.gzq.uiframework.renderer.view.tree

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.modifier.PaddingModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.ChildReconciler
import com.gzq.uiframework.renderer.reconcile.InsertPatch
import com.gzq.uiframework.renderer.reconcile.ReconcileNode
import com.gzq.uiframework.renderer.reconcile.ReconcileResult
import com.gzq.uiframework.renderer.reconcile.RemovePatch
import com.gzq.uiframework.renderer.reconcile.RenderPatch
import com.gzq.uiframework.renderer.reconcile.ReusePatch

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
        var stats = RenderStats()
        val reconcileResult = ChildReconciler.reconcile(
            previous = previous.map { mountedNode ->
                ReconcileNode(
                    vnode = mountedNode.vnode,
                    payload = mountedNode,
                )
            },
            nodes = nodes,
        )
        val nextMounted = mutableListOf<MountedNode>()
        reconcileResult.patches.forEach { patch ->
            val patchResult = applyPatch(
                container = container,
                patch = patch,
            )
            stats = stats.copy(
                inserts = stats.inserts + patchResult.stats.inserts,
                reuses = stats.reuses + patchResult.stats.reuses,
                removals = stats.removals + patchResult.stats.removals,
                reboundNodes = stats.reboundNodes + patchResult.stats.reboundNodes,
                patchedNodes = stats.patchedNodes + patchResult.stats.patchedNodes,
                skippedBindings = stats.skippedBindings + patchResult.stats.skippedBindings,
            )
            nextMounted += patchResult.mountedNode
        }
        reconcileResult.removals.forEach { removal ->
            applyRemoval(
                container = container,
                removal = removal,
            )
            stats = stats.withRemoval()
        }
        val structure = RenderStructureStats.from(
            nodes = nodes,
            mountedNodes = nextMounted,
        )
        val warnings = if (onReconcile == null) {
            emptyList()
        } else {
            collectRenderWarnings(
                nodes = nodes,
                structure = structure,
                stats = stats,
            )
        }
        return RenderTreeResult(
            mountedNodes = nextMounted,
            reconcileResult = reconcileResult,
            stats = stats,
            structure = structure,
            warnings = warnings,
        ).also { onReconcile?.invoke(it) }
    }

    private data class PatchApplicationResult(
        val mountedNode: MountedNode,
        val stats: RenderStats,
    )

    private fun applyPatch(
        container: ViewGroup,
        patch: RenderPatch<MountedNode>,
    ): PatchApplicationResult {
        return when (patch) {
            is InsertPatch -> {
                val mountedNode = mountNode(container.context, patch.nextVNode)
                container.addView(
                    mountedNode.view,
                    patch.targetIndex.coerceAtMost(container.childCount),
                    ViewLayoutParamsFactory.createLayoutParams(
                        parent = container,
                        node = patch.nextVNode,
                        warningTag = WARNING_TAG,
                        emittedModifierWarnings = emittedModifierWarnings,
                    ),
                )
                PatchApplicationResult(
                    mountedNode = mountedNode,
                    stats = RenderStats(inserts = 1),
                )
            }

            is ReusePatch -> {
                val mountedNode = patch.payload
                val bindingPlan = NodeBindingDiffer.plan(mountedNode.vnode, patch.nextVNode)
                when (bindingPlan) {
                    NodeBindingPlan.Rebind -> bindView(mountedNode.view, patch.nextVNode)
                    NodeBindingPlan.Skip -> Unit
                    is NodeBindingPlan.Patch -> NodeViewBinderRegistry.applyPatch(
                        view = mountedNode.view,
                        patch = bindingPlan.patch,
                    )
                }
                mountedNode.view.layoutParams = ViewLayoutParamsFactory.createLayoutParams(
                    parent = container,
                    node = patch.nextVNode,
                    warningTag = WARNING_TAG,
                    emittedModifierWarnings = emittedModifierWarnings,
                )
                val childResult = reconcileChildren(
                    view = mountedNode.view,
                    previousChildren = mountedNode.children,
                    node = patch.nextVNode,
                )
                mountedNode.children = childResult.mountedNodes
                mountedNode.vnode = patch.nextVNode
                moveViewToIndex(
                    container = container,
                    view = mountedNode.view,
                    targetIndex = patch.targetIndex,
                )
                PatchApplicationResult(
                    mountedNode = mountedNode,
                    stats = childResult.stats.withReuse(
                        result = when (bindingPlan) {
                            NodeBindingPlan.Rebind -> ReuseBindingResult.Rebound
                            NodeBindingPlan.Skip -> ReuseBindingResult.Skipped
                            is NodeBindingPlan.Patch -> ReuseBindingResult.Patched
                        },
                    ),
                )
            }
        }
    }

    private fun applyRemoval(
        container: ViewGroup,
        removal: RemovePatch<MountedNode>,
    ) {
        ViewTreeDisposer.disposeMountedNode(removal.payload)
        container.removeView(removal.payload.view)
    }

    private fun reconcileChildren(
        view: View,
        previousChildren: List<MountedNode>,
        node: VNode,
    ): RenderTreeResult {
        val viewGroup = view as? ViewGroup ?: return RenderTreeResult(
            mountedNodes = emptyList(),
            reconcileResult = ReconcileResult(
                patches = emptyList(),
                removals = emptyList(),
            ),
            stats = RenderStats(),
        )
        return renderInto(
            container = viewGroup,
            previous = previousChildren,
            nodes = node.children,
        )
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

    private fun mountNode(context: Context, node: VNode): MountedNode {
        val view = ViewNodeFactory.createView(
            context = context,
            node = node,
            createAndroidView = readViewFactory(node),
        )

        ViewModifierApplier.cacheOriginalBackground(view)
        ViewModifierApplier.cacheOriginalForeground(view)
        ViewModifierApplier.bindView(
            view = view,
            node = node,
            defaultRippleColor = DEFAULT_RIPPLE_COLOR,
        )
        val children = if (view is ViewGroup) {
            renderInto(
                container = view,
                previous = emptyList(),
                nodes = node.children,
            ).mountedNodes
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
        ViewModifierApplier.bindView(
            view = view,
            node = node,
            defaultRippleColor = DEFAULT_RIPPLE_COLOR,
        )
    }

    private fun moveViewToIndex(
        container: ViewGroup,
        view: View,
        targetIndex: Int,
    ) {
        val currentIndex = container.indexOfChild(view)
        if (currentIndex == -1 || currentIndex == targetIndex) {
            return
        }
        container.removeViewAt(currentIndex)
        container.addView(
            view,
            targetIndex.coerceAtMost(container.childCount),
        )
    }

    private fun readViewFactory(node: VNode): ((Context) -> View)? {
        return (node.spec as? com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps)?.factory
            ?: node.props[TypedPropKeys.ViewFactory]
    }
}
