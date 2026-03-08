package com.viewcompose.ui.state

class PagerState {
    private val listeners = linkedSetOf<(Int, Float) -> Unit>()
    private var connector: PagerConnector? = null

    var currentPage: Int = 0
        private set
    var pageOffset: Float = 0f
        private set

    fun scrollToPage(page: Int) {
        connector?.scrollToPage(page)
    }

    fun updateFromPager(
        currentPage: Int,
        pageOffset: Float,
    ) {
        if (this.currentPage == currentPage && this.pageOffset == pageOffset) {
            return
        }
        this.currentPage = currentPage
        this.pageOffset = pageOffset
        listeners.forEach { listener ->
            listener(currentPage, pageOffset)
        }
    }

    fun addOnPageSnapshotListener(
        listener: (Int, Float) -> Unit,
    ) {
        listeners += listener
    }

    fun removeOnPageSnapshotListener(
        listener: (Int, Float) -> Unit,
    ) {
        listeners -= listener
    }

    fun attach(connector: PagerConnector?) {
        this.connector = connector
    }
}

interface PagerConnector {
    fun scrollToPage(page: Int)
}
