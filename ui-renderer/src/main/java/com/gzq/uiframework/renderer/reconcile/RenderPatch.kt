package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.view.MountedNode

sealed interface RenderPatch {
    val targetIndex: Int
}

data class ReusePatch(
    override val targetIndex: Int,
    val previousIndex: Int,
    val mountedNode: MountedNode,
    val nextVNode: VNode,
) : RenderPatch

data class InsertPatch(
    override val targetIndex: Int,
    val nextVNode: VNode,
) : RenderPatch

data class RemovePatch(
    val previousIndex: Int,
    val mountedNode: MountedNode,
)
