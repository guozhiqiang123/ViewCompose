package com.gzq.uiframework.renderer.view.tree

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.LayoutParamDefaultsResolver
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.ModifierParentDataValidator
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.BoxAlignModifierElement
import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.modifier.HorizontalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.MarginModifierElement
import com.gzq.uiframework.renderer.modifier.PaddingModifierElement
import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.modifier.VerticalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.modifier.WidthModifierElement
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
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter

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
            disposeMountedNode(mountedNode)
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
                    createLayoutParams(container, patch.nextVNode),
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
                mountedNode.view.layoutParams = createLayoutParams(container, patch.nextVNode)
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
        disposeMountedNode(removal.payload)
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

    private fun createLayoutParams(parent: ViewGroup, node: VNode): ViewGroup.LayoutParams {
        emitModifierWarnings(parent, node)
        val boxAlign = node.modifier.elements.lastOrNull { it is BoxAlignModifierElement } as? BoxAlignModifierElement
        val margin = node.modifier.elements.lastOrNull { it is MarginModifierElement } as? MarginModifierElement
        val size = node.modifier.elements.lastOrNull { it is SizeModifierElement } as? SizeModifierElement
        val widthModifier = node.modifier.elements.lastOrNull { it is WidthModifierElement } as? WidthModifierElement
        val heightModifier = node.modifier.elements.lastOrNull { it is HeightModifierElement } as? HeightModifierElement
        val weight = node.modifier.elements.lastOrNull { it is WeightModifierElement } as? WeightModifierElement
        val horizontalAlign = node.modifier.elements
            .lastOrNull { it is HorizontalAlignModifierElement } as? HorizontalAlignModifierElement
        val verticalAlign = node.modifier.elements
            .lastOrNull { it is VerticalAlignModifierElement } as? VerticalAlignModifierElement
        val defaultWidth = if (node.type == NodeType.Divider) {
            defaultDividerWidth(parent, node)
        } else {
            LayoutParamDefaultsResolver.defaultWidth(
                nodeType = node.type,
                parentIsLinearLayout = parent is DeclarativeLinearLayout,
                linearOrientation = (parent as? DeclarativeLinearLayout)?.orientation,
            )
        }
        val defaultHeight = if (node.type == NodeType.Divider) {
            defaultDividerHeight(parent, node)
        } else {
            LayoutParamDefaultsResolver.defaultHeight(
                nodeType = node.type,
                parentIsLinearLayout = parent is DeclarativeLinearLayout,
                linearOrientation = (parent as? DeclarativeLinearLayout)?.orientation,
            )
        }
        val width = widthModifier?.width ?: size?.width ?: defaultWidth
        val height = heightModifier?.height ?: size?.height ?: defaultHeight
        return when (parent) {
            is DeclarativeLinearLayout -> {
                val resolvedWidth = if (
                    weight != null &&
                    parent.orientation == LinearLayout.HORIZONTAL &&
                    widthModifier == null &&
                    size?.width == null
                ) {
                    0
                } else {
                    width
                }
                val resolvedHeight = if (
                    weight != null &&
                    parent.orientation == LinearLayout.VERTICAL &&
                    heightModifier == null &&
                    size?.height == null
                ) {
                    0
                } else {
                    height
                }
                android.widget.LinearLayout.LayoutParams(resolvedWidth, resolvedHeight).applyLayoutParams(
                    margin = margin,
                ) {
                    this.weight = weight?.weight ?: 0f
                    gravity = when (parent.orientation) {
                        LinearLayout.HORIZONTAL -> verticalAlign?.alignment?.toGravity() ?: -1
                        else -> horizontalAlign?.alignment?.toGravity() ?: -1
                    }
                }
            }
            is DeclarativeBoxLayout -> FrameLayout.LayoutParams(width, height).applyLayoutParams(
                margin = margin,
            ) {
                gravity = boxAlign?.alignment?.toGravity() ?: DeclarativeBoxLayout.UNSET_GRAVITY
            }
            is FrameLayout -> FrameLayout.LayoutParams(width, height).applyLayoutParams(margin = margin)
            else -> ViewGroup.MarginLayoutParams(width, height).applyMargin(margin)
        }
    }

    private fun emitModifierWarnings(
        parent: ViewGroup,
        node: VNode,
    ) {
        ModifierParentDataValidator.validate(parent, node).forEach { warning ->
            val key = "${parent::class.java.name}|${node.type}|$warning"
            if (emittedModifierWarnings.add(key)) {
                Log.w(WARNING_TAG, warning)
            }
        }
    }

    private fun <T : ViewGroup.MarginLayoutParams> T.applyLayoutParams(
        margin: MarginModifierElement?,
        block: T.() -> Unit = {},
    ): T {
        applyMargin(margin)
        block()
        return this
    }

    private fun <T : ViewGroup.MarginLayoutParams> T.applyMargin(
        margin: MarginModifierElement?,
    ): T {
        if (margin == null) {
            setMargins(0, 0, 0, 0)
            return this
        }
        setMargins(
            margin.left,
            margin.top,
            margin.right,
            margin.bottom,
        )
        return this
    }

    private fun readViewFactory(node: VNode): ((Context) -> View)? {
        return (node.spec as? com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps)?.factory
            ?: node.props[TypedPropKeys.ViewFactory]
    }

    private fun defaultDividerWidth(parent: ViewGroup, node: VNode): Int {
        val thickness = readDividerThickness(node)
        return if ((parent as? LinearLayout)?.orientation == LinearLayout.HORIZONTAL) {
            thickness
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun defaultDividerHeight(parent: ViewGroup, node: VNode): Int {
        val thickness = readDividerThickness(node)
        return if ((parent as? LinearLayout)?.orientation == LinearLayout.HORIZONTAL) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            thickness
        }
    }

    private fun readDividerThickness(node: VNode): Int {
        return ContainerViewBinder.readDividerSpec(node).thickness
    }

    private fun disposeMountedNode(
        mountedNode: MountedNode,
    ) {
        mountedNode.children.forEach(::disposeMountedNode)
        (mountedNode.view as? DeclarativeTabPagerLayout)?.dispose()
        (mountedNode.view as? RecyclerView)
            ?.adapter
            ?.let { adapter ->
                (adapter as? LazyColumnAdapter)?.disposeAll()
            }
        mountedNode.children = emptyList()
    }

    private fun VerticalAlignment.toGravity(): Int {
        return when (this) {
            VerticalAlignment.Top -> android.view.Gravity.TOP
            VerticalAlignment.Center -> android.view.Gravity.CENTER_VERTICAL
            VerticalAlignment.Bottom -> android.view.Gravity.BOTTOM
        }
    }

    private fun HorizontalAlignment.toGravity(): Int {
        return when (this) {
            HorizontalAlignment.Start -> android.view.Gravity.START
            HorizontalAlignment.Center -> android.view.Gravity.CENTER_HORIZONTAL
            HorizontalAlignment.End -> android.view.Gravity.END
        }
    }

    private fun BoxAlignment.toGravity(): Int {
        return when (this) {
            BoxAlignment.TopStart -> android.view.Gravity.TOP or android.view.Gravity.START
            BoxAlignment.TopCenter -> android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL
            BoxAlignment.TopEnd -> android.view.Gravity.TOP or android.view.Gravity.END
            BoxAlignment.CenterStart -> android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.START
            BoxAlignment.Center -> android.view.Gravity.CENTER
            BoxAlignment.CenterEnd -> android.view.Gravity.CENTER_VERTICAL or android.view.Gravity.END
            BoxAlignment.BottomStart -> android.view.Gravity.BOTTOM or android.view.Gravity.START
            BoxAlignment.BottomCenter -> android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
            BoxAlignment.BottomEnd -> android.view.Gravity.BOTTOM or android.view.Gravity.END
        }
    }

}
