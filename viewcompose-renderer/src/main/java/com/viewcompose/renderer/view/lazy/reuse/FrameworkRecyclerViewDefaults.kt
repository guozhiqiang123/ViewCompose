package com.viewcompose.renderer.view.lazy.reuse

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.viewcompose.renderer.R

internal object FrameworkRecyclerViewDefaults {
    private val lazyColumnPool = RecyclerView.RecycledViewPool()
    private val lazyRowPool = RecyclerView.RecycledViewPool()
    private val lazyGridPool = RecyclerView.RecycledViewPool()
    private val horizontalPagerPool = RecyclerView.RecycledViewPool()
    private val verticalPagerPool = RecyclerView.RecycledViewPool()

    fun applyLazyColumnDefaults(
        recyclerView: RecyclerView,
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        applyDefaults(
            recyclerView = recyclerView,
            sharedPool = lazyColumnPool,
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        )
    }

    fun applyLazyRowDefaults(
        recyclerView: RecyclerView,
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        applyDefaults(
            recyclerView = recyclerView,
            sharedPool = lazyRowPool,
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        )
    }

    fun applyLazyGridDefaults(
        recyclerView: RecyclerView,
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        applyDefaults(
            recyclerView = recyclerView,
            sharedPool = lazyGridPool,
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        )
    }

    fun applyHorizontalPagerDefaults(
        recyclerView: RecyclerView,
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        applyDefaults(
            recyclerView = recyclerView,
            sharedPool = horizontalPagerPool,
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        )
    }

    fun applyVerticalPagerDefaults(
        recyclerView: RecyclerView,
        sharePool: Boolean = false,
        disableItemAnimator: Boolean = false,
    ) {
        applyDefaults(
            recyclerView = recyclerView,
            sharedPool = verticalPagerPool,
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        )
    }

    private fun applyDefaults(
        recyclerView: RecyclerView,
        sharedPool: RecyclerView.RecycledViewPool,
        sharePool: Boolean,
        disableItemAnimator: Boolean,
    ) {
        if (disableItemAnimator) {
            recyclerView.itemAnimator = null
        } else if (recyclerView.itemAnimator == null) {
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
        if (sharePool) {
            recyclerView.setRecycledViewPool(sharedPool)
        } else {
            recyclerView.setRecycledViewPool(resolveLocalPool(recyclerView))
        }
    }

    private fun resolveLocalPool(recyclerView: RecyclerView): RecyclerView.RecycledViewPool {
        val existing = recyclerView.getTag(R.id.ui_framework_local_recycled_view_pool)
            as? RecyclerView.RecycledViewPool
        if (existing != null) {
            return existing
        }
        return RecyclerView.RecycledViewPool().also { local ->
            recyclerView.setTag(R.id.ui_framework_local_recycled_view_pool, local)
        }
    }
}
