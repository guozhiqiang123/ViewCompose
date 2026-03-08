package com.viewcompose.renderer.view.tree

import android.view.View
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.NodeSpec
import kotlin.reflect.KClass

internal object NodeViewBinderRegistry {
    private val binders: Map<NodeType, (View, VNode) -> Unit> by lazy {
        NodeBinderDescriptors.bindersByType()
    }
    private val patchAppliers: Map<KClass<out NodeViewPatch>, (View, NodeViewPatch) -> Unit> by lazy {
        NodeBinderDescriptors.patchAppliersByType()
    }

    fun bind(
        view: View,
        node: VNode,
    ) {
        binders.getValue(node.type).invoke(view, node)
    }

    fun applyPatch(
        view: View,
        patch: NodeViewPatch,
    ) {
        patchAppliers[patch::class]?.invoke(view, patch)
            ?: error("Unknown patch type: ${patch::class.simpleName}")
    }

    internal fun descriptorsForTest(): List<NodeBinderDescriptor> = NodeBinderDescriptors.all

    internal fun patchAppliersForTest(): Map<KClass<out NodeViewPatch>, (View, NodeViewPatch) -> Unit> = patchAppliers

    internal fun patchFactoriesForTest(): Map<KClass<out NodeSpec>, PatchFactory> = NodeBinderDescriptors.patchFactoriesBySpec()
}
