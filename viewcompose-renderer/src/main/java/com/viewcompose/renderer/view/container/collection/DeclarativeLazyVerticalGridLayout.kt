package com.viewcompose.renderer.view.container

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.viewcompose.renderer.R
import com.viewcompose.renderer.node.LazyListItem
import com.viewcompose.renderer.view.lazy.LazyListAdapter
import com.viewcompose.renderer.view.lazy.LazyGridLayoutManager
import com.viewcompose.renderer.view.lazy.LazyGridSpacingDecoration
import com.viewcompose.renderer.view.lazy.LazyFocusFollowLayoutMonitor
import com.viewcompose.renderer.view.lazy.FrameworkRecyclerViewDefaults
import com.viewcompose.renderer.view.lazy.LazyListState
import com.viewcompose.renderer.view.tree.LayoutPassTracker

internal class DeclarativeLazyVerticalGridLayout(
    context: Context,
) : RecyclerView(context) {
    private val gridAdapter = LazyListAdapter(RecyclerView.VERTICAL)
    private var listState: LazyListState? = null

    init {
        adapter = gridAdapter
        applyRecyclerDefaults()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val startNs = System.nanoTime()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LayoutPassTracker.recordMeasure(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val startNs = System.nanoTime()
        super.onLayout(changed, left, top, right, bottom)
        LayoutPassTracker.recordLayout(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    fun bind(
        spanCount: Int,
        contentPadding: Int,
        horizontalSpacing: Int,
        verticalSpacing: Int,
        items: List<LazyListItem>,
        state: LazyListState?,
    ) {
        val lm = layoutManager as? LazyGridLayoutManager
        if (lm == null || lm.spanCount != spanCount) {
            layoutManager = LazyGridLayoutManager(
                context = context,
                spanCount = spanCount,
            )
        }
        updateSpacingDecoration(horizontalSpacing, verticalSpacing, spanCount)
        setPadding(contentPadding, contentPadding, contentPadding, contentPadding)
        clipToPadding = contentPadding == 0
        gridAdapter.submitItems(items)
        if (listState !== state) {
            listState?.recyclerView = null
            listState = state
        }
        listState?.recyclerView = this
    }

    fun dispose() {
        listState?.recyclerView = null
        listState = null
        gridAdapter.disposeAll()
    }

    fun applyRecyclerDefaults(
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        FrameworkRecyclerViewDefaults.applyLazyGridDefaults(
            recyclerView = this,
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        )
    }

    fun setFocusFollowKeyboardEnabled(enabled: Boolean) {
        LazyFocusFollowLayoutMonitor.apply(
            recyclerView = this,
            enabled = enabled,
        )
    }

    private fun updateSpacingDecoration(
        horizontalSpacing: Int,
        verticalSpacing: Int,
        spanCount: Int,
    ) {
        val existing = getTag(R.id.ui_framework_lazy_grid_spacing_decoration)
            as? LazyGridSpacingDecoration
        if (existing != null) {
            existing.update(horizontalSpacing, verticalSpacing, spanCount)
            invalidateItemDecorations()
            return
        }
        val decoration = LazyGridSpacingDecoration(horizontalSpacing, verticalSpacing, spanCount)
        setTag(R.id.ui_framework_lazy_grid_spacing_decoration, decoration)
        addItemDecoration(decoration)
    }
}
