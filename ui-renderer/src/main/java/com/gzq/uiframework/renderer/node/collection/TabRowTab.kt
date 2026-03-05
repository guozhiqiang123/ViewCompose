package com.gzq.uiframework.renderer.node.collection

import com.gzq.uiframework.renderer.node.LazyListItem

class TabRowTab(
    val item: LazyListItem,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabRowTab) return false
        return item == other.item
    }

    override fun hashCode(): Int = item.hashCode()
}
