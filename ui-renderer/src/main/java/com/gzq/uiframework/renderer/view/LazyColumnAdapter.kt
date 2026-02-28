package com.gzq.uiframework.renderer.view

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.reconcile.LazyListDiff
import com.gzq.uiframework.renderer.reconcile.LazyListUpdate

internal class LazyColumnAdapter : RecyclerView.Adapter<LazyColumnViewHolder>() {
    private var items: List<LazyListItem> = emptyList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LazyColumnViewHolder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        }
        return LazyColumnViewHolder(container)
    }

    override fun onBindViewHolder(
        holder: LazyColumnViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: LazyColumnViewHolder) {
        holder.recycle()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        val key = items[position].key
        return key?.hashCode()?.toLong() ?: position.toLong()
    }

    fun submitItems(items: List<LazyListItem>) {
        val result = LazyListDiff.calculate(
            previous = this.items,
            next = items,
        )
        this.items = result.items
        result.updates.forEach { update ->
            when (update) {
                is LazyListUpdate.Insert -> notifyItemInserted(update.index)
                is LazyListUpdate.Remove -> notifyItemRemoved(update.index)
                is LazyListUpdate.Move -> notifyItemMoved(update.fromIndex, update.toIndex)
                is LazyListUpdate.Change -> notifyItemChanged(update.index)
                LazyListUpdate.ReloadAll -> notifyDataSetChanged()
            }
        }
    }
}

internal class LazyColumnViewHolder(
    private val container: FrameLayout,
) : RecyclerView.ViewHolder(container) {
    private val controller = LazyItemSessionController(
        createSession = { item ->
            item.sessionFactory.create(container)
        },
        clearContainer = container::removeAllViews,
    )

    fun bind(item: LazyListItem) {
        controller.bind(item)
    }

    fun recycle() {
        controller.recycle()
    }
}
