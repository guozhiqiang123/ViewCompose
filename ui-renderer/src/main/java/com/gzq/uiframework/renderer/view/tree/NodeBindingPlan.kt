package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps

internal sealed interface NodeBindingPlan {
    data object Skip : NodeBindingPlan

    data object Rebind : NodeBindingPlan

    data class Patch(
        val patch: NodeViewPatch,
    ) : NodeBindingPlan
}

internal sealed interface NodeViewPatch

internal data class ButtonNodePatch(
    val previous: ButtonNodeProps,
    val next: ButtonNodeProps,
) : NodeViewPatch

internal data class TextNodePatch(
    val previous: TextNodeProps,
    val next: TextNodeProps,
) : NodeViewPatch

internal data class TextFieldNodePatch(
    val previous: TextFieldNodeProps,
    val next: TextFieldNodeProps,
) : NodeViewPatch
