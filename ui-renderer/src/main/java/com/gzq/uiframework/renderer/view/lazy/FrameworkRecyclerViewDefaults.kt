package com.gzq.uiframework.renderer.view.lazy

import androidx.recyclerview.widget.RecyclerView

internal object FrameworkRecyclerViewDefaults {
    private val lazyListPool = RecyclerView.RecycledViewPool()
    private val lazyGridPool = RecyclerView.RecycledViewPool()
    private val pagerPool = RecyclerView.RecycledViewPool()

    fun applyLazyListDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(lazyListPool)
    }

    fun applyLazyGridDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(lazyGridPool)
    }

    fun applyPagerDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(pagerPool)
    }
}
