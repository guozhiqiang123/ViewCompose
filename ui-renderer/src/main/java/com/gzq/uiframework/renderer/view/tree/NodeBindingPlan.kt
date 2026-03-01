package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps

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
