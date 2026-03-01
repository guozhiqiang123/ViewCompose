package com.gzq.uiframework

import com.gzq.uiframework.renderer.view.tree.RenderStats

internal data class DemoRenderSnapshot(
    val renderCount: Int = 0,
    val stats: RenderStats = RenderStats(),
    val updatedAtMillis: Long = 0L,
)

internal object DemoRenderDiagnosticsStore {
    @Volatile
    private var latestSnapshot: DemoRenderSnapshot = DemoRenderSnapshot()

    fun record(
        stats: RenderStats,
    ) {
        val previous = latestSnapshot
        latestSnapshot = DemoRenderSnapshot(
            renderCount = previous.renderCount + 1,
            stats = stats,
            updatedAtMillis = System.currentTimeMillis(),
        )
    }

    fun snapshot(): DemoRenderSnapshot = latestSnapshot
}
