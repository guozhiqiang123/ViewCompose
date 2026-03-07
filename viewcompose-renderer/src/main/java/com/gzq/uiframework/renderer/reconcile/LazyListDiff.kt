package com.gzq.uiframework.renderer.reconcile

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.gzq.uiframework.renderer.node.LazyListItem

sealed interface LazyListUpdate {
    data class Insert(
        val index: Int,
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
        val payload: Any? = null,
    ) : LazyListUpdate

    data object ReloadAll : LazyListUpdate
}

sealed interface LazyListChangePayload {
    data class ContentTokenChanged(
        val previous: Any?,
        val next: Any?,
    ) : LazyListChangePayload
}

data class LazyListDiffResult(
    val updates: List<LazyListUpdate>,
    val items: List<LazyListItem>,
    val diffResult: DiffUtil.DiffResult? = null,
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
                diffResult = null,
            )
        }

        val updates = mutableListOf<LazyListUpdate>()
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

                override fun getChangePayload(
                    oldItemPosition: Int,
                    newItemPosition: Int,
                ): Any {
                    val oldItem = previous[oldItemPosition]
                    val newItem = next[newItemPosition]
                    return LazyListChangePayload.ContentTokenChanged(
                        previous = oldItem.contentToken,
                        next = newItem.contentToken,
                    )
                }
            },
        )
        diffResult.dispatchUpdatesTo(
            RecordingLazyListUpdateCallback(
                updates = updates,
            ),
        )

        return LazyListDiffResult(
            updates = updates,
            // Always use latest item instances to preserve refreshed closures/session updaters.
            items = next,
            diffResult = diffResult,
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
    private val updates: MutableList<LazyListUpdate>,
) : ListUpdateCallback {
    override fun onInserted(
        position: Int,
        count: Int,
    ) {
        repeat(count) { offset ->
            val index = position + offset
            updates += LazyListUpdate.Insert(index)
        }
    }

    override fun onRemoved(
        position: Int,
        count: Int,
    ) {
        repeat(count) {
            updates += LazyListUpdate.Remove(position)
        }
    }

    override fun onMoved(
        fromPosition: Int,
        toPosition: Int,
    ) {
        updates += LazyListUpdate.Move(fromPosition, toPosition)
    }

    override fun onChanged(
        position: Int,
        count: Int,
        payload: Any?,
    ) {
        repeat(count) { offset ->
            val index = position + offset
            updates += LazyListUpdate.Change(
                index = index,
                payload = payload,
            )
        }
    }
}
