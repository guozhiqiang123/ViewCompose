package com.gzq.uiframework.renderer.reconcile

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
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

        val updates = mutableListOf<LazyListUpdate>()
        val working = previous.toMutableList()
        val diffResult = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = previous.size

                override fun getNewListSize(): Int = next.size

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int,
                ): Boolean {
                    return previous[oldItemPosition].key == next[newItemPosition].key
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int,
                ): Boolean {
                    return previous[oldItemPosition] == next[newItemPosition]
                }
            },
        )
        diffResult.dispatchUpdatesTo(
            RecordingLazyListUpdateCallback(
                working = working,
                next = next,
                updates = updates,
            ),
        )

        return LazyListDiffResult(
            updates = updates,
            // Always use latest item instances to preserve refreshed closures/session updaters.
            items = next,
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

private class RecordingLazyListUpdateCallback(
    private val working: MutableList<LazyListItem>,
    private val next: List<LazyListItem>,
    private val updates: MutableList<LazyListUpdate>,
) : ListUpdateCallback {
    override fun onInserted(
        position: Int,
        count: Int,
    ) {
        repeat(count) { offset ->
            val index = position + offset
            val item = next[index.coerceIn(0, next.lastIndex)]
            working.add(index.coerceIn(0, working.size), item)
            updates += LazyListUpdate.Insert(index, item)
        }
    }

    override fun onRemoved(
        position: Int,
        count: Int,
    ) {
        repeat(count) {
            working.removeAt(position)
            updates += LazyListUpdate.Remove(position)
        }
    }

    override fun onMoved(
        fromPosition: Int,
        toPosition: Int,
    ) {
        val moved = working.removeAt(fromPosition)
        working.add(toPosition, moved)
        updates += LazyListUpdate.Move(fromPosition, toPosition)
    }

    override fun onChanged(
        position: Int,
        count: Int,
        payload: Any?,
    ) {
        repeat(count) { offset ->
            val index = position + offset
            val clampedIndex = index.coerceIn(0, next.lastIndex)
            val item = next[clampedIndex]
            working[index.coerceIn(0, working.lastIndex)] = item
            updates += LazyListUpdate.Change(index, item)
        }
    }
}
