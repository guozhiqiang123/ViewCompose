package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.node.LazyListItem

sealed interface LazyListUpdate {
    data class Insert(
        val index: Int,
        val item: LazyListItem,
    ) : LazyListUpdate

    data class Remove(
        val index: Int,
    ) : LazyListUpdate

    data class Move(
        val fromIndex: Int,
        val toIndex: Int,
    ) : LazyListUpdate

    data class Change(
        val index: Int,
        val item: LazyListItem,
    ) : LazyListUpdate

    data object ReloadAll : LazyListUpdate
}

data class LazyListDiffResult(
    val updates: List<LazyListUpdate>,
    val items: List<LazyListItem>,
)

object LazyListDiff {
    fun calculate(
        previous: List<LazyListItem>,
        next: List<LazyListItem>,
    ): LazyListDiffResult {
        if (!canDiffByKey(previous, next)) {
            return LazyListDiffResult(
                updates = listOf(LazyListUpdate.ReloadAll),
                items = next,
            )
        }

        val working = previous.toMutableList()
        val updates = mutableListOf<LazyListUpdate>()
        val nextKeys = next.map { it.key }.toSet()

        for (index in working.lastIndex downTo 0) {
            if (working[index].key !in nextKeys) {
                working.removeAt(index)
                updates += LazyListUpdate.Remove(index)
            }
        }

        next.forEachIndexed { index, item ->
            val currentIndex = working.indexOfFirst { it.key == item.key }
            if (currentIndex == -1) {
                working.add(index, item)
                updates += LazyListUpdate.Insert(index, item)
            } else {
                if (currentIndex != index) {
                    val moved = working.removeAt(currentIndex)
                    working.add(index, moved)
                    updates += LazyListUpdate.Move(currentIndex, index)
                }
                val previousItem = working[index]
                working[index] = item
                if (previousItem != item) {
                    updates += LazyListUpdate.Change(index, item)
                }
            }
        }

        return LazyListDiffResult(
            updates = updates,
            items = working,
        )
    }

    private fun canDiffByKey(
        previous: List<LazyListItem>,
        next: List<LazyListItem>,
    ): Boolean {
        return LazyListIdentityInspector.analyze(previous).supportsKeyedDiff &&
            LazyListIdentityInspector.analyze(next).supportsKeyedDiff
    }
}
