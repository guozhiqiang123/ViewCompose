package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.NodeSpec
import kotlin.reflect.KClass

internal object NodeBinderDescriptors {
    val all: List<NodeBinderDescriptor> by lazy { buildDescriptors() }

    fun bindersByType(): Map<NodeType, BindBlock> = all.associateByUnique(
        keySelector = { it.nodeType },
        valueSelector = { it.bind },
        duplicateMessage = { "Duplicate binder descriptor for NodeType: $it" },
    )

    fun patchAppliersByType(): Map<KClass<out NodeViewPatch>, PatchApplyBlock> = all
        .uniquePatchDescriptorsBy(
            keySelector = { it.patchClass },
            duplicateMessage = {
                "Conflicting patch applier descriptor for NodeViewPatch: ${it.simpleName}"
            },
        )
        .associateByUnique(
            keySelector = { it.patchClass },
            valueSelector = { it.apply },
            duplicateMessage = { "Duplicate patch applier descriptor for NodeViewPatch: ${it.simpleName}" },
        )

    fun patchFactoriesBySpec(): Map<KClass<out NodeSpec>, PatchFactory> = all
        .uniquePatchDescriptorsBy(
            keySelector = { it.specClass },
            duplicateMessage = { "Conflicting patch factory descriptor for NodeSpec: ${it.simpleName}" },
        )
        .associateByUnique(
            keySelector = { it.specClass },
            valueSelector = { it.factory },
            duplicateMessage = { "Duplicate patch factory descriptor for NodeSpec: ${it.simpleName}" },
        )

    private inline fun <K, V, T> List<T>.associateByUnique(
        keySelector: (T) -> K,
        valueSelector: (T) -> V,
        duplicateMessage: (K) -> String,
    ): Map<K, V> {
        val result = LinkedHashMap<K, V>(size)
        for (item in this) {
            val key = keySelector(item)
            require(!result.containsKey(key)) { duplicateMessage(key) }
            result[key] = valueSelector(item)
        }
        return result
    }

    private inline fun <K> List<NodeBinderDescriptor>.uniquePatchDescriptorsBy(
        keySelector: (NodePatchDescriptor) -> K,
        duplicateMessage: (K) -> String,
    ): List<NodePatchDescriptor> {
        val result = LinkedHashMap<K, NodePatchDescriptor>()
        for (descriptor in this) {
            val patch = descriptor.patch ?: continue
            val key = keySelector(patch)
            val existing = result[key]
            if (existing == null) {
                result[key] = patch
                continue
            }
            require(existing === patch || (existing.specClass == patch.specClass && existing.patchClass == patch.patchClass)) {
                duplicateMessage(key)
            }
        }
        return result.values.toList()
    }

    private fun buildDescriptors(): List<NodeBinderDescriptor> {
        return buildList {
            addContentNodeBinderDescriptors()
            addInputNodeBinderDescriptors()
            addFeedbackNodeBinderDescriptors()
            addMediaNodeBinderDescriptors()
            addContainerNodeBinderDescriptors()
            addCollectionNodeBinderDescriptors()
        }
    }
}
