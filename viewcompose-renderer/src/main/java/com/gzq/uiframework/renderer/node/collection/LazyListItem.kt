package com.gzq.uiframework.renderer.node

import android.view.ViewGroup

class LazyListItem(
    val key: Any?,
    val contentToken: Any?,
    val sessionFactory: LazyListItemSessionFactory,
    val sessionUpdater: ((LazyListItemSession) -> Unit)? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LazyListItem) return false
        return key == other.key && contentToken == other.contentToken
    }

    override fun hashCode(): Int {
        var result = key?.hashCode() ?: 0
        result = 31 * result + (contentToken?.hashCode() ?: 0)
        return result
    }
}

fun interface LazyListItemSessionFactory {
    fun create(container: ViewGroup): LazyListItemSession
}

interface LazyListItemSession {
    fun render()
    fun dispose()
}
