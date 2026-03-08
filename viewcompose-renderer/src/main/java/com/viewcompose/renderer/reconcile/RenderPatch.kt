package com.viewcompose.renderer.reconcile

import com.viewcompose.ui.node.VNode

data class ReconcileNode<T>(
    val vnode: VNode,
    val payload: T,
)

sealed interface RenderPatch<T> {
    val targetIndex: Int
}

data class ReusePatch<T>(
    override val targetIndex: Int,
    val previousIndex: Int,
    val payload: T,
    val nextVNode: VNode,
) : RenderPatch<T>

data class InsertPatch<T>(
    override val targetIndex: Int,
    val nextVNode: VNode,
) : RenderPatch<T>

data class RemovePatch<T>(
    val previousIndex: Int,
    val payload: T,
)
