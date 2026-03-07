package com.gzq.uiframework.renderer.view.lazy

import androidx.recyclerview.widget.RecyclerView

internal object FrameworkRecyclerViewDefaults {
    private val lazyColumnPool = RecyclerView.RecycledViewPool()
    private val lazyRowPool = RecyclerView.RecycledViewPool()
    private val lazyGridPool = RecyclerView.RecycledViewPool()
    private val horizontalPagerPool = RecyclerView.RecycledViewPool()
    private val verticalPagerPool = RecyclerView.RecycledViewPool()

    fun applyLazyColumnDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(lazyColumnPool)
    }

    fun applyLazyRowDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(lazyRowPool)
    }

    fun applyLazyGridDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(lazyGridPool)
    }

    fun applyHorizontalPagerDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(horizontalPagerPool)
    }

    fun applyVerticalPagerDefaults(recyclerView: RecyclerView) {
        recyclerView.itemAnimator = null
        recyclerView.setRecycledViewPool(verticalPagerPool)
    }
}
