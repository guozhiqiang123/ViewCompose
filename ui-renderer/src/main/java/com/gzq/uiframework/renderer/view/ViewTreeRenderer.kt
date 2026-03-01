package com.gzq.uiframework.renderer.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.LayoutParamDefaultsResolver
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.ModifierCompatibilityInspector
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
import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.modifier.VerticalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.VisibilityModifierElement
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.modifier.WidthModifierElement
import com.gzq.uiframework.renderer.modifier.ZIndexModifierElement
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.RemoteImageLoader
import com.gzq.uiframework.renderer.node.RemoteImageRequest
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.ChildReconciler
import com.gzq.uiframework.renderer.reconcile.InsertPatch
import com.gzq.uiframework.renderer.reconcile.ReconcileNode
import com.gzq.uiframework.renderer.reconcile.ReconcileResult
import com.gzq.uiframework.renderer.reconcile.RemovePatch
import com.gzq.uiframework.renderer.reconcile.RenderPatch
import com.gzq.uiframework.renderer.reconcile.ReusePatch
import com.gzq.uiframework.renderer.R
import kotlin.math.roundToInt

object ViewTreeRenderer {
    private const val DEFAULT_RIPPLE_COLOR: Int = 0x22000000
    private const val WARNING_TAG: String = "UIFramework"
    private val emittedModifierWarnings = mutableSetOf<String>()

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
            NodeType.TextField -> DeclarativeTextFieldLayout(context)
            NodeType.Checkbox -> CheckBox(context)
            NodeType.Switch -> Switch(context)
            NodeType.RadioButton -> RadioButton(context)
            NodeType.Slider -> SeekBar(context)
            NodeType.LinearProgressIndicator -> LinearProgressIndicator(context)
            NodeType.CircularProgressIndicator -> CircularProgressIndicator(context)
            NodeType.Button -> Button(context)
            NodeType.IconButton -> ImageButton(context)
            NodeType.Row -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            NodeType.Column -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }

            NodeType.Box -> DeclarativeBoxLayout(context)
            NodeType.Surface -> DeclarativeBoxLayout(context)
            NodeType.Spacer -> View(context)
            NodeType.Divider -> View(context)
            NodeType.Image -> ImageView(context)
            NodeType.AndroidView -> readViewFactory(node)?.invoke(context) ?: View(context)
            NodeType.LazyColumn -> RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = LazyColumnAdapter()
            }
            NodeType.TabPager -> DeclarativeTabPagerLayout(context)
            NodeType.SegmentedControl -> DeclarativeSegmentedControlLayout(context)
        }

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
        when (node.type) {
            NodeType.Text -> {
                (view as TextView).apply {
                    text = node.props.values[PropKeys.TEXT] as? CharSequence
                    maxLines = readTextMaxLines(node)
                    ellipsize = when (readTextOverflow(node)) {
                        TextOverflow.Clip -> null
                        TextOverflow.Ellipsis -> TextUtils.TruncateAt.END
                    }
                    gravity = readTextAlign(node).toTextGravity()
                }
            }

            NodeType.TextField -> {
                bindTextField(view as DeclarativeTextFieldLayout, node)
            }

            NodeType.Checkbox -> {
                bindCompoundButton(view as CheckBox, node)
            }

            NodeType.Switch -> {
                bindCompoundButton(view as Switch, node)
            }

            NodeType.RadioButton -> {
                bindCompoundButton(view as RadioButton, node)
            }

            NodeType.Slider -> {
                bindSlider(view as SeekBar, node)
            }

            NodeType.LinearProgressIndicator -> {
                bindLinearProgressIndicator(view as LinearProgressIndicator, node)
            }

            NodeType.CircularProgressIndicator -> {
                bindCircularProgressIndicator(view as CircularProgressIndicator, node)
            }

            NodeType.Button -> {
                (view as Button).apply {
                    text = node.props.values[PropKeys.TEXT] as? CharSequence
                    isEnabled = readEnabled(node)
                    isAllCaps = false
                    setSingleLine(false)
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                    gravity = Gravity.CENTER
                    minimumWidth = 0
                    minWidth = 0
                    compoundDrawablePadding = readButtonIconSpacing(node)
                    setCompoundDrawablesRelative(
                        resolveButtonIconDrawable(
                            context = context,
                            source = readButtonLeadingIcon(node),
                            tint = readButtonContentColor(node),
                            size = readButtonIconSize(node),
                        ),
                        null,
                        resolveButtonIconDrawable(
                            context = context,
                            source = readButtonTrailingIcon(node),
                            tint = readButtonContentColor(node),
                            size = readButtonIconSize(node),
                        ),
                        null,
                    )
                    setOnClickListener {
                        if (readEnabled(node)) {
                            readOnClick(node)?.invoke()
                        }
                    }
                }
            }

            NodeType.IconButton -> {
                bindIconButton(view as ImageButton, node)
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

            NodeType.Surface -> {
                (view as DeclarativeBoxLayout).contentGravity = readBoxAlignment(node).toGravity()
            }

            NodeType.Spacer -> Unit

            NodeType.Divider -> {
                view.setBackgroundColor(readDividerColor(node))
            }

            NodeType.Image -> {
                bindImage(view as ImageView, node)
            }

            NodeType.AndroidView -> {
                readViewUpdate(node)?.invoke(view)
            }

            NodeType.LazyColumn -> {
                (view as RecyclerView).let { recyclerView ->
                    val adapter = recyclerView.adapter as? LazyColumnAdapter ?: LazyColumnAdapter().also {
                        recyclerView.adapter = it
                    }
                    applyLazyListPadding(
                        recyclerView = recyclerView,
                        padding = readLazyContentPadding(node),
                    )
                    applyLazyListSpacing(
                        recyclerView = recyclerView,
                        spacing = readLazySpacing(node),
                    )
                    adapter.submitItems(readLazyItems(node))
                }
            }

            NodeType.TabPager -> {
                (view as DeclarativeTabPagerLayout).bind(
                    pages = readTabPages(node),
                    selectedTabIndex = readSelectedTabIndex(node),
                    onTabSelected = readOnTabSelected(node),
                    backgroundColor = readTabBackgroundColor(node),
                    indicatorColor = readTabIndicatorColor(node),
                    cornerRadius = readTabCornerRadius(node),
                    indicatorHeight = readTabIndicatorHeight(node),
                    tabPaddingHorizontal = readTabPaddingHorizontal(node),
                    tabPaddingVertical = readTabPaddingVertical(node),
                    selectedTextColor = readTabSelectedTextColor(node),
                    unselectedTextColor = readTabUnselectedTextColor(node),
                    rippleColor = readTabRippleColor(node),
                )
            }

            NodeType.SegmentedControl -> {
                (view as DeclarativeSegmentedControlLayout).bind(
                    items = readSegmentItems(node),
                    selectedIndex = readSegmentSelectedIndex(node),
                    onSelectionChange = readOnSegmentSelected(node),
                    enabled = readEnabled(node),
                    backgroundColor = readSegmentBackgroundColor(node),
                    indicatorColor = readSegmentIndicatorColor(node),
                    cornerRadius = readSegmentCornerRadius(node),
                    textColor = readSegmentTextColor(node),
                    selectedTextColor = readSegmentSelectedTextColor(node),
                    rippleColor = readSegmentRippleColor(node),
                    textSizeSp = readSegmentTextSize(node),
                    horizontalPadding = readSegmentPaddingHorizontal(node),
                    verticalPadding = readSegmentPaddingVertical(node),
                )
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
        val legacyTextColor = node.modifier.elements
            .lastOrNull { it is TextColorModifierElement } as? TextColorModifierElement
        val legacyTextSize = node.modifier.elements
            .lastOrNull { it is TextSizeModifierElement } as? TextSizeModifierElement
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
        val textColor = readNodeTextColor(node) ?: legacyTextColor?.color
        val textSizeSp = readNodeTextSize(node) ?: legacyTextSize?.sizeSp
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

    private fun bindTextField(view: DeclarativeTextFieldLayout, node: VNode) {
        val input = view.inputView
        val value = readFieldValue(node)
        if (input.text?.toString() != value) {
            input.setText(value)
            input.setSelection(value.length)
        }
        view.setLabel(
            text = readFieldLabel(node),
            color = readFieldLabelColor(node),
            textSizeSp = readFieldLabelTextSize(node),
        )
        view.setSupportingText(
            text = readFieldSupportingText(node),
            color = readFieldSupportingTextColor(node),
            textSizeSp = readFieldSupportingTextSize(node),
        )
        input.hint = readFieldPlaceholder(node)
        input.isEnabled = readEnabled(node)
        input.isSingleLine = readFieldSingleLine(node)
        input.minLines = if (readFieldSingleLine(node)) 1 else readFieldMinLines(node)
        input.maxLines = if (readFieldSingleLine(node)) 1 else readFieldMaxLines(node)
        input.inputType = resolveInputType(
            type = readFieldType(node),
            singleLine = readFieldSingleLine(node),
        )
        input.imeOptions = readFieldImeAction(node).toEditorAction()
        input.setHintTextColor(readFieldHintColor(node))
        applyReadOnly(
            view = input,
            readOnly = readFieldReadOnly(node),
        )
        bindTextWatcher(input, node)
    }

    private fun applyLazyListPadding(
        recyclerView: RecyclerView,
        padding: Int,
    ) {
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = padding == 0
    }

    private fun applyLazyListSpacing(
        recyclerView: RecyclerView,
        spacing: Int,
    ) {
        val existing = recyclerView.getTag(R.id.ui_framework_lazy_spacing_decoration) as? LazyItemSpacingDecoration
        if (existing != null) {
            existing.updateSpacing(spacing)
            recyclerView.invalidateItemDecorations()
            return
        }
        val decoration = LazyItemSpacingDecoration(spacing)
        recyclerView.setTag(R.id.ui_framework_lazy_spacing_decoration, decoration)
        recyclerView.addItemDecoration(decoration)
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

    private fun applyReadOnly(
        view: EditText,
        readOnly: Boolean,
    ) {
        view.isFocusable = !readOnly
        view.isFocusableInTouchMode = !readOnly
        view.isCursorVisible = !readOnly
        view.isLongClickable = !readOnly
        view.setTextIsSelectable(readOnly)
    }

    private fun bindTextWatcher(view: EditText, node: VNode) {
        val previousWatcher = view.getTag(R.id.ui_framework_text_watcher) as? TextWatcher
        if (previousWatcher != null) {
            view.removeTextChangedListener(previousWatcher)
        }
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) = Unit

            override fun afterTextChanged(s: Editable?) {
                val nextValue = s?.toString().orEmpty()
                if (nextValue != readFieldValue(node)) {
                    readOnValueChange(node)?.invoke(nextValue)
                }
            }
        }
        view.addTextChangedListener(watcher)
        view.setTag(R.id.ui_framework_text_watcher, watcher)
    }

    private fun bindCompoundButton(view: CompoundButton, node: VNode) {
        view.setOnCheckedChangeListener(null)
        view.text = node.props.values[PropKeys.TEXT] as? CharSequence
        view.isEnabled = readEnabled(node)
        view.isChecked = readChecked(node)
        val tint = ColorStateList.valueOf(readControlColor(node))
        view.buttonTintList = tint
        if (view is Switch) {
            view.thumbTintList = tint
            view.trackTintList = tint
        }
        view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != readChecked(node)) {
                readOnCheckedChange(node)?.invoke(isChecked)
            }
        }
    }

    private fun bindSlider(view: SeekBar, node: VNode) {
        val min = readMinValue(node)
        val max = readMaxValue(node)
        val value = readSliderValue(node).coerceIn(min, max)
        val listener = view.getTag(R.id.ui_framework_seek_listener) as? SeekBar.OnSeekBarChangeListener
        if (listener != null) {
            view.setOnSeekBarChangeListener(null)
        }
        view.max = (max - min).coerceAtLeast(0)
        view.progress = value - min
        view.isEnabled = readEnabled(node)
        val tint = ColorStateList.valueOf(readControlColor(node))
        view.progressTintList = tint
        view.thumbTintList = tint
        val nextListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val resolvedValue = min + progress
                if (fromUser && resolvedValue != readSliderValue(node)) {
                    readOnSliderValueChange(node)?.invoke(resolvedValue)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
        view.setOnSeekBarChangeListener(nextListener)
        view.setTag(R.id.ui_framework_seek_listener, nextListener)
    }

    private fun bindLinearProgressIndicator(
        view: LinearProgressIndicator,
        node: VNode,
    ) {
        bindProgressIndicator(view, node)
    }

    private fun bindCircularProgressIndicator(
        view: CircularProgressIndicator,
        node: VNode,
    ) {
        bindProgressIndicator(view, node)
        view.indicatorSize = readProgressIndicatorSize(node)
    }

    private fun bindImage(
        view: ImageView,
        node: VNode,
    ) {
        view.contentDescription = readImageContentDescription(node)
        view.scaleType = readImageContentScale(node).toScaleType()
        val tint = readImageTint(node)
        view.imageTintList = tint?.let(ColorStateList::valueOf)
        when (val source = readImageSource(node)) {
            is ImageSource.Resource -> {
                view.setImageResource(source.resId)
            }

            is ImageSource.Remote -> {
                val normalizedUrl = source.url?.takeIf { it.isNotBlank() }
                if (normalizedUrl == null) {
                    bindImagePlaceholder(
                        view = view,
                        source = readImageFallback(node),
                    )
                    return
                }
                val loader = readRemoteImageLoader(node)
                if (loader == null) {
                    bindImagePlaceholder(
                        view = view,
                        source = readImageError(node) ?: readImagePlaceholder(node) ?: readImageFallback(node),
                    )
                    return
                }
                bindImagePlaceholder(
                    view = view,
                    source = readImagePlaceholder(node),
                )
                loader.load(
                    imageView = view,
                    request = RemoteImageRequest(
                        url = normalizedUrl,
                        placeholderResId = readImagePlaceholder(node)?.resId,
                        errorResId = readImageError(node)?.resId,
                        fallbackResId = readImageFallback(node)?.resId,
                    ),
                )
            }

            null -> {
                view.setImageDrawable(null)
            }
        }
    }

    private fun bindImagePlaceholder(
        view: ImageView,
        source: ImageSource.Resource?,
    ) {
        if (source == null) {
            view.setImageDrawable(null)
            return
        }
        view.setImageDrawable(
            ContextCompat.getDrawable(view.context, source.resId),
        )
    }

    private fun bindIconButton(
        view: ImageButton,
        node: VNode,
    ) {
        bindImage(view, node)
        view.isEnabled = readEnabled(node)
        view.scaleType = ImageView.ScaleType.CENTER_INSIDE
        view.adjustViewBounds = false
    }

    private fun resolveButtonIconDrawable(
        context: Context,
        source: ImageSource.Resource?,
        tint: Int,
        size: Int,
    ): Drawable? {
        val drawable = source?.let { ContextCompat.getDrawable(context, it.resId) }?.mutate() ?: return null
        drawable.setTint(tint)
        drawable.setBounds(0, 0, size, size)
        return drawable
    }

    private fun bindProgressIndicator(
        view: ProgressBar,
        node: VNode,
    ) {
        val progress = readProgressFraction(node)
        val indicatorColor = ColorStateList.valueOf(readProgressIndicatorColor(node))
        val trackColor = readProgressTrackColor(node)

        view.isEnabled = readEnabled(node)
        view.isIndeterminate = progress == null
        view.progressTintList = indicatorColor
        view.indeterminateTintList = indicatorColor

        if (view is BaseProgressIndicator<*>) {
            view.trackColor = trackColor
            view.trackThickness = readProgressTrackThickness(node)
            view.setIndicatorColor(readProgressIndicatorColor(node))
        } else {
            view.progressBackgroundTintList = ColorStateList.valueOf(trackColor)
        }

        if (progress != null) {
            view.max = 10_000
            view.progress = (progress.coerceIn(0f, 1f) * 10_000f).roundToInt()
        }
    }

    private fun resolveInputType(type: TextFieldType, singleLine: Boolean): Int {
        val baseType = when (type) {
            TextFieldType.Text -> InputType.TYPE_CLASS_TEXT
            TextFieldType.Password -> {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            TextFieldType.Email -> {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            TextFieldType.Number -> InputType.TYPE_CLASS_NUMBER
        }
        return if (singleLine) {
            baseType
        } else {
            baseType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
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
        ModifierCompatibilityInspector.warnings(node).forEach { warning ->
            val key = "compat|${node.type}|$warning"
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

    @Suppress("UNCHECKED_CAST")
    private fun readOnClick(node: VNode): (() -> Unit)? {
        return node.props.values[PropKeys.ON_CLICK] as? (() -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnValueChange(node: VNode): ((String) -> Unit)? {
        return node.props.values[PropKeys.ON_VALUE_CHANGE] as? ((String) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnCheckedChange(node: VNode): ((Boolean) -> Unit)? {
        return node.props.values[PropKeys.ON_CHECKED_CHANGE] as? ((Boolean) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnSliderValueChange(node: VNode): ((Int) -> Unit)? {
        return node.props.values[PropKeys.ON_SLIDER_VALUE_CHANGE] as? ((Int) -> Unit)
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

    @Suppress("UNCHECKED_CAST")
    private fun readTabPages(node: VNode): List<TabPage> {
        return node.props.values[PropKeys.TAB_PAGES] as? List<TabPage> ?: emptyList()
    }

    private fun readSelectedTabIndex(node: VNode): Int {
        return node.props.values[PropKeys.SELECTED_TAB_INDEX] as? Int ?: 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnTabSelected(node: VNode): ((Int) -> Unit)? {
        return node.props.values[PropKeys.ON_TAB_SELECTED] as? ((Int) -> Unit)
    }

    private fun readTabBackgroundColor(node: VNode): Int {
        return node.props.values[PropKeys.TAB_BACKGROUND_COLOR] as? Int ?: 0
    }

    private fun readTabIndicatorColor(node: VNode): Int {
        return node.props.values[PropKeys.TAB_INDICATOR_COLOR] as? Int ?: 0
    }

    private fun readTabCornerRadius(node: VNode): Int {
        return node.props.values[PropKeys.TAB_CORNER_RADIUS] as? Int ?: 0
    }

    private fun readTabIndicatorHeight(node: VNode): Int {
        return node.props.values[PropKeys.TAB_INDICATOR_HEIGHT] as? Int ?: 0
    }

    private fun readTabPaddingHorizontal(node: VNode): Int {
        return node.props.values[PropKeys.TAB_CONTENT_PADDING_HORIZONTAL] as? Int ?: 0
    }

    private fun readTabPaddingVertical(node: VNode): Int {
        return node.props.values[PropKeys.TAB_CONTENT_PADDING_VERTICAL] as? Int ?: 0
    }

    private fun readTabSelectedTextColor(node: VNode): Int {
        return node.props.values[PropKeys.TAB_SELECTED_TEXT_COLOR] as? Int ?: 0
    }

    private fun readTabUnselectedTextColor(node: VNode): Int {
        return node.props.values[PropKeys.TAB_UNSELECTED_TEXT_COLOR] as? Int ?: 0
    }

    private fun readTabRippleColor(node: VNode): Int {
        return node.props.values[PropKeys.TAB_RIPPLE_COLOR] as? Int ?: DEFAULT_RIPPLE_COLOR
    }

    @Suppress("UNCHECKED_CAST")
    private fun readSegmentItems(node: VNode): List<SegmentedControlItem> {
        return node.props.values[PropKeys.SEGMENT_ITEMS] as? List<SegmentedControlItem> ?: emptyList()
    }

    private fun readSegmentSelectedIndex(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_SELECTED_INDEX] as? Int ?: 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnSegmentSelected(node: VNode): ((Int) -> Unit)? {
        return node.props.values[PropKeys.ON_SEGMENT_SELECTED] as? ((Int) -> Unit)
    }

    private fun readSegmentBackgroundColor(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_BACKGROUND_COLOR] as? Int ?: Color.TRANSPARENT
    }

    private fun readSegmentIndicatorColor(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_INDICATOR_COLOR] as? Int ?: Color.TRANSPARENT
    }

    private fun readSegmentCornerRadius(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_CORNER_RADIUS] as? Int ?: 0
    }

    private fun readSegmentTextColor(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_TEXT_COLOR] as? Int ?: Color.BLACK
    }

    private fun readSegmentSelectedTextColor(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_SELECTED_TEXT_COLOR] as? Int ?: Color.WHITE
    }

    private fun readSegmentRippleColor(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_RIPPLE_COLOR] as? Int ?: DEFAULT_RIPPLE_COLOR
    }

    private fun readSegmentTextSize(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_TEXT_SIZE_SP] as? Int ?: 14
    }

    private fun readSegmentPaddingHorizontal(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_CONTENT_PADDING_HORIZONTAL] as? Int ?: 0
    }

    private fun readSegmentPaddingVertical(node: VNode): Int {
        return node.props.values[PropKeys.SEGMENT_CONTENT_PADDING_VERTICAL] as? Int ?: 0
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

    private fun readDividerColor(node: VNode): Int {
        return node.props.values[PropKeys.DIVIDER_COLOR] as? Int ?: 0xFF000000.toInt()
    }

    private fun readDividerThickness(node: VNode): Int {
        return node.props.values[PropKeys.DIVIDER_THICKNESS] as? Int ?: 1
    }

    private fun readChecked(node: VNode): Boolean {
        return node.props.values[PropKeys.CHECKED] as? Boolean ?: false
    }

    private fun readTextMaxLines(node: VNode): Int {
        return node.props.values[PropKeys.TEXT_MAX_LINES] as? Int ?: Int.MAX_VALUE
    }

    private fun readTextOverflow(node: VNode): TextOverflow {
        return node.props.values[PropKeys.TEXT_OVERFLOW] as? TextOverflow ?: TextOverflow.Clip
    }

    private fun readTextAlign(node: VNode): TextAlign {
        return node.props.values[PropKeys.TEXT_ALIGN] as? TextAlign ?: TextAlign.Start
    }

    private fun readEnabled(node: VNode): Boolean {
        return node.props.values[PropKeys.ENABLED] as? Boolean ?: true
    }

    private fun readControlColor(node: VNode): Int {
        return node.props.values[PropKeys.CONTROL_COLOR] as? Int ?: 0xFF000000.toInt()
    }

    private fun readFieldValue(node: VNode): String {
        return node.props.values[PropKeys.VALUE] as? String ?: ""
    }

    private fun readFieldLabel(node: VNode): String {
        return node.props.values[PropKeys.LABEL] as? String ?: ""
    }

    private fun readFieldHint(node: VNode): String {
        return node.props.values[PropKeys.HINT] as? String ?: ""
    }

    private fun readFieldPlaceholder(node: VNode): String {
        return node.props.values[PropKeys.PLACEHOLDER] as? String ?: readFieldHint(node)
    }

    private fun readFieldSupportingText(node: VNode): String {
        return node.props.values[PropKeys.SUPPORTING_TEXT] as? String ?: ""
    }

    private fun readFieldSingleLine(node: VNode): Boolean {
        return node.props.values[PropKeys.SINGLE_LINE] as? Boolean ?: true
    }

    private fun readFieldType(node: VNode): TextFieldType {
        return node.props.values[PropKeys.TEXT_FIELD_TYPE] as? TextFieldType ?: TextFieldType.Text
    }

    private fun readFieldHintColor(node: VNode): Int {
        return node.props.values[PropKeys.HINT_TEXT_COLOR] as? Int ?: 0xFF888888.toInt()
    }

    private fun readFieldReadOnly(node: VNode): Boolean {
        return node.props.values[PropKeys.READ_ONLY] as? Boolean ?: false
    }

    private fun readFieldMaxLines(node: VNode): Int {
        return node.props.values[PropKeys.MAX_LINES] as? Int ?: Int.MAX_VALUE
    }

    private fun readFieldMinLines(node: VNode): Int {
        return node.props.values[PropKeys.MIN_LINES] as? Int ?: 1
    }

    private fun readFieldImeAction(node: VNode): TextFieldImeAction {
        return node.props.values[PropKeys.IME_ACTION] as? TextFieldImeAction ?: TextFieldImeAction.Default
    }

    private fun readFieldLabelColor(node: VNode): Int {
        return node.props.values[PropKeys.LABEL_TEXT_COLOR] as? Int ?: readFieldHintColor(node)
    }

    private fun readFieldSupportingTextColor(node: VNode): Int {
        return node.props.values[PropKeys.SUPPORTING_TEXT_COLOR] as? Int ?: readFieldHintColor(node)
    }

    private fun readFieldLabelTextSize(node: VNode): Int {
        return node.props.values[PropKeys.LABEL_TEXT_SIZE_SP] as? Int ?: 12
    }

    private fun readFieldSupportingTextSize(node: VNode): Int {
        return node.props.values[PropKeys.SUPPORTING_TEXT_SIZE_SP] as? Int ?: 12
    }

    private fun readSliderValue(node: VNode): Int {
        return node.props.values[PropKeys.SLIDER_VALUE] as? Int ?: 0
    }

    private fun readProgressFraction(node: VNode): Float? {
        return node.props.values[PropKeys.PROGRESS_FRACTION] as? Float
    }

    private fun readLazyContentPadding(node: VNode): Int {
        return node.props.values[PropKeys.LAZY_CONTENT_PADDING] as? Int ?: 0
    }

    private fun readLazySpacing(node: VNode): Int {
        return node.props.values[PropKeys.LAZY_SPACING] as? Int ?: 0
    }

    private fun readImageSource(node: VNode): ImageSource? {
        return node.props.values[PropKeys.IMAGE_SOURCE] as? ImageSource
    }

    private fun readImageContentScale(node: VNode): ImageContentScale {
        return node.props.values[PropKeys.IMAGE_CONTENT_SCALE] as? ImageContentScale
            ?: ImageContentScale.Fit
    }

    private fun readImageContentDescription(node: VNode): String? {
        return node.props.values[PropKeys.IMAGE_CONTENT_DESCRIPTION] as? String
    }

    private fun readImageTint(node: VNode): Int? {
        return node.props.values[PropKeys.IMAGE_TINT] as? Int
    }

    private fun readRemoteImageLoader(node: VNode): RemoteImageLoader? {
        return node.props.values[PropKeys.IMAGE_REMOTE_LOADER] as? RemoteImageLoader
    }

    private fun readButtonLeadingIcon(node: VNode): ImageSource.Resource? {
        return node.props.values[PropKeys.BUTTON_LEADING_ICON] as? ImageSource.Resource
    }

    private fun readButtonTrailingIcon(node: VNode): ImageSource.Resource? {
        return node.props.values[PropKeys.BUTTON_TRAILING_ICON] as? ImageSource.Resource
    }

    private fun readButtonIconSize(node: VNode): Int {
        return node.props.values[PropKeys.BUTTON_ICON_SIZE] as? Int ?: 18
    }

    private fun readButtonIconSpacing(node: VNode): Int {
        return node.props.values[PropKeys.BUTTON_ICON_SPACING] as? Int ?: 8
    }

    private fun readButtonContentColor(node: VNode): Int {
        return readNodeTextColor(node) ?: readLegacyModifierTextColor(node) ?: Color.BLACK
    }

    private fun readImagePlaceholder(node: VNode): ImageSource.Resource? {
        return node.props.values[PropKeys.IMAGE_PLACEHOLDER] as? ImageSource.Resource
    }

    private fun readImageError(node: VNode): ImageSource.Resource? {
        return node.props.values[PropKeys.IMAGE_ERROR] as? ImageSource.Resource
    }

    private fun readImageFallback(node: VNode): ImageSource.Resource? {
        return node.props.values[PropKeys.IMAGE_FALLBACK] as? ImageSource.Resource
    }

    private fun readProgressIndicatorColor(node: VNode): Int {
        return node.props.values[PropKeys.PROGRESS_INDICATOR_COLOR] as? Int ?: 0xFF000000.toInt()
    }

    private fun readNodeTextColor(node: VNode): Int? {
        return node.props.values[PropKeys.TEXT_COLOR] as? Int
    }

    private fun readNodeTextSize(node: VNode): Int? {
        return node.props.values[PropKeys.TEXT_SIZE_SP] as? Int
    }

    private fun readNodeAlpha(node: VNode): Float? {
        return node.props.values[PropKeys.STYLE_ALPHA] as? Float
    }

    private fun readNodeBackgroundColor(node: VNode): Int? {
        return node.props.values[PropKeys.STYLE_BACKGROUND_COLOR] as? Int
    }

    private fun readNodeBorderWidth(node: VNode): Int? {
        return node.props.values[PropKeys.STYLE_BORDER_WIDTH] as? Int
    }

    private fun readNodeBorderColor(node: VNode): Int? {
        return node.props.values[PropKeys.STYLE_BORDER_COLOR] as? Int
    }

    private fun readNodeCornerRadius(node: VNode): Int? {
        return node.props.values[PropKeys.STYLE_CORNER_RADIUS] as? Int
    }

    private fun readNodeRippleColor(node: VNode): Int? {
        return node.props.values[PropKeys.STYLE_RIPPLE_COLOR] as? Int
    }

    private fun readNodeMinHeight(node: VNode): Int? {
        return node.props.values[PropKeys.STYLE_MIN_HEIGHT] as? Int
    }

    private fun readNodePadding(node: VNode): PaddingModifierElement? {
        val left = node.props.values[PropKeys.STYLE_PADDING_LEFT] as? Int ?: return null
        val top = node.props.values[PropKeys.STYLE_PADDING_TOP] as? Int ?: return null
        val right = node.props.values[PropKeys.STYLE_PADDING_RIGHT] as? Int ?: return null
        val bottom = node.props.values[PropKeys.STYLE_PADDING_BOTTOM] as? Int ?: return null
        return PaddingModifierElement(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
        )
    }

    private fun readLegacyModifierTextColor(node: VNode): Int? {
        return (node.modifier.elements
            .lastOrNull { it is TextColorModifierElement } as? TextColorModifierElement)
            ?.color
    }

    private fun TextAlign.toTextGravity(): Int {
        return when (this) {
            TextAlign.Start -> Gravity.START or Gravity.CENTER_VERTICAL
            TextAlign.Center -> Gravity.CENTER
            TextAlign.End -> Gravity.END or Gravity.CENTER_VERTICAL
        }
    }

    private fun TextFieldImeAction.toEditorAction(): Int {
        return when (this) {
            TextFieldImeAction.Default -> EditorInfo.IME_ACTION_UNSPECIFIED
            TextFieldImeAction.Next -> EditorInfo.IME_ACTION_NEXT
            TextFieldImeAction.Done -> EditorInfo.IME_ACTION_DONE
            TextFieldImeAction.Go -> EditorInfo.IME_ACTION_GO
            TextFieldImeAction.Search -> EditorInfo.IME_ACTION_SEARCH
            TextFieldImeAction.Send -> EditorInfo.IME_ACTION_SEND
        }
    }

    private fun readProgressTrackColor(node: VNode): Int {
        return node.props.values[PropKeys.PROGRESS_TRACK_COLOR] as? Int ?: 0x33000000
    }

    private fun readProgressTrackThickness(node: VNode): Int {
        return node.props.values[PropKeys.PROGRESS_TRACK_THICKNESS] as? Int ?: 4
    }

    private fun readProgressIndicatorSize(node: VNode): Int {
        return node.props.values[PropKeys.PROGRESS_INDICATOR_SIZE] as? Int ?: 32
    }

    private fun readMinValue(node: VNode): Int {
        return node.props.values[PropKeys.MIN_VALUE] as? Int ?: 0
    }

    private fun readMaxValue(node: VNode): Int {
        return node.props.values[PropKeys.MAX_VALUE] as? Int ?: 100
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

    private fun ImageContentScale.toScaleType(): ImageView.ScaleType {
        return when (this) {
            ImageContentScale.Fit -> ImageView.ScaleType.FIT_CENTER
            ImageContentScale.Crop -> ImageView.ScaleType.CENTER_CROP
            ImageContentScale.FillBounds -> ImageView.ScaleType.FIT_XY
            ImageContentScale.Inside -> ImageView.ScaleType.CENTER_INSIDE
        }
    }
}
