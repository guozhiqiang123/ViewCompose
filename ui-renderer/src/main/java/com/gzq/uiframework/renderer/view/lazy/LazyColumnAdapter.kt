package com.gzq.uiframework.renderer.view.lazy

import android.util.Log
import android.graphics.Rect
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.reconcile.LazyListDiff
import com.gzq.uiframework.renderer.reconcile.LazyListIdentityInspector
import com.gzq.uiframework.renderer.reconcile.LazyListUpdate

internal class LazyColumnAdapter(
    private val orientation: Int = LinearLayoutManager.VERTICAL,
) : RecyclerView.Adapter<LazyColumnViewHolder>() {
    private var items: List<LazyListItem> = emptyList()
    private val holderRegistry = LazyHolderRegistry<LazyColumnViewHolder> { holder ->
        holder.recycle()
    }
    private var lastIdentityWarning: String? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LazyColumnViewHolder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = if (orientation == LinearLayoutManager.HORIZONTAL) {
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            } else {
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
            }
        }
        return LazyColumnViewHolder(container)
    }

    override fun onBindViewHolder(
        holder: LazyColumnViewHolder,
        position: Int,
    ) {
        holderRegistry.onBound(holder)
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: LazyColumnViewHolder) {
        holderRegistry.onRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: LazyColumnViewHolder) {
        super.onViewAttachedToWindow(holder)
        holderRegistry.onAttached(holder)
    }

    override fun onViewDetachedFromWindow(holder: LazyColumnViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holderRegistry.onDetached(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposeAll()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        val key = items[position].key
        return key?.hashCode()?.toLong() ?: position.toLong()
    }

    fun submitItems(items: List<LazyListItem>) {
        warnAboutIdentityIssues(items)
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
        if (result.updates.isEmpty()) {
            holderRegistry.forEachBound { holder ->
                val position = holder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < this.items.size) {
                    holder.bind(this.items[position])
                }
            }
        }
    }

    private fun warnAboutIdentityIssues(items: List<LazyListItem>) {
        val warning = LazyListIdentityInspector
            .analyze(items)
            .warningMessage(listName = "items")
        if (warning == null) {
            lastIdentityWarning = null
            return
        }
        if (warning == lastIdentityWarning) {
            return
        }
        lastIdentityWarning = warning
        Log.w("UIFramework", warning)
    }

    fun disposeAll() {
        holderRegistry.disposeAll()
        items = emptyList()
    }
}

internal class LazyItemSpacingDecoration(
    private var spacing: Int,
    private val orientation: Int = LinearLayoutManager.VERTICAL,
) : RecyclerView.ItemDecoration() {
    fun updateSpacing(spacing: Int) {
        this.spacing = spacing
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: android.view.View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        if (spacing <= 0) {
            outRect.setEmpty()
            return
        }
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            outRect.setEmpty()
            return
        }
        if (position == 0) {
            outRect.setEmpty()
            return
        }
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect.left = spacing
        } else {
            outRect.top = spacing
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
