package com.gzq.uiframework.renderer.view.tree

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.InsertPatch
import com.gzq.uiframework.renderer.reconcile.ReconcileResult
import com.gzq.uiframework.renderer.reconcile.RemovePatch
import com.gzq.uiframework.renderer.reconcile.RenderPatch
import com.gzq.uiframework.renderer.reconcile.ReusePatch

internal object ViewTreePatchPipeline {
    data class ExecutionResult(
        val mountedNodes: List<MountedNode>,
        val stats: RenderStats,
    )

    private data class PatchApplicationResult(
        val mountedNode: MountedNode,
        val stats: RenderStats,
    )

    fun execute(
        container: ViewGroup,
        reconcileResult: ReconcileResult<MountedNode>,
        defaultRippleColor: Int,
        warningTag: String,
        emittedModifierWarnings: MutableSet<String>,
        renderChildren: (ViewGroup, List<MountedNode>, List<VNode>) -> RenderTreeResult,
    ): ExecutionResult {
        var stats = RenderStats()
        val nextMounted = mutableListOf<MountedNode>()
        reconcileResult.patches.forEach { patch ->
            val patchResult = applyPatch(
                container = container,
                patch = patch,
                defaultRippleColor = defaultRippleColor,
                warningTag = warningTag,
                emittedModifierWarnings = emittedModifierWarnings,
                renderChildren = renderChildren,
            )
            stats = stats.mergeWith(patchResult.stats)
            nextMounted += patchResult.mountedNode
        }
        reconcileResult.removals.forEach { removal ->
            applyRemoval(
                container = container,
                removal = removal,
            )
            stats = stats.withRemoval()
        }
        return ExecutionResult(
            mountedNodes = nextMounted,
            stats = stats,
        )
    }

    private fun applyPatch(
        container: ViewGroup,
        patch: RenderPatch<MountedNode>,
        defaultRippleColor: Int,
        warningTag: String,
        emittedModifierWarnings: MutableSet<String>,
        renderChildren: (ViewGroup, List<MountedNode>, List<VNode>) -> RenderTreeResult,
    ): PatchApplicationResult {
        return when (patch) {
            is InsertPatch -> {
                val mountedNode = mountNode(
                    context = container.context,
                    node = patch.nextVNode,
                    defaultRippleColor = defaultRippleColor,
                    renderChildren = renderChildren,
                )
                container.addView(
                    mountedNode.view,
                    patch.targetIndex.coerceAtMost(container.childCount),
                    ViewLayoutParamsFactory.createLayoutParams(
                        parent = container,
                        node = patch.nextVNode,
                        warningTag = warningTag,
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
                    NodeBindingPlan.Rebind -> bindView(
                        view = mountedNode.view,
                        node = patch.nextVNode,
                        defaultRippleColor = defaultRippleColor,
                    )

                    NodeBindingPlan.Skip -> Unit
                    is NodeBindingPlan.Patch -> NodeViewBinderRegistry.applyPatch(
                        view = mountedNode.view,
                        patch = bindingPlan.patch,
                    )
                }
                mountedNode.view.layoutParams = ViewLayoutParamsFactory.createLayoutParams(
                    parent = container,
                    node = patch.nextVNode,
                    warningTag = warningTag,
                    emittedModifierWarnings = emittedModifierWarnings,
                )
                val childResult = reconcileChildren(
                    view = mountedNode.view,
                    previousChildren = mountedNode.children,
                    node = patch.nextVNode,
                    renderChildren = renderChildren,
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
                        nodeType = patch.nextVNode.type,
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
        renderChildren: (ViewGroup, List<MountedNode>, List<VNode>) -> RenderTreeResult,
    ): RenderTreeResult {
        val viewGroup = view as? ViewGroup ?: return RenderTreeResult(
            mountedNodes = emptyList(),
            reconcileResult = ReconcileResult(
                patches = emptyList(),
                removals = emptyList(),
            ),
            stats = RenderStats(),
        )
        return renderChildren(
            viewGroup,
            previousChildren,
            node.children,
        )
    }

    private fun mountNode(
        context: Context,
        node: VNode,
        defaultRippleColor: Int,
        renderChildren: (ViewGroup, List<MountedNode>, List<VNode>) -> RenderTreeResult,
    ): MountedNode {
        val view = ViewNodeFactory.createView(
            context = context,
            node = node,
            createAndroidView = readViewFactory(node),
        )
        ViewModifierApplier.cacheOriginalBackground(view)
        ViewModifierApplier.cacheOriginalForeground(view)
        bindView(
            view = view,
            node = node,
            defaultRippleColor = defaultRippleColor,
        )
        val children = if (view is ViewGroup) {
            renderChildren(
                view,
                emptyList(),
                node.children,
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

    private fun bindView(
        view: View,
        node: VNode,
        defaultRippleColor: Int,
    ) {
        ViewModifierApplier.bindView(
            view = view,
            node = node,
            defaultRippleColor = defaultRippleColor,
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
