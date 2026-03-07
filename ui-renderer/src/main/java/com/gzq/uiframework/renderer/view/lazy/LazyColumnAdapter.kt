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
        bindHolder(
            holder = holder,
            position = position,
            payload = null,
        )
    }

    override fun onBindViewHolder(
        holder: LazyColumnViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        bindHolder(
            holder = holder,
            position = position,
            payload = payloads.lastOrNull(),
        )
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
        if (result.diffResult != null) {
            result.diffResult.dispatchUpdatesTo(this)
        } else {
            notifyDataSetChanged()
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

    private fun bindHolder(
        holder: LazyColumnViewHolder,
        position: Int,
        payload: Any?,
    ) {
        holderRegistry.onBound(holder)
        holder.bind(
            item = items[position],
            payload = payload,
        )
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

    fun bind(
        item: LazyListItem,
        payload: Any? = null,
    ) {
        controller.bind(
            item = item,
            payload = payload,
        )
    }

    fun recycle() {
        controller.recycle()
    }
}
