package com.viewcompose.renderer.view.tree

import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.viewcompose.renderer.R
import com.viewcompose.ui.modifier.NativeViewElement
import com.viewcompose.ui.modifier.Visibility
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.renderer.modifier.ResolvedModifiers

internal object ModifierInteractionApplier {
    fun applyCommonHostProperties(
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

    fun applyClickAndFocusState(
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

    fun applyTextAppearanceIfTextView(
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

    fun applyNativeViewConfigs(
        view: View,
        node: VNode,
    ) {
        for (element in node.modifier.elements) {
            if (element is NativeViewElement) {
                element.configure(view)
            }
        }
    }

    private fun shouldKeepIntrinsicInteraction(type: NodeType): Boolean {
        return type == NodeType.Checkbox ||
            type == NodeType.Switch ||
            type == NodeType.RadioButton ||
            type == NodeType.Slider
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
}
