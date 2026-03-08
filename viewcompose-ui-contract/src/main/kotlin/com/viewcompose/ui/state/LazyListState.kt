package com.viewcompose.ui.state

class LazyListState {
    private var connector: LazyListConnector? = null

    fun scrollToPosition(index: Int) {
        connector?.scrollToPosition(
            index = index,
            smooth = false,
        )
    }

    fun smoothScrollToPosition(index: Int) {
        connector?.scrollToPosition(
            index = index,
            smooth = true,
        )
    }

    fun attach(connector: LazyListConnector?) {
        this.connector = connector
    }
}

interface LazyListConnector {
    fun scrollToPosition(
        index: Int,
        smooth: Boolean,
    )
}
