package com.viewcompose.renderer.reconcile

import com.viewcompose.renderer.node.VNode

data class ReconcileResult<T>(
    val patches: List<RenderPatch<T>>,
    val removals: List<RemovePatch<T>>,
)

object ChildReconciler {
    fun <T> reconcile(
        previous: List<ReconcileNode<T>>,
        nodes: List<VNode>,
    ): ReconcileResult<T> {
        val usedPrevious = BooleanArray(previous.size)
        val keyedIndex = buildKeyedIndex(previous)
        val patches = buildList {
            nodes.forEachIndexed { index, node ->
                val reusableIndex = findReusableIndex(
                    previous = previous,
                    usedPrevious = usedPrevious,
                    keyedIndex = keyedIndex,
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
                            payload = previousNode.payload,
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
                            payload = mountedNode.payload,
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

    private fun <T> buildKeyedIndex(
        previous: List<ReconcileNode<T>>,
    ): Map<Any, MutableList<Int>> {
        val map = HashMap<Any, MutableList<Int>>(previous.size)
        previous.forEachIndexed { index, node ->
            val key = node.vnode.key
            if (key != null) {
                map.getOrPut(key) { mutableListOf() }.add(index)
            }
        }
        return map
    }

    private fun <T> findReusableIndex(
        previous: List<ReconcileNode<T>>,
        usedPrevious: BooleanArray,
        keyedIndex: Map<Any, MutableList<Int>>,
        targetIndex: Int,
        node: VNode,
    ): Int? {
        if (node.key != null) {
            val candidates = keyedIndex[node.key] ?: return null
            for (candidateIndex in candidates) {
                if (!usedPrevious[candidateIndex] &&
                    canReuse(previous[candidateIndex].vnode, node)
                ) {
                    return candidateIndex
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
