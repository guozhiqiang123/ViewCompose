package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps

internal object NodeBindingDiffer {
    fun plan(
        previous: VNode,
        next: VNode,
    ): NodeBindingPlan {
        if (previous.type != next.type) {
            return NodeBindingPlan.Rebind
        }
        if (previous.modifier != next.modifier) {
            return NodeBindingPlan.Rebind
        }
        if (readStyleSignature(previous) != readStyleSignature(next)) {
            return NodeBindingPlan.Rebind
        }
        if (previous.spec != null || next.spec != null) {
            if (previous.spec == next.spec) {
                return NodeBindingPlan.Skip
            }
            val previousButton = previous.spec as? ButtonNodeProps
            val nextButton = next.spec as? ButtonNodeProps
            if (previousButton != null && nextButton != null) {
                return NodeBindingPlan.Patch(
                    patch = ButtonNodePatch(
                        previous = previousButton,
                        next = nextButton,
                    ),
                )
            }
            val previousText = previous.spec as? TextNodeProps
            val nextText = next.spec as? TextNodeProps
            if (previousText != null && nextText != null) {
                return NodeBindingPlan.Patch(
                    patch = TextNodePatch(
                        previous = previousText,
                        next = nextText,
                    ),
                )
            }
            return NodeBindingPlan.Rebind
        }
        if (previous.props != next.props) {
            return NodeBindingPlan.Rebind
        }
        return NodeBindingPlan.Skip
    }

    private fun readStyleSignature(node: VNode): StyleSignature {
        return StyleSignature(
            textColor = node.props[TypedPropKeys.TextColor],
            textSizeSp = node.props[TypedPropKeys.TextSizeSp],
            alpha = node.props[TypedPropKeys.StyleAlpha],
            backgroundColor = node.props[TypedPropKeys.StyleBackgroundColor],
            borderWidth = node.props[TypedPropKeys.StyleBorderWidth],
            borderColor = node.props[TypedPropKeys.StyleBorderColor],
            cornerRadius = node.props[TypedPropKeys.StyleCornerRadius],
            rippleColor = node.props[TypedPropKeys.StyleRippleColor],
            minHeight = node.props[TypedPropKeys.StyleMinHeight],
            paddingLeft = node.props[TypedPropKeys.StylePaddingLeft],
            paddingTop = node.props[TypedPropKeys.StylePaddingTop],
            paddingRight = node.props[TypedPropKeys.StylePaddingRight],
            paddingBottom = node.props[TypedPropKeys.StylePaddingBottom],
        )
    }
}

private data class StyleSignature(
    val textColor: Int?,
    val textSizeSp: Int?,
    val alpha: Float?,
    val backgroundColor: Int?,
    val borderWidth: Int?,
    val borderColor: Int?,
    val cornerRadius: Int?,
    val rippleColor: Int?,
    val minHeight: Int?,
    val paddingLeft: Int?,
    val paddingTop: Int?,
    val paddingRight: Int?,
    val paddingBottom: Int?,
)
