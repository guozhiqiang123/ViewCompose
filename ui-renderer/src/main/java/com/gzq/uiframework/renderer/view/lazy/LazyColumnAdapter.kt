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
    private data class ScrollAnchor(
        val position: Int,
        val offset: Int,
    )

    private var items: List<LazyListItem> = emptyList()
    private val holderRegistry = LazyHolderRegistry<LazyColumnViewHolder> { holder ->
        holder.recycle()
    }
    private var lastIdentityWarning: String? = null
    private var attachedRecyclerView: RecyclerView? = null

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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }

    override fun onViewDetachedFromWindow(holder: LazyColumnViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holderRegistry.onDetached(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerView = null
        disposeAll()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = orientation

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
        val reloadAnchor = if (result.diffResult == null) {
            captureScrollAnchor()
        } else {
            null
        }
        this.items = result.items
        if (result.diffResult != null) {
            result.diffResult.dispatchUpdatesTo(this)
        } else {
            notifyDataSetChanged()
            restoreScrollAnchor(reloadAnchor)
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
        ensureContainerLayoutParams(holder)
        holderRegistry.onBound(holder)
        holder.bind(
            item = items[position],
            payload = payload,
        )
    }

    private fun ensureContainerLayoutParams(holder: LazyColumnViewHolder) {
        val expectedWidth = if (orientation == LinearLayoutManager.HORIZONTAL) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
        val expectedHeight = if (orientation == LinearLayoutManager.HORIZONTAL) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT
        }
        val current = holder.itemView.layoutParams as? RecyclerView.LayoutParams
        if (current?.width == expectedWidth && current.height == expectedHeight) {
            return
        }
        holder.itemView.layoutParams = RecyclerView.LayoutParams(expectedWidth, expectedHeight)
    }

    private fun captureScrollAnchor(): ScrollAnchor? {
        val recyclerView = attachedRecyclerView ?: return null
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return null
        val position = layoutManager.findFirstVisibleItemPosition()
        if (position == RecyclerView.NO_POSITION) {
            return null
        }
        val anchorView = layoutManager.findViewByPosition(position)
        val offset = if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
            (anchorView?.left ?: recyclerView.paddingLeft) - recyclerView.paddingLeft
        } else {
            (anchorView?.top ?: recyclerView.paddingTop) - recyclerView.paddingTop
        }
        return ScrollAnchor(position, offset)
    }

    private fun restoreScrollAnchor(anchor: ScrollAnchor?) {
        val recyclerView = attachedRecyclerView ?: return
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        if (anchor == null || itemCount == 0) {
            return
        }
        if (shouldDeferAnchorRestore(recyclerView, layoutManager)) {
            return
        }
        val targetPosition = anchor.position.coerceIn(0, itemCount - 1)
        recyclerView.post {
            layoutManager.scrollToPositionWithOffset(targetPosition, anchor.offset)
        }
    }

    private fun shouldDeferAnchorRestore(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
    ): Boolean {
        val focusAwareLayoutManager = layoutManager as? LazyLinearLayoutManager ?: return false
        if (!focusAwareLayoutManager.focusAutoScrollEnabled) {
            return false
        }
        return recyclerView.findFocus() != null
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
