package com.viewcompose.renderer.view.tree

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.viewcompose.renderer.R
import com.viewcompose.renderer.modifier.CornerRadiusModifierElement
import com.viewcompose.renderer.modifier.ImeInsetsPaddingModifierElement
import com.viewcompose.renderer.modifier.NativeViewElement
import com.viewcompose.renderer.modifier.PaddingModifierElement
import com.viewcompose.renderer.modifier.ResolvedModifiers
import com.viewcompose.renderer.modifier.SystemBarsInsetsPaddingModifierElement
import com.viewcompose.renderer.modifier.Visibility
import com.viewcompose.renderer.modifier.lazyContainerFocusPolicy
import com.viewcompose.renderer.modifier.lazyContainerReusePolicy
import com.viewcompose.renderer.modifier.resolve
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.TypedPropKeys
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.ButtonNodeProps
import com.viewcompose.renderer.node.spec.IconButtonNodeProps
import com.viewcompose.renderer.node.spec.TextFieldNodeProps
import com.viewcompose.renderer.node.spec.TextNodeProps
import com.viewcompose.renderer.node.spec.ToggleNodeProps
import com.viewcompose.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.viewcompose.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.viewcompose.renderer.view.container.DeclarativeScrollableColumnLayout
import com.viewcompose.renderer.view.container.DeclarativeTextFieldLayout
import com.viewcompose.renderer.view.container.DeclarativeVerticalPagerLayout
import com.viewcompose.renderer.view.lazy.LazyFocusFollowLayoutMonitor
import com.viewcompose.renderer.view.lazy.ScrollableFocusFollowLayoutMonitor
import com.viewcompose.renderer.view.lazy.FrameworkRecyclerViewDefaults

internal object ViewModifierApplier {
    private const val FOCUS_FOLLOW_TAG = "UIFocusFollow"

    fun bindView(
        view: View,
        node: VNode,
        defaultRippleColor: Int,
        resolved: ResolvedModifiers = node.modifier.resolve(),
    ) {
        applyModifier(
            view = view,
            node = node,
            defaultRippleColor = defaultRippleColor,
            resolved = resolved,
        )
        NodeViewBinderRegistry.bind(view, node)
        applyNativeViewConfigs(view, node)
    }

    fun cacheOriginalBackground(view: View) {
        if (view.getTag(R.id.ui_framework_original_background) != null) {
            return
        }
        view.setTag(
            R.id.ui_framework_original_background,
            cloneDrawable(view.background),
        )
    }

    fun cacheOriginalForeground(view: View) {
        if (view.getTag(R.id.ui_framework_original_foreground) != null) {
            return
        }
        view.setTag(
            R.id.ui_framework_original_foreground,
            cloneDrawable(view.foreground),
        )
    }

    fun applyStylePatch(
        view: View,
        backgroundColor: Int,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: Int,
        rippleColor: Int,
        clickable: Boolean,
    ) {
        val corners = if (cornerRadius > 0) {
            CornerRadiusModifierElement(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        } else {
            null
        }
        applyBackgroundAndInteraction(
            view = view,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = corners,
            rippleColor = rippleColor,
            clickable = clickable,
        )
    }

    fun applyModifier(
        view: View,
        node: VNode,
        defaultRippleColor: Int,
        resolved: ResolvedModifiers = node.modifier.resolve(),
    ) {
        applyRecyclerContainerDefaults(
            view = view,
            node = node,
        )
        val resolvedAlpha = resolved.alpha?.alpha ?: readNodeAlpha(node) ?: 1f
        val resolvedBackgroundColor = resolved.backgroundColor?.color ?: readNodeBackgroundColor(node)
        val resolvedBorderWidth = resolved.border?.width ?: readNodeBorderWidth(node) ?: 0
        val resolvedBorderColor = resolved.border?.color ?: readNodeBorderColor(node) ?: Color.TRANSPARENT
        val resolvedCornerRadius = resolved.cornerRadius
            ?: readNodeCornerRadius(node)?.let {
                CornerRadiusModifierElement(it, it, it, it)
            }
        val resolvedPadding = resolved.padding ?: readNodePadding(node)
        val resolvedMinHeight = resolved.minHeight?.minHeight ?: readNodeMinHeight(node) ?: 0
        val resolvedMinWidth = resolved.minWidth?.minWidth ?: 0
        val resolvedRippleColor = resolved.rippleColor?.color ?: readNodeRippleColor(node) ?: defaultRippleColor
        val textColor = readNodeTextColor(node)
        val textSizeSp = readNodeTextSize(node)
        val anchorId = readAnchorId(node)
        view.alpha = resolvedAlpha
        if (view is DeclarativeTextFieldLayout) {
            applyAnchorId(view, anchorId)
            applyTestTag(view, resolved.testTag?.tag)
            view.visibility = when (resolved.visibility?.visibility ?: Visibility.Visible) {
                Visibility.Visible -> View.VISIBLE
                Visibility.Invisible -> View.INVISIBLE
                Visibility.Gone -> View.GONE
            }
            view.translationX = resolved.offset?.x ?: 0f
            view.translationY = resolved.offset?.y ?: 0f
            view.z = resolved.zIndex?.zIndex ?: 0f
            view.elevation = resolved.elevation?.elevation?.toFloat() ?: 0f
            view.setOnClickListener(null)
            view.isClickable = false
            view.isFocusable = false
            view.isFocusableInTouchMode = false
            view.minimumHeight = 0
            view.minimumWidth = 0
            view.contentDescription = resolved.contentDescription?.contentDescription
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
            applyWindowInsetsPadding(
                view = view,
                systemBarsModifier = resolved.systemBarsInsetsPadding,
                imeModifier = resolved.imeInsetsPadding,
            )
            return
        }
        val isClickable = resolved.clickable != null || readNodeClickable(node)
        applyBackgroundAndInteraction(
            view = view,
            backgroundColor = resolvedBackgroundColor,
            borderWidth = resolvedBorderWidth,
            borderColor = resolvedBorderColor,
            cornerRadius = resolvedCornerRadius,
            rippleColor = resolvedRippleColor,
            clickable = isClickable,
            forceClip = resolved.clip?.clip ?: false,
        )
        applyAnchorId(view, anchorId)
        applyTestTag(view, resolved.testTag?.tag)
        view.visibility = when (resolved.visibility?.visibility ?: Visibility.Visible) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
        view.translationX = resolved.offset?.x ?: 0f
        view.translationY = resolved.offset?.y ?: 0f
        view.z = resolved.zIndex?.zIndex ?: 0f
        view.elevation = resolved.elevation?.elevation?.toFloat() ?: 0f
        view.minimumHeight = resolvedMinHeight
        view.minimumWidth = resolvedMinWidth
        view.contentDescription = resolved.contentDescription?.contentDescription
        val clickListener = resolved.clickable?.let { clickableElement ->
            View.OnClickListener { clickableElement.onClick() }
        }
        val hasClickListener = clickListener != null
        val keepIntrinsicInteraction = shouldKeepIntrinsicInteraction(node.type)
        view.setOnClickListener(clickListener)
        view.isClickable = hasClickListener || keepIntrinsicInteraction
        view.isFocusable = hasClickListener || keepIntrinsicInteraction
        view.isFocusableInTouchMode = false
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
        applyWindowInsetsPadding(
            view = view,
            systemBarsModifier = resolved.systemBarsInsetsPadding,
            imeModifier = resolved.imeInsetsPadding,
        )
        if (view is TextView) {
            if (textColor != null) {
                view.setTextColor(textColor)
            }
            if (textSizeSp != null) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
            }
        }
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

    private fun applyAnchorId(
        view: View,
        anchorId: String?,
    ) {
        view.setTag(R.id.ui_framework_anchor_id, anchorId)
    }

    private fun applyTestTag(
        view: View,
        testTag: String?,
    ) {
        view.setTag(R.id.ui_framework_test_tag, testTag)
    }

    private fun applyBackgroundAndInteraction(
        view: View,
        backgroundColor: Int?,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: CornerRadiusModifierElement?,
        rippleColor: Int,
        clickable: Boolean,
        forceClip: Boolean = false,
    ) {
        val hasCorner = cornerRadius != null &&
            (cornerRadius.topStart > 0 || cornerRadius.topEnd > 0 ||
                cornerRadius.bottomEnd > 0 || cornerRadius.bottomStart > 0)
        val hasCustomShape = backgroundColor != null || hasCorner || borderWidth > 0
        if (hasCustomShape) {
            view.background = createBackgroundDrawable(
                backgroundColor = backgroundColor ?: Color.TRANSPARENT,
                borderWidth = borderWidth,
                borderColor = borderColor,
                cornerRadius = cornerRadius,
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
        applyCornerOutline(view, cornerRadius, forceClip)
    }

    private fun createBackgroundDrawable(
        backgroundColor: Int,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: CornerRadiusModifierElement?,
        rippleColor: Int,
        clickable: Boolean,
    ): Drawable {
        val content = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            if (borderWidth > 0) {
                setStroke(borderWidth, borderColor)
            }
            applyCornerRadiusToDrawable(this, cornerRadius)
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
                applyCornerRadiusToDrawable(this, cornerRadius)
            },
        )
    }

    private fun applyCornerRadiusToDrawable(
        drawable: GradientDrawable,
        cornerRadius: CornerRadiusModifierElement?,
    ) {
        if (cornerRadius == null) return
        if (cornerRadius.isUniform) {
            drawable.cornerRadius = cornerRadius.topStart.toFloat()
        } else {
            val tl = cornerRadius.topStart.toFloat()
            val tr = cornerRadius.topEnd.toFloat()
            val br = cornerRadius.bottomEnd.toFloat()
            val bl = cornerRadius.bottomStart.toFloat()
            drawable.cornerRadii = floatArrayOf(tl, tl, tr, tr, br, br, bl, bl)
        }
    }

    private fun applyCornerOutline(
        view: View,
        cornerRadius: CornerRadiusModifierElement?,
        forceClip: Boolean = false,
    ) {
        val hasCorner = cornerRadius != null &&
            (cornerRadius.topStart > 0 || cornerRadius.topEnd > 0 ||
                cornerRadius.bottomEnd > 0 || cornerRadius.bottomStart > 0)
        if (!hasCorner && !forceClip) {
            view.clipToOutline = false
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
            view.invalidateOutline()
            return
        }
        if (cornerRadius != null && hasCorner) {
            if (cornerRadius.isUniform) {
                val r = cornerRadius.topStart.toFloat()
                view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, r)
                    }
                }
            } else {
                val r = maxOf(
                    cornerRadius.topStart,
                    cornerRadius.topEnd,
                    cornerRadius.bottomEnd,
                    cornerRadius.bottomStart,
                ).toFloat()
                view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, r)
                    }
                }
            }
        } else {
            view.outlineProvider = ViewOutlineProvider.BACKGROUND
        }
        // Apply rounded outline for shadow, but only clip content when clip() is explicitly requested.
        view.clipToOutline = forceClip
        view.invalidateOutline()
    }

    private fun applyTextFieldModifier(
        layout: DeclarativeTextFieldLayout,
        backgroundColor: Int?,
        borderWidth: Int,
        borderColor: Int,
        cornerRadius: CornerRadiusModifierElement?,
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

    private fun shouldKeepIntrinsicInteraction(type: NodeType): Boolean {
        return type == NodeType.Checkbox ||
            type == NodeType.Switch ||
            type == NodeType.RadioButton ||
            type == NodeType.Slider
    }

    private fun readNodeTextColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.textColor
        is TextNodeProps -> spec.textColor
        is TextFieldNodeProps -> spec.textColor
        is ToggleNodeProps -> spec.textColor
        else -> node.props[TypedPropKeys.TextColor]
    }

    private fun readNodeTextSize(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.textSizeSp
        is TextNodeProps -> spec.textSizeSp
        is TextFieldNodeProps -> spec.textSizeSp
        is ToggleNodeProps -> spec.textSizeSp
        else -> node.props[TypedPropKeys.TextSizeSp]
    }

    private fun readNodeAlpha(node: VNode): Float? {
        return node.props[TypedPropKeys.StyleAlpha]
    }

    private fun readNodeBackgroundColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.backgroundColor
        is TextFieldNodeProps -> spec.backgroundColor
        is IconButtonNodeProps -> spec.backgroundColor
        else -> node.props[TypedPropKeys.StyleBackgroundColor]
    }

    private fun readNodeBorderWidth(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderWidth
        is TextFieldNodeProps -> spec.borderWidth
        is IconButtonNodeProps -> spec.borderWidth
        else -> node.props[TypedPropKeys.StyleBorderWidth]
    }

    private fun readNodeBorderColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderColor
        is TextFieldNodeProps -> spec.borderColor
        is IconButtonNodeProps -> spec.borderColor
        else -> node.props[TypedPropKeys.StyleBorderColor]
    }

    private fun readNodeCornerRadius(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.cornerRadius
        is TextFieldNodeProps -> spec.cornerRadius
        is IconButtonNodeProps -> spec.cornerRadius
        else -> node.props[TypedPropKeys.StyleCornerRadius]
    }

    private fun readNodeRippleColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.rippleColor
        is TextFieldNodeProps -> spec.rippleColor
        is IconButtonNodeProps -> spec.rippleColor
        is ToggleNodeProps -> spec.rippleColor
        else -> node.props[TypedPropKeys.StyleRippleColor]
    }

    private fun readNodeClickable(node: VNode): Boolean = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.onClick != null && spec.enabled
        is IconButtonNodeProps -> spec.enabled
        else -> false
    }

    private fun readNodeMinHeight(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.minHeight
        is TextFieldNodeProps -> spec.minHeight
        else -> node.props[TypedPropKeys.StyleMinHeight]
    }

    private fun readNodePadding(node: VNode): PaddingModifierElement? = when (val spec = node.spec) {
        is ButtonNodeProps -> PaddingModifierElement(
            left = spec.paddingHorizontal,
            top = spec.paddingVertical,
            right = spec.paddingHorizontal,
            bottom = spec.paddingVertical,
        )
        is TextFieldNodeProps -> PaddingModifierElement(
            left = spec.paddingHorizontal,
            top = spec.paddingVertical,
            right = spec.paddingHorizontal,
            bottom = spec.paddingVertical,
        )
        is IconButtonNodeProps -> PaddingModifierElement(
            left = spec.contentPadding,
            top = spec.contentPadding,
            right = spec.contentPadding,
            bottom = spec.contentPadding,
        )
        else -> {
            val left = node.props[TypedPropKeys.StylePaddingLeft] ?: return null
            val top = node.props[TypedPropKeys.StylePaddingTop] ?: return null
            val right = node.props[TypedPropKeys.StylePaddingRight] ?: return null
            val bottom = node.props[TypedPropKeys.StylePaddingBottom] ?: return null
            PaddingModifierElement(left = left, top = top, right = right, bottom = bottom)
        }
    }

    private fun readAnchorId(node: VNode): String? {
        return node.props[TypedPropKeys.AnchorId]
    }

    private fun applyRecyclerContainerDefaults(
        view: View,
        node: VNode,
    ) {
        when (node.type) {
            NodeType.LazyColumn -> {
                val recyclerView = view as? RecyclerView ?: return
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                FrameworkRecyclerViewDefaults.applyLazyColumnDefaults(
                    recyclerView = recyclerView,
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                )
                val focusPolicy = node.modifier.lazyContainerFocusPolicy()
                LazyFocusFollowLayoutMonitor.apply(
                    recyclerView = recyclerView,
                    enabled = focusPolicy.enabled,
                )
            }
            NodeType.LazyRow -> {
                val recyclerView = view as? RecyclerView ?: return
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                FrameworkRecyclerViewDefaults.applyLazyRowDefaults(
                    recyclerView = recyclerView,
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                )
                val focusPolicy = node.modifier.lazyContainerFocusPolicy()
                if (focusPolicy.enabled) {
                    warnUnsupportedFocusFollowOnce(
                        view = recyclerView,
                        nodeType = node.type,
                    )
                }
                // Keyboard follow targets vertical overflow; LazyRow keeps horizontal-only semantics.
                LazyFocusFollowLayoutMonitor.apply(recyclerView, enabled = false)
            }
            NodeType.LazyVerticalGrid -> {
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                (view as? DeclarativeLazyVerticalGridLayout)?.applyRecyclerDefaults(
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                )
                val focusPolicy = node.modifier.lazyContainerFocusPolicy()
                (view as? DeclarativeLazyVerticalGridLayout)?.setFocusFollowKeyboardEnabled(focusPolicy.enabled)
            }
            NodeType.HorizontalPager -> {
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                (view as? DeclarativeHorizontalPagerLayout)?.applyRecyclerDefaults(
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                )
                val focusPolicy = node.modifier.lazyContainerFocusPolicy()
                if (focusPolicy.enabled) {
                    warnUnsupportedFocusFollowOnce(
                        view = view,
                        nodeType = node.type,
                    )
                }
            }
            NodeType.VerticalPager -> {
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                (view as? DeclarativeVerticalPagerLayout)?.applyRecyclerDefaults(
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                )
                val focusPolicy = node.modifier.lazyContainerFocusPolicy()
                (view as? DeclarativeVerticalPagerLayout)?.setFocusFollowKeyboardEnabled(focusPolicy.enabled)
            }
            NodeType.ScrollableColumn -> {
                val focusPolicy = node.modifier.lazyContainerFocusPolicy()
                (view as? DeclarativeScrollableColumnLayout)?.let { scrollView ->
                    ScrollableFocusFollowLayoutMonitor.apply(
                        scrollView = scrollView,
                        enabled = focusPolicy.enabled,
                    )
                }
            }
            NodeType.ScrollableRow -> {
                val focusPolicy = node.modifier.lazyContainerFocusPolicy()
                if (focusPolicy.enabled) {
                    warnUnsupportedFocusFollowOnce(
                        view = view,
                        nodeType = node.type,
                    )
                }
            }
            else -> Unit
        }
    }

    private fun warnUnsupportedFocusFollowOnce(
        view: View,
        nodeType: NodeType,
    ) {
        if (view.getTag(R.id.ui_framework_focus_follow_warning_emitted) == true) {
            return
        }
        view.setTag(R.id.ui_framework_focus_follow_warning_emitted, true)
        Log.w(
            FOCUS_FOLLOW_TAG,
            "focusFollowKeyboard(enabled=true) is ignored for $nodeType because keyboard follow only targets vertical overflow containers.",
        )
    }

    private fun applyNativeViewConfigs(view: View, node: VNode) {
        for (element in node.modifier.elements) {
            if (element is NativeViewElement) {
                element.configure(view)
            }
        }
    }

    private fun applyWindowInsetsPadding(
        view: View,
        systemBarsModifier: SystemBarsInsetsPaddingModifierElement?,
        imeModifier: ImeInsetsPaddingModifierElement?,
    ) {
        if (systemBarsModifier == null && imeModifier == null) {
            val state = view.getTag(R.id.ui_framework_system_bars_padding_state) as? WindowInsetsPaddingState
            if (state != null) {
                view.setPadding(state.baseLeft, state.baseTop, state.baseRight, state.baseBottom)
                view.setTag(R.id.ui_framework_system_bars_padding_state, null)
            }
            ViewCompat.setOnApplyWindowInsetsListener(view, null)
            return
        }

        val state = (view.getTag(R.id.ui_framework_system_bars_padding_state) as? WindowInsetsPaddingState)
            ?: WindowInsetsPaddingState().also {
                view.setTag(R.id.ui_framework_system_bars_padding_state, it)
            }
        state.baseLeft = view.paddingLeft - state.appliedLeft
        state.baseTop = view.paddingTop - state.appliedTop
        state.baseRight = view.paddingRight - state.appliedRight
        state.baseBottom = view.paddingBottom - state.appliedBottom

        ViewCompat.setOnApplyWindowInsetsListener(view) { target, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            state.appliedLeft =
                (if (systemBarsModifier?.left == true) systemBars.left else 0) +
                (if (imeModifier?.left == true) ime.left else 0)
            state.appliedTop =
                (if (systemBarsModifier?.top == true) systemBars.top else 0) +
                (if (imeModifier?.top == true) ime.top else 0)
            state.appliedRight =
                (if (systemBarsModifier?.right == true) systemBars.right else 0) +
                (if (imeModifier?.right == true) ime.right else 0)
            state.appliedBottom =
                (if (systemBarsModifier?.bottom == true) systemBars.bottom else 0) +
                (if (imeModifier?.bottom == true) ime.bottom else 0)
            target.setPadding(
                state.baseLeft + state.appliedLeft,
                state.baseTop + state.appliedTop,
                state.baseRight + state.appliedRight,
                state.baseBottom + state.appliedBottom,
            )
            insets
        }
        view.requestApplyInsetsWhenAttached()
    }

    private fun View.requestApplyInsetsWhenAttached() {
        if (isAttachedToWindow) {
            requestApplyInsets()
            return
        }
        addOnAttachStateChangeListener(
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view: View) {
                    view.removeOnAttachStateChangeListener(this)
                    view.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(view: View) = Unit
            },
        )
    }

    private data class WindowInsetsPaddingState(
        var baseLeft: Int = 0,
        var baseTop: Int = 0,
        var baseRight: Int = 0,
        var baseBottom: Int = 0,
        var appliedLeft: Int = 0,
        var appliedTop: Int = 0,
        var appliedRight: Int = 0,
        var appliedBottom: Int = 0,
    )
}
