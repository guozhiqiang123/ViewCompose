package com.gzq.uiframework.renderer.view

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession

internal class LazyItemSessionController(
    private val createSession: (LazyListItem) -> LazyListItemSession,
    private val clearContainer: () -> Unit,
) {
    private var currentKey: Any? = null
    private var currentContentToken: Any? = null
    private var session: LazyListItemSession? = null

    fun bind(item: LazyListItem) {
        if (session == null || currentKey != item.key || currentContentToken != item.contentToken) {
            session?.dispose()
            clearContainer()
            session = createSession(item)
            currentKey = item.key
            currentContentToken = item.contentToken
        }
        session?.render()
    }

    fun recycle() {
        session?.dispose()
        session = null
        currentKey = null
        currentContentToken = null
        clearContainer()
    }
}
