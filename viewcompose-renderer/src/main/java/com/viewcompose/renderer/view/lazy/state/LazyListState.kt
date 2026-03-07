package com.viewcompose.renderer.view.lazy

import androidx.recyclerview.widget.RecyclerView

class LazyListState {
    internal var recyclerView: RecyclerView? = null

    fun scrollToPosition(index: Int) {
        recyclerView?.scrollToPosition(index)
    }

    fun smoothScrollToPosition(index: Int) {
        recyclerView?.smoothScrollToPosition(index)
    }
}
