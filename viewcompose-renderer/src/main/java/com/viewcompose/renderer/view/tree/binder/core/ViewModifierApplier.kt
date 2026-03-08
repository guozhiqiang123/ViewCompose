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
import com.viewcompose.renderer.modifier.focusFollowKeyboardPolicy
import com.viewcompose.renderer.modifier.lazyContainerReusePolicy
import com.viewcompose.renderer.modifier.resolve
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.ButtonNodeProps
import com.viewcompose.renderer.node.spec.BoxNodeProps
import com.viewcompose.renderer.node.spec.IconButtonNodeProps
import com.viewcompose.renderer.node.spec.TextFieldNodeProps
import com.viewcompose.renderer.node.spec.TextNodeProps
import com.viewcompose.renderer.node.spec.ToggleNodeProps
import com.viewcompose.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.viewcompose.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.viewcompose.renderer.view.container.DeclarativeScrollableColumnLayout
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
        applyScrollableContainerPolicies(
            view = view,
            node = node,
        )
        val nodeStyle = resolveNodeStyle(
            node = node,
            resolved = resolved,
            defaultRippleColor = defaultRippleColor,
        )
        val hostStyle = resolveHostStyle(
            resolved = resolved,
            nodeStyle = nodeStyle,
        )
        applySurfaceStyle(
            view = view,
            resolved = resolved,
            nodeStyle = nodeStyle,
        )
        applyCommonHostProperties(
            view = view,
            resolved = resolved,
            minHeight = hostStyle.minHeight,
            minWidth = hostStyle.minWidth,
        )
        applyClickAndFocusState(
            view = view,
            node = node,
            resolved = resolved,
        )
        applyHostPaddingWhenNoInsets(
            view = view,
            hasWindowInsetsPadding = hostStyle.hasWindowInsetsPadding,
            hostPadding = hostStyle.padding,
        )
        applyWindowInsetsPadding(
            view = view,
            systemBarsModifier = resolved.systemBarsInsetsPadding,
            imeModifier = resolved.imeInsetsPadding,
            basePadding = if (hostStyle.hasWindowInsetsPadding) {
                nodeStyle.padding
            } else {
                null
            },
        )
        applyTextAppearanceIfTextView(
            view = view,
            textColor = nodeStyle.textColor,
            textSizeSp = nodeStyle.textSizeSp,
        )
    }

    private fun resolveNodeStyle(
        node: VNode,
        resolved: ResolvedModifiers,
        defaultRippleColor: Int,
    ): NodeStyle {
        return NodeStyle(
            backgroundColor = resolved.backgroundColor?.color ?: readNodeBackgroundColor(node),
            borderWidth = resolved.border?.width ?: readNodeBorderWidth(node) ?: 0,
            borderColor = resolved.border?.color ?: readNodeBorderColor(node) ?: Color.TRANSPARENT,
            cornerRadius = resolved.cornerRadius
                ?: readNodeCornerRadius(node)?.let {
                    CornerRadiusModifierElement(it, it, it, it)
                },
            padding = resolved.padding ?: readNodePadding(node),
            minHeight = resolved.minHeight?.minHeight ?: readNodeMinHeight(node) ?: 0,
            minWidth = resolved.minWidth?.minWidth ?: 0,
            rippleColor = readNodeRippleColor(node) ?: defaultRippleColor,
            textColor = readNodeTextColor(node),
            textSizeSp = readNodeTextSize(node),
            clickable = resolved.clickable != null || readNodeClickable(node),
        )
    }

    private fun resolveHostStyle(
        resolved: ResolvedModifiers,
        nodeStyle: NodeStyle,
    ): HostStyle {
        val hasWindowInsetsPadding = resolved.systemBarsInsetsPadding != null || resolved.imeInsetsPadding != null
        return HostStyle(
            hasWindowInsetsPadding = hasWindowInsetsPadding,
            padding = nodeStyle.padding,
            minHeight = nodeStyle.minHeight,
            minWidth = nodeStyle.minWidth,
        )
    }

    private fun applySurfaceStyle(
        view: View,
        resolved: ResolvedModifiers,
        nodeStyle: NodeStyle,
    ) {
        applyBackgroundAndInteraction(
            view = view,
            backgroundColor = nodeStyle.backgroundColor,
            borderWidth = nodeStyle.borderWidth,
            borderColor = nodeStyle.borderColor,
            cornerRadius = nodeStyle.cornerRadius,
            rippleColor = nodeStyle.rippleColor,
            clickable = nodeStyle.clickable,
            forceClip = resolved.clip?.clip ?: false,
        )
    }

    private fun applyCommonHostProperties(
        view: View,
        resolved: ResolvedModifiers,
        minHeight: Int,
        minWidth: Int,
    ) {
        // Anchor metadata is sourced only from resolved modifier elements.
        applyAnchorId(view, resolved.overlayAnchor?.anchorId)
        applyTestTag(view, resolved.testTag?.tag)
        view.alpha = resolved.alpha?.alpha ?: 1f
        view.visibility = when (resolved.visibility?.visibility ?: Visibility.Visible) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
        view.translationX = resolved.offset?.x ?: 0f
        view.translationY = resolved.offset?.y ?: 0f
        view.translationZ = resolved.zIndex?.zIndex ?: 0f
        view.elevation = resolved.elevation?.elevation?.toFloat() ?: 0f
        view.minimumHeight = minHeight
        view.minimumWidth = minWidth
        view.contentDescription = resolved.contentDescription?.contentDescription
    }

    private fun applyClickAndFocusState(
        view: View,
        node: VNode,
        resolved: ResolvedModifiers,
    ) {
        if (node.type == NodeType.TextField) {
            // EditText should keep its intrinsic focus/click semantics.
            view.setOnClickListener(null)
            return
        }
        val clickListener = resolved.clickable?.let { clickableElement ->
            View.OnClickListener { clickableElement.onClick() }
        }
        val hasClickListener = clickListener != null
        val keepIntrinsicInteraction = shouldKeepIntrinsicInteraction(node.type)
        view.setOnClickListener(clickListener)
        view.isClickable = hasClickListener || keepIntrinsicInteraction
        view.isFocusable = hasClickListener || keepIntrinsicInteraction
        view.isFocusableInTouchMode = false
    }

    private fun applyHostPaddingWhenNoInsets(
        view: View,
        hasWindowInsetsPadding: Boolean,
        hostPadding: PaddingModifierElement?,
    ) {
        if (hasWindowInsetsPadding) return
        if (hostPadding == null) {
            view.setPadding(0, 0, 0, 0)
            return
        }
        view.setPadding(
            hostPadding.left,
            hostPadding.top,
            hostPadding.right,
            hostPadding.bottom,
        )
    }

    private fun applyTextAppearanceIfTextView(
        view: View,
        textColor: Int?,
        textSizeSp: Int?,
    ) {
        if (view !is TextView) return
        if (textColor != null) {
            view.setTextColor(textColor)
        }
        if (textSizeSp != null) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
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
        else -> null
    }

    private fun readNodeTextSize(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.textSizeSp
        is TextNodeProps -> spec.textSizeSp
        is TextFieldNodeProps -> spec.textSizeSp
        is ToggleNodeProps -> spec.textSizeSp
        else -> null
    }

    private fun readNodeBackgroundColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.backgroundColor
        is TextFieldNodeProps -> spec.backgroundColor
        is IconButtonNodeProps -> spec.backgroundColor
        else -> null
    }

    private fun readNodeBorderWidth(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderWidth
        is TextFieldNodeProps -> spec.borderWidth
        is IconButtonNodeProps -> spec.borderWidth
        else -> null
    }

    private fun readNodeBorderColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.borderColor
        is TextFieldNodeProps -> spec.borderColor
        is IconButtonNodeProps -> spec.borderColor
        else -> null
    }

    private fun readNodeCornerRadius(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.cornerRadius
        is TextFieldNodeProps -> spec.cornerRadius
        is IconButtonNodeProps -> spec.cornerRadius
        else -> null
    }

    private fun readNodeRippleColor(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.rippleColor
        is TextFieldNodeProps -> spec.rippleColor
        is IconButtonNodeProps -> spec.rippleColor
        is ToggleNodeProps -> spec.rippleColor
        is BoxNodeProps -> spec.rippleColor
        else -> null
    }

    private fun readNodeClickable(node: VNode): Boolean = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.onClick != null && spec.enabled
        is IconButtonNodeProps -> spec.enabled
        else -> false
    }

    private fun readNodeMinHeight(node: VNode): Int? = when (val spec = node.spec) {
        is ButtonNodeProps -> spec.minHeight
        is TextFieldNodeProps -> spec.minHeight
        else -> null
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
        else -> null
    }

    private fun applyScrollableContainerPolicies(
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
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
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
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
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
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                (view as? DeclarativeLazyVerticalGridLayout)?.setFocusFollowKeyboardEnabled(focusPolicy.enabled)
            }
            NodeType.HorizontalPager -> {
                val reusePolicy = node.modifier.lazyContainerReusePolicy()
                (view as? DeclarativeHorizontalPagerLayout)?.applyRecyclerDefaults(
                    sharePool = reusePolicy.sharePool,
                    disableItemAnimator = reusePolicy.disableItemAnimator,
                )
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
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
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                (view as? DeclarativeVerticalPagerLayout)?.setFocusFollowKeyboardEnabled(focusPolicy.enabled)
            }
            NodeType.ScrollableColumn -> {
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
                (view as? DeclarativeScrollableColumnLayout)?.let { scrollView ->
                    ScrollableFocusFollowLayoutMonitor.apply(
                        scrollView = scrollView,
                        enabled = focusPolicy.enabled,
                    )
                }
            }
            NodeType.ScrollableRow -> {
                val focusPolicy = node.modifier.focusFollowKeyboardPolicy()
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
        basePadding: PaddingModifierElement?,
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
        if (basePadding != null) {
            state.baseLeft = basePadding.left
            state.baseTop = basePadding.top
            state.baseRight = basePadding.right
            state.baseBottom = basePadding.bottom
        } else {
            state.baseLeft = view.paddingLeft - state.appliedLeft
            state.baseTop = view.paddingTop - state.appliedTop
            state.baseRight = view.paddingRight - state.appliedRight
            state.baseBottom = view.paddingBottom - state.appliedBottom
        }

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

    private data class NodeStyle(
        val backgroundColor: Int?,
        val borderWidth: Int,
        val borderColor: Int,
        val cornerRadius: CornerRadiusModifierElement?,
        val padding: PaddingModifierElement?,
        val minHeight: Int,
        val minWidth: Int,
        val rippleColor: Int,
        val textColor: Int?,
        val textSizeSp: Int?,
        val clickable: Boolean,
    )

    private data class HostStyle(
        val hasWindowInsetsPadding: Boolean,
        val padding: PaddingModifierElement?,
        val minHeight: Int,
        val minWidth: Int,
    )
}
