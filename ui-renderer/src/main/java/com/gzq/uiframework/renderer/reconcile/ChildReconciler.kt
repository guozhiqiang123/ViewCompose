package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.view.MountedNode

data class ReconcileResult(
    val patches: List<RenderPatch>,
    val removals: List<RemovePatch>,
)

object ChildReconciler {
    fun reconcile(
        previous: List<MountedNode>,
        nodes: List<VNode>,
    ): ReconcileResult {
        val usedPrevious = BooleanArray(previous.size)
        val patches = buildList {
            nodes.forEachIndexed { index, node ->
                val reusableIndex = findReusableIndex(
                    previous = previous,
                    usedPrevious = usedPrevious,
                    targetIndex = index,
                    node = node,
                )
                val previousNode = reusableIndex?.let(previous::get)
                if (previousNode != null) {
                    usedPrevious[reusableIndex] = true
                    add(
                        ReusePatch(
                            targetIndex = index,
                            previousIndex = reusableIndex,
                            mountedNode = previousNode,
                            nextVNode = node,
                        ),
                    )
                } else {
                    add(
                        InsertPatch(
                            targetIndex = index,
                            nextVNode = node,
                        ),
                    )
                }
            }
        }
        val removals = buildList {
            previous.forEachIndexed { index, mountedNode ->
                if (!usedPrevious[index]) {
                    add(
                        RemovePatch(
                            previousIndex = index,
                            mountedNode = mountedNode,
                        ),
                    )
                }
            }
        }
        return ReconcileResult(
            patches = patches,
            removals = removals,
        )
    }

    private fun findReusableIndex(
        previous: List<MountedNode>,
        usedPrevious: BooleanArray,
        targetIndex: Int,
        node: VNode,
    ): Int? {
        if (node.key != null) {
            previous.forEachIndexed { index, mountedNode ->
                if (!usedPrevious[index] && canReuse(mountedNode.vnode, node)) {
                    return index
                }
            }
            return null
        }

        val candidate = previous.getOrNull(targetIndex) ?: return null
        return if (!usedPrevious[targetIndex] && canReuse(candidate.vnode, node)) {
            targetIndex
        } else {
            null
        }
    }

    private fun canReuse(previous: VNode, next: VNode): Boolean {
        if (previous.type != next.type) {
            return false
        }
        return previous.key == next.key
    }
}
