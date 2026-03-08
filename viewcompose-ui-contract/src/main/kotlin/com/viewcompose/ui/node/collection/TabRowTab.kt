package com.viewcompose.ui.node.collection

import com.viewcompose.ui.node.LazyListItem

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
