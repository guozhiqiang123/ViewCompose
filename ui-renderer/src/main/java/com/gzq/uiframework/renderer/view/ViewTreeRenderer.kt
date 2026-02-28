package com.gzq.uiframework.renderer.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.AlphaModifierElement
import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.ClickableModifierElement
import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.modifier.MarginModifierElement
import com.gzq.uiframework.renderer.modifier.PaddingModifierElement
import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.VisibilityModifierElement
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.modifier.WidthModifierElement
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.ChildReconciler
import com.gzq.uiframework.renderer.reconcile.InsertPatch
import com.gzq.uiframework.renderer.reconcile.ReconcileNode
import com.gzq.uiframework.renderer.reconcile.ReconcileResult
import com.gzq.uiframework.renderer.reconcile.RemovePatch
import com.gzq.uiframework.renderer.reconcile.RenderPatch
import com.gzq.uiframework.renderer.reconcile.ReusePatch

object ViewTreeRenderer {
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
        onReconcile: ((ReconcileResult<MountedNode>) -> Unit)? = null,
    ): List<MountedNode> {
        val reconcileResult = ChildReconciler.reconcile(
            previous = previous.map { mountedNode ->
                ReconcileNode(
                    vnode = mountedNode.vnode,
                    payload = mountedNode,
                )
            },
            nodes = nodes,
        )
        onReconcile?.invoke(reconcileResult)
        val nextMounted = mutableListOf<MountedNode>()
        reconcileResult.patches.forEach { patch ->
            nextMounted += applyPatch(
                container = container,
                patch = patch,
            )
        }
        reconcileResult.removals.forEach { removal ->
            applyRemoval(
                container = container,
                removal = removal,
            )
        }
        return nextMounted
    }

    private fun applyPatch(
        container: ViewGroup,
        patch: RenderPatch<MountedNode>,
    ): MountedNode {
        return when (patch) {
            is InsertPatch -> {
                val mountedNode = mountNode(container.context, patch.nextVNode)
                container.addView(
                    mountedNode.view,
                    patch.targetIndex.coerceAtMost(container.childCount),
                    createLayoutParams(container, patch.nextVNode),
                )
                mountedNode
            }

            is ReusePatch -> {
                val mountedNode = patch.payload
                bindView(mountedNode.view, patch.nextVNode)
                mountedNode.view.layoutParams = createLayoutParams(container, patch.nextVNode)
                mountedNode.children = reconcileChildren(
                    view = mountedNode.view,
                    previousChildren = mountedNode.children,
                    node = patch.nextVNode,
                )
                mountedNode.vnode = patch.nextVNode
                moveViewToIndex(
                    container = container,
                    view = mountedNode.view,
                    targetIndex = patch.targetIndex,
                )
                mountedNode
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
            NodeType.Row -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            NodeType.Column -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }

            NodeType.Box -> DeclarativeBoxLayout(context)
            NodeType.Image -> View(context)
            NodeType.AndroidView -> readViewFactory(node)?.invoke(context) ?: View(context)
            NodeType.LazyColumn -> RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = LazyColumnAdapter()
            }
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
        applyModifier(view, node)
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

            NodeType.Row -> {
                (view as DeclarativeLinearLayout).apply {
                    orientation = LinearLayout.HORIZONTAL
                    itemSpacing = readLinearSpacing(node)
                    mainAxisArrangement = readRowArrangement(node)
                    gravity = readRowVerticalAlignment(node).toGravity()
                }
            }

            NodeType.Column -> {
                (view as DeclarativeLinearLayout).apply {
                    orientation = LinearLayout.VERTICAL
                    itemSpacing = readLinearSpacing(node)
                    mainAxisArrangement = readColumnArrangement(node)
                    gravity = readColumnHorizontalAlignment(node).toGravity()
                }
            }
            NodeType.Box -> {
                (view as DeclarativeBoxLayout).contentGravity = readBoxAlignment(node).toGravity()
            }

            NodeType.Image,
            -> Unit

            NodeType.AndroidView -> {
                readViewUpdate(node)?.invoke(view)
            }

            NodeType.LazyColumn -> {
                (view as RecyclerView).let { recyclerView ->
                    val adapter = recyclerView.adapter as? LazyColumnAdapter ?: LazyColumnAdapter().also {
                        recyclerView.adapter = it
                    }
                    adapter.submitItems(readLazyItems(node))
                }
            }
        }
    }

    private fun applyModifier(view: View, node: VNode) {
        val alpha = node.modifier.elements
            .lastOrNull { it is AlphaModifierElement } as? AlphaModifierElement
        val backgroundColor = node.modifier.elements
            .lastOrNull { it is BackgroundColorModifierElement } as? BackgroundColorModifierElement
        val clickable = node.modifier.elements
            .lastOrNull { it is ClickableModifierElement } as? ClickableModifierElement
        val padding = node.modifier.elements.lastOrNull { it is PaddingModifierElement } as? PaddingModifierElement
        val visibility = node.modifier.elements
            .lastOrNull { it is VisibilityModifierElement } as? VisibilityModifierElement
        view.alpha = alpha?.alpha ?: 1f
        view.setBackgroundColor(backgroundColor?.color ?: 0x00000000)
        view.visibility = when (visibility?.visibility ?: Visibility.Visible) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
        view.isClickable = clickable != null
        view.setOnClickListener(
            if (clickable == null) {
                null
            } else {
                View.OnClickListener { clickable.onClick() }
            },
        )
        if (padding == null) {
            view.setPadding(0, 0, 0, 0)
            return
        }
        view.setPadding(
            padding.left,
            padding.top,
            padding.right,
            padding.bottom,
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
        val margin = node.modifier.elements.lastOrNull { it is MarginModifierElement } as? MarginModifierElement
        val size = node.modifier.elements.lastOrNull { it is SizeModifierElement } as? SizeModifierElement
        val widthModifier = node.modifier.elements.lastOrNull { it is WidthModifierElement } as? WidthModifierElement
        val heightModifier = node.modifier.elements.lastOrNull { it is HeightModifierElement } as? HeightModifierElement
        val weight = node.modifier.elements.lastOrNull { it is WeightModifierElement } as? WeightModifierElement
        val defaultWidth = if (node.type == NodeType.Text || node.type == NodeType.Button) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
        val defaultHeight = ViewGroup.LayoutParams.WRAP_CONTENT
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
                }
            }
            is FrameLayout -> FrameLayout.LayoutParams(width, height).applyLayoutParams(margin = margin)
            else -> ViewGroup.MarginLayoutParams(width, height).applyMargin(margin)
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

    @Suppress("UNCHECKED_CAST")
    private fun readOnClick(node: VNode): (() -> Unit)? {
        return node.props.values[PropKeys.ON_CLICK] as? (() -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readViewFactory(node: VNode): ((Context) -> View)? {
        return node.props.values[PropKeys.VIEW_FACTORY] as? ((Context) -> View)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readViewUpdate(node: VNode): ((View) -> Unit)? {
        return node.props.values[PropKeys.VIEW_UPDATE] as? ((View) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readLazyItems(node: VNode): List<LazyListItem> {
        return node.props.values[PropKeys.LAZY_ITEMS] as? List<LazyListItem> ?: emptyList()
    }

    private fun readLinearSpacing(node: VNode): Int {
        return node.props.values[PropKeys.LINEAR_SPACING] as? Int ?: 0
    }

    private fun readBoxAlignment(node: VNode): BoxAlignment {
        return node.props.values[PropKeys.BOX_ALIGNMENT] as? BoxAlignment
            ?: BoxAlignment.TopStart
    }

    private fun readRowVerticalAlignment(node: VNode): VerticalAlignment {
        return node.props.values[PropKeys.ROW_VERTICAL_ALIGNMENT] as? VerticalAlignment
            ?: VerticalAlignment.Top
    }

    private fun readRowArrangement(node: VNode): MainAxisArrangement {
        return node.props.values[PropKeys.ROW_MAIN_AXIS_ARRANGEMENT] as? MainAxisArrangement
            ?: MainAxisArrangement.Start
    }

    private fun readColumnHorizontalAlignment(node: VNode): HorizontalAlignment {
        return node.props.values[PropKeys.COLUMN_HORIZONTAL_ALIGNMENT] as? HorizontalAlignment
            ?: HorizontalAlignment.Start
    }

    private fun readColumnArrangement(node: VNode): MainAxisArrangement {
        return node.props.values[PropKeys.COLUMN_MAIN_AXIS_ARRANGEMENT] as? MainAxisArrangement
            ?: MainAxisArrangement.Start
    }

    private fun disposeMountedNode(
        mountedNode: MountedNode,
    ) {
        mountedNode.children.forEach(::disposeMountedNode)
        (mountedNode.view as? RecyclerView)
            ?.adapter
            ?.let { adapter ->
                (adapter as? LazyColumnAdapter)?.disposeAll()
            }
        mountedNode.children = emptyList()
    }

    private fun VerticalAlignment.toGravity(): Int {
        return when (this) {
            VerticalAlignment.Top -> Gravity.TOP
            VerticalAlignment.Center -> Gravity.CENTER_VERTICAL
            VerticalAlignment.Bottom -> Gravity.BOTTOM
        }
    }

    private fun HorizontalAlignment.toGravity(): Int {
        return when (this) {
            HorizontalAlignment.Start -> Gravity.START
            HorizontalAlignment.Center -> Gravity.CENTER_HORIZONTAL
            HorizontalAlignment.End -> Gravity.END
        }
    }

    private fun BoxAlignment.toGravity(): Int {
        return when (this) {
            BoxAlignment.TopStart -> Gravity.TOP or Gravity.START
            BoxAlignment.TopCenter -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            BoxAlignment.TopEnd -> Gravity.TOP or Gravity.END
            BoxAlignment.CenterStart -> Gravity.CENTER_VERTICAL or Gravity.START
            BoxAlignment.Center -> Gravity.CENTER
            BoxAlignment.CenterEnd -> Gravity.CENTER_VERTICAL or Gravity.END
            BoxAlignment.BottomStart -> Gravity.BOTTOM or Gravity.START
            BoxAlignment.BottomCenter -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            BoxAlignment.BottomEnd -> Gravity.BOTTOM or Gravity.END
        }
    }
}
