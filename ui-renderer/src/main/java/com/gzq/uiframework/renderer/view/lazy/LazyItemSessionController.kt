package com.gzq.uiframework.renderer.view.lazy

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.reconcile.LazyListChangePayload

internal class LazyItemSessionController(
    private val createSession: (LazyListItem) -> LazyListItemSession,
    private val clearContainer: () -> Unit,
) {
    private var currentKey: Any? = null
    private var currentContentToken: Any? = null
    private var session: LazyListItemSession? = null

    fun bind(
        item: LazyListItem,
        payload: Any? = null,
    ) {
        if (session == null || currentKey != item.key) {
            session?.dispose()
            clearContainer()
            session = createSession(item)
            currentKey = item.key
            currentContentToken = item.contentToken
            item.sessionUpdater?.invoke(session!!)
        } else if (payload is LazyListChangePayload.ContentTokenChanged) {
            applyContentTokenUpdate(item)
        } else if (currentContentToken == item.contentToken) {
            session?.let { currentSession ->
                item.sessionUpdater?.invoke(currentSession)
            }
        } else if (currentContentToken != item.contentToken) {
            applyContentTokenUpdate(item)
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

    private fun applyContentTokenUpdate(item: LazyListItem) {
        val currentSession = session
        if (currentSession != null && item.sessionUpdater != null) {
            item.sessionUpdater.invoke(currentSession)
            currentContentToken = item.contentToken
        } else {
            currentSession?.dispose()
            clearContainer()
            session = createSession(item)
            currentContentToken = item.contentToken
        }
    }
}
