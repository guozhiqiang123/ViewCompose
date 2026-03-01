package com.gzq.uiframework.renderer.view.tree

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.LayoutParamDefaultsResolver
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.ModifierParentDataValidator
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.AlphaModifierElement
import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.BorderModifierElement
import com.gzq.uiframework.renderer.modifier.BoxAlignModifierElement
import com.gzq.uiframework.renderer.modifier.ClickableModifierElement
import com.gzq.uiframework.renderer.modifier.CornerRadiusModifierElement
import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.modifier.HorizontalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.MarginModifierElement
import com.gzq.uiframework.renderer.modifier.MinHeightModifierElement
import com.gzq.uiframework.renderer.modifier.OffsetModifierElement
import com.gzq.uiframework.renderer.modifier.PaddingModifierElement
import com.gzq.uiframework.renderer.modifier.RippleColorModifierElement
import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.modifier.VerticalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.VisibilityModifierElement
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.modifier.WidthModifierElement
import com.gzq.uiframework.renderer.modifier.ZIndexModifierElement
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
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter
import com.gzq.uiframework.renderer.R

object ViewTreeRenderer {
    private const val DEFAULT_RIPPLE_COLOR: Int = 0x22000000
    private const val WARNING_TAG: String = "UIFramework"
    private val emittedModifierWarnings = mutableSetOf<String>()

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
        val view = ViewNodeFactory.createView(
            context = context,
            node = node,
            createAndroidView = readViewFactory(node),
        )

        cacheOriginalBackground(view)
        cacheOriginalForeground(view)
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
        NodeViewBinderRegistry.bind(view, node)
    }

    private fun applyModifier(view: View, node: VNode) {
        val alpha = node.modifier.elements
            .lastOrNull { it is AlphaModifierElement } as? AlphaModifierElement
        val backgroundColor = node.modifier.elements
            .lastOrNull { it is BackgroundColorModifierElement } as? BackgroundColorModifierElement
        val clickable = node.modifier.elements
            .lastOrNull { it is ClickableModifierElement } as? ClickableModifierElement
        val border = node.modifier.elements
            .lastOrNull { it is BorderModifierElement } as? BorderModifierElement
        val cornerRadius = node.modifier.elements
            .lastOrNull { it is CornerRadiusModifierElement } as? CornerRadiusModifierElement
        val offset = node.modifier.elements
            .lastOrNull { it is OffsetModifierElement } as? OffsetModifierElement
        val padding = node.modifier.elements.lastOrNull { it is PaddingModifierElement } as? PaddingModifierElement
        val minHeight = node.modifier.elements
            .lastOrNull { it is MinHeightModifierElement } as? MinHeightModifierElement
        val rippleColor = node.modifier.elements
            .lastOrNull { it is RippleColorModifierElement } as? RippleColorModifierElement
        val visibility = node.modifier.elements
            .lastOrNull { it is VisibilityModifierElement } as? VisibilityModifierElement
        val zIndex = node.modifier.elements
            .lastOrNull { it is ZIndexModifierElement } as? ZIndexModifierElement
        val resolvedAlpha = alpha?.alpha ?: readNodeAlpha(node) ?: 1f
        val resolvedBackgroundColor = backgroundColor?.color ?: readNodeBackgroundColor(node)
        val resolvedBorderWidth = border?.width ?: readNodeBorderWidth(node) ?: 0
        val resolvedBorderColor = border?.color ?: readNodeBorderColor(node) ?: Color.TRANSPARENT
        val resolvedCornerRadius = cornerRadius?.radius ?: readNodeCornerRadius(node) ?: 0
        val resolvedPadding = padding ?: readNodePadding(node)
        val resolvedMinHeight = minHeight?.minHeight ?: readNodeMinHeight(node) ?: 0
        val resolvedRippleColor = rippleColor?.color ?: readNodeRippleColor(node) ?: DEFAULT_RIPPLE_COLOR
        val textColor = readNodeTextColor(node)
        val textSizeSp = readNodeTextSize(node)
        view.alpha = resolvedAlpha
        if (view is DeclarativeTextFieldLayout) {
            view.visibility = when (visibility?.visibility ?: Visibility.Visible) {
                Visibility.Visible -> View.VISIBLE
                Visibility.Invisible -> View.INVISIBLE
                Visibility.Gone -> View.GONE
            }
            view.translationX = offset?.x ?: 0f
            view.translationY = offset?.y ?: 0f
            view.z = zIndex?.zIndex ?: 0f
            view.isClickable = false
            view.setOnClickListener(null)
            view.minimumHeight = 0
            view.setPadding(0, 0, 0, 0)
            applyTextFieldModifier(
                layout = view,
                backgroundColor = resolvedBackgroundColor,
                borderWidth = resolvedBorderWidth,
                borderColor = resolvedBorderColor,
                cornerRadius = resolvedCornerRadius,
                rippleColor = resolvedRippleColor,
                padding = resolvedPadding,
                minHeight = resolvedMinHeight,
                textColor = textColor,
                textSizeSp = textSizeSp,
            )
            return
        }
        applyBackgroundAndInteraction(
            view = view,
            backgroundColor = resolvedBackgroundColor,
            borderWidth = resolvedBorderWidth,
            borderColor = resolvedBorderColor,
            cornerRadius = resolvedCornerRadius,
            rippleColor = resolvedRippleColor,
            clickable = clickable != null,
        )
        view.visibility = when (visibility?.visibility ?: Visibility.Visible) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
        view.translationX = offset?.x ?: 0f
        view.translationY = offset?.y ?: 0f
        view.z = zIndex?.zIndex ?: 0f
        view.minimumHeight = resolvedMinHeight
        view.isClickable = clickable != null
        view.setOnClickListener(
            if (clickable == null) {
                null
            } else {
                View.OnClickListener { clickable.onClick() }
            },
        )
        if (resolvedPadding == null) {
            view.setPadding(0, 0, 0, 0)
        } else {
            view.setPadding(
                resolvedPadding.left,
                resolvedPadding.top,
                resolvedPadding.right,
                resolvedPadding.bottom,
            )
        }
        if (view is TextView) {
            if (textColor != null) {
                view.setTextColor(textColor)
            }
            if (textSizeSp != null) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
            }
        }
    }

    private fun cacheOriginalBackground(view: View) {
        if (view.getTag(R.id.ui_framework_original_background) != null) {
            return
        }
        view.setTag(
            R.id.ui_framework_original_background,
            cloneDrawable(view.background),
        )
    }

    private fun cacheOriginalForeground(view: View) {
        if (view.getTag(R.id.ui_framework_original_foreground) != null) {
            return
        }
        view.setTag(
            R.id.ui_framework_original_foreground,
            cloneDrawable(view.foreground),
        )
    }

    private fun restoreOriginalBackground(view: View) {
        view.background = cloneDrawable(
            view.getTag(R.id.ui_framework_original_background) as? Drawable,
        )
    }

    private fun restoreOriginalForeground(view: View) {
        view.foreground = cloneDrawable(
            view.getTag(R.id.ui_framework_original_foreground) as? Drawable,
        )
    }

    private fun cloneDrawable(drawable: Drawable?): Drawable? {
        return drawable?.constantState?.newDrawable()?.mutate() ?: drawable?.mutate()
    }

    private fun applyBackgroundAndInteraction(
        view: View,
        backgroundColor: Int?,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: Int,
        rippleColor: Int,
        clickable: Boolean,
    ) {
        val hasCustomShape = backgroundColor != null || cornerRadius > 0 || borderWidth > 0
        if (hasCustomShape) {
            view.background = createBackgroundDrawable(
                backgroundColor = backgroundColor ?: Color.TRANSPARENT,
                borderWidth = borderWidth,
                borderColor = borderColor,
                cornerRadiusPx = cornerRadius,
                rippleColor = rippleColor,
                clickable = clickable,
            )
            view.foreground = null
        } else {
            restoreOriginalBackground(view)
            if (clickable) {
                view.foreground = RippleDrawable(
                    ColorStateList.valueOf(rippleColor),
                    null,
                    null,
                )
            } else {
                restoreOriginalForeground(view)
            }
        }
        applyCornerOutline(view, cornerRadius)
    }

    private fun createBackgroundDrawable(
        backgroundColor: Int,
        borderWidth: Int,
        borderColor: Int,
        cornerRadiusPx: Int,
        rippleColor: Int,
        clickable: Boolean,
    ): Drawable {
        val content = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            if (borderWidth > 0) {
                setStroke(borderWidth, borderColor)
            }
            cornerRadius = cornerRadiusPx.toFloat()
        }
        if (!clickable) {
            return content
        }
        return RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            content,
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                if (borderWidth > 0) {
                    setStroke(borderWidth, borderColor)
                }
                cornerRadius = cornerRadiusPx.toFloat()
            },
        )
    }

    private fun applyCornerOutline(
        view: View,
        cornerRadius: Int,
    ) {
        if (cornerRadius <= 0) {
            view.clipToOutline = false
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
            view.invalidateOutline()
            return
        }
        view.clipToOutline = true
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius.toFloat())
            }
        }
        view.invalidateOutline()
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

    private fun applyTextFieldModifier(
        layout: DeclarativeTextFieldLayout,
        backgroundColor: Int?,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: Int,
        rippleColor: Int,
        padding: PaddingModifierElement?,
        minHeight: Int,
        textColor: Int?,
        textSizeSp: Int?,
    ) {
        applyBackgroundAndInteraction(
            view = layout.fieldContainer,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = cornerRadius,
            rippleColor = rippleColor,
            clickable = false,
        )
        if (padding == null) {
            layout.fieldContainer.setPadding(0, 0, 0, 0)
        } else {
            layout.fieldContainer.setPadding(
                padding.left,
                padding.top,
                padding.right,
                padding.bottom,
            )
        }
        layout.fieldContainer.minimumHeight = minHeight
        textColor?.let(layout.inputView::setTextColor)
        if (textSizeSp != null) {
            layout.inputView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
        }
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
        return node.props[TypedPropKeys.ViewFactory]
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
        return node.props[TypedPropKeys.DividerThickness] ?: 1
    }

    private fun readNodeTextColor(node: VNode): Int? {
        return node.props[TypedPropKeys.TextColor]
    }

    private fun readNodeTextSize(node: VNode): Int? {
        return node.props[TypedPropKeys.TextSizeSp]
    }

    private fun readNodeAlpha(node: VNode): Float? {
        return node.props[TypedPropKeys.StyleAlpha]
    }

    private fun readNodeBackgroundColor(node: VNode): Int? {
        return node.props[TypedPropKeys.StyleBackgroundColor]
    }

    private fun readNodeBorderWidth(node: VNode): Int? {
        return node.props[TypedPropKeys.StyleBorderWidth]
    }

    private fun readNodeBorderColor(node: VNode): Int? {
        return node.props[TypedPropKeys.StyleBorderColor]
    }

    private fun readNodeCornerRadius(node: VNode): Int? {
        return node.props[TypedPropKeys.StyleCornerRadius]
    }

    private fun readNodeRippleColor(node: VNode): Int? {
        return node.props[TypedPropKeys.StyleRippleColor]
    }

    private fun readNodeMinHeight(node: VNode): Int? {
        return node.props[TypedPropKeys.StyleMinHeight]
    }

    private fun readNodePadding(node: VNode): PaddingModifierElement? {
        val left = node.props[TypedPropKeys.StylePaddingLeft] ?: return null
        val top = node.props[TypedPropKeys.StylePaddingTop] ?: return null
        val right = node.props[TypedPropKeys.StylePaddingRight] ?: return null
        val bottom = node.props[TypedPropKeys.StylePaddingBottom] ?: return null
        return PaddingModifierElement(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
        )
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
