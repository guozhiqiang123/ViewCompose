package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter
import com.gzq.uiframework.renderer.view.lazy.LazyGridSpacingDecoration
import com.gzq.uiframework.renderer.view.lazy.FrameworkRecyclerViewDefaults
import com.gzq.uiframework.renderer.view.lazy.LazyListState
import com.gzq.uiframework.renderer.view.tree.LayoutPassTracker

internal class DeclarativeLazyVerticalGridLayout(
    context: Context,
) : FrameLayout(context) {
    private val recyclerView = RecyclerView(context)
    private val adapter = LazyColumnAdapter(RecyclerView.VERTICAL)

    init {
        recyclerView.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        recyclerView.adapter = adapter
        applyRecyclerDefaults()
        addView(recyclerView)
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
        val lm = recyclerView.layoutManager as? GridLayoutManager
        if (lm == null || lm.spanCount != spanCount) {
            recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        }
        updateSpacingDecoration(horizontalSpacing, verticalSpacing, spanCount)
        recyclerView.setPadding(contentPadding, contentPadding, contentPadding, contentPadding)
        recyclerView.clipToPadding = contentPadding == 0
        adapter.submitItems(items)
        state?.recyclerView = recyclerView
    }

    fun dispose() {
        adapter.disposeAll()
    }

    fun applyRecyclerDefaults(
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        FrameworkRecyclerViewDefaults.applyLazyGridDefaults(
            recyclerView = recyclerView,
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        )
    }

    private fun updateSpacingDecoration(
        horizontalSpacing: Int,
        verticalSpacing: Int,
        spanCount: Int,
    ) {
        val existing = recyclerView.getTag(R.id.ui_framework_lazy_grid_spacing_decoration)
            as? LazyGridSpacingDecoration
        if (existing != null) {
            existing.update(horizontalSpacing, verticalSpacing, spanCount)
            recyclerView.invalidateItemDecorations()
            return
        }
        val decoration = LazyGridSpacingDecoration(horizontalSpacing, verticalSpacing, spanCount)
        recyclerView.setTag(R.id.ui_framework_lazy_grid_spacing_decoration, decoration)
        recyclerView.addItemDecoration(decoration)
    }
}
