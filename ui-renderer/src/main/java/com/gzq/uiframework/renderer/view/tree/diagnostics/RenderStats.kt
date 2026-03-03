package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.NodeType

data class RenderStats(
    val inserts: Int = 0,
    val reuses: Int = 0,
    val removals: Int = 0,
    val reboundNodes: Int = 0,
    val patchedNodes: Int = 0,
    val skippedBindings: Int = 0,
    val bindingsByType: Map<NodeType, NodeTypeBindingStats> = emptyMap(),
) {
    fun withInsert(): RenderStats = copy(inserts = inserts + 1)

    fun withReuse(
        result: ReuseBindingResult,
        nodeType: NodeType,
    ): RenderStats {
        val existing = bindingsByType[nodeType] ?: NodeTypeBindingStats()
        val updated = when (result) {
            ReuseBindingResult.Rebound -> existing.copy(rebound = existing.rebound + 1)
            ReuseBindingResult.Patched -> existing.copy(patched = existing.patched + 1)
            ReuseBindingResult.Skipped -> existing.copy(skipped = existing.skipped + 1)
        }
        return copy(
            reuses = reuses + 1,
            reboundNodes = reboundNodes + if (result == ReuseBindingResult.Rebound) 1 else 0,
            patchedNodes = patchedNodes + if (result == ReuseBindingResult.Patched) 1 else 0,
            skippedBindings = skippedBindings + if (result == ReuseBindingResult.Skipped) 1 else 0,
            bindingsByType = bindingsByType + (nodeType to updated),
        )
    }

    fun withRemoval(): RenderStats = copy(removals = removals + 1)

    fun mergeWith(other: RenderStats): RenderStats {
        return RenderStats(
            inserts = inserts + other.inserts,
            reuses = reuses + other.reuses,
            removals = removals + other.removals,
            reboundNodes = reboundNodes + other.reboundNodes,
            patchedNodes = patchedNodes + other.patchedNodes,
            skippedBindings = skippedBindings + other.skippedBindings,
            bindingsByType = mergeBindingsByType(bindingsByType, other.bindingsByType),
        )
    }
}

data class NodeTypeBindingStats(
    val rebound: Int = 0,
    val patched: Int = 0,
    val skipped: Int = 0,
)

data class RenderTreeResult(
    val mountedNodes: List<MountedNode>,
    val reconcileResult: com.gzq.uiframework.renderer.reconcile.ReconcileResult<MountedNode>,
    val stats: RenderStats,
    val structure: RenderStructureStats = RenderStructureStats(),
    val warnings: List<String> = emptyList(),
)

enum class ReuseBindingResult {
    Rebound,
    Patched,
    Skipped,
}

private fun mergeBindingsByType(
    a: Map<NodeType, NodeTypeBindingStats>,
    b: Map<NodeType, NodeTypeBindingStats>,
): Map<NodeType, NodeTypeBindingStats> {
    if (a.isEmpty()) return b
    if (b.isEmpty()) return a
    val result = a.toMutableMap()
    b.forEach { (type, stats) ->
        val existing = result[type]
        result[type] = if (existing == null) {
            stats
        } else {
            NodeTypeBindingStats(
                rebound = existing.rebound + stats.rebound,
                patched = existing.patched + stats.patched,
                skipped = existing.skipped + stats.skipped,
            )
        }
    }
    return result
}
