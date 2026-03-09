package com.viewcompose.renderer.view.tree

import android.view.View
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.NodeSpec
import kotlin.reflect.KClass

internal typealias BindBlock = (View, VNode) -> Unit
internal typealias PatchApplyBlock = (View, NodeViewPatch) -> Unit
internal typealias PatchFactory = (NodeSpec, NodeSpec) -> NodeViewPatch

internal data class NodeBinderDescriptor(
    val nodeType: NodeType,
    val bind: BindBlock,
    val patch: NodePatchDescriptor? = null,
)

internal data class NodePatchDescriptor(
    val patchClass: KClass<out NodeViewPatch>,
    val specClass: KClass<out NodeSpec>,
    val factory: PatchFactory,
    val apply: PatchApplyBlock,
)

internal fun descriptor(
    nodeType: NodeType,
    bind: BindBlock,
    patch: NodePatchDescriptor? = null,
): NodeBinderDescriptor = NodeBinderDescriptor(
    nodeType = nodeType,
    bind = bind,
    patch = patch,
)

internal inline fun <reified S : NodeSpec, reified P : NodeViewPatch> patchDescriptor(
    noinline factory: (S, S) -> P,
    noinline apply: (View, P) -> Unit,
): NodePatchDescriptor {
    return NodePatchDescriptor(
        patchClass = P::class,
        specClass = S::class,
        factory = { previous, next -> factory(previous as S, next as S) },
        apply = { view, patch -> apply(view, patch as P) },
    )
}
