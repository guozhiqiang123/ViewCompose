package com.gzq.uiframework.renderer.view.tree

data class LayoutPassEntry(
    val viewName: String,
    val measureCount: Int,
    val layoutCount: Int,
)

data class LayoutPassSnapshot(
    val totalMeasureCount: Int = 0,
    val totalLayoutCount: Int = 0,
    val entries: List<LayoutPassEntry> = emptyList(),
)

object LayoutPassTracker {
    private val counters = linkedMapOf<String, MutableLayoutPassCounter>()

    @Synchronized
    fun recordMeasure(
        viewName: String,
    ) {
        val counter = counters.getOrPut(viewName) {
            MutableLayoutPassCounter(viewName)
        }
        counter.measureCount += 1
    }

    @Synchronized
    fun recordLayout(
        viewName: String,
    ) {
        val counter = counters.getOrPut(viewName) {
            MutableLayoutPassCounter(viewName)
        }
        counter.layoutCount += 1
    }

    @Synchronized
    fun snapshot(): LayoutPassSnapshot {
        val entries = counters.values
            .map { counter ->
                LayoutPassEntry(
                    viewName = counter.viewName,
                    measureCount = counter.measureCount,
                    layoutCount = counter.layoutCount,
                )
            }
            .sortedWith(
                compareByDescending<LayoutPassEntry> { it.measureCount + it.layoutCount }
                    .thenBy { it.viewName },
            )
        return LayoutPassSnapshot(
            totalMeasureCount = entries.sumOf { it.measureCount },
            totalLayoutCount = entries.sumOf { it.layoutCount },
            entries = entries,
        )
    }

    @Synchronized
    fun reset() {
        counters.clear()
    }
}

private class MutableLayoutPassCounter(
    val viewName: String,
) {
    var measureCount: Int = 0
    var layoutCount: Int = 0
}
