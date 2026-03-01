package com.gzq.uiframework.renderer.view.tree

data class RenderStats(
    val inserts: Int = 0,
    val reuses: Int = 0,
    val removals: Int = 0,
    val reboundNodes: Int = 0,
    val skippedBindings: Int = 0,
) {
    fun withInsert(): RenderStats = copy(inserts = inserts + 1)

    fun withReuse(
        rebound: Boolean,
    ): RenderStats {
        return copy(
            reuses = reuses + 1,
            reboundNodes = reboundNodes + if (rebound) 1 else 0,
            skippedBindings = skippedBindings + if (rebound) 0 else 1,
        )
    }

    fun withRemoval(): RenderStats = copy(removals = removals + 1)
}

data class RenderTreeResult(
    val mountedNodes: List<MountedNode>,
    val reconcileResult: com.gzq.uiframework.renderer.reconcile.ReconcileResult<MountedNode>,
    val stats: RenderStats,
)
