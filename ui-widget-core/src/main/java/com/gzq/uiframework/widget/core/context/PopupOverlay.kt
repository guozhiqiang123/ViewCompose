package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.VNode

class PopupOverlaySpec(
    val anchorId: String,
    val dismissOnClickOutside: Boolean = true,
    val focusable: Boolean = true,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val onDismissRequest: (() -> Unit)? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is PopupOverlaySpec) {
            return false
        }
        return anchorId == other.anchorId &&
            dismissOnClickOutside == other.dismissOnClickOutside &&
            focusable == other.focusable &&
            offsetX == other.offsetX &&
            offsetY == other.offsetY
    }

    override fun hashCode(): Int {
        var result = anchorId.hashCode()
        result = 31 * result + dismissOnClickOutside.hashCode()
        result = 31 * result + focusable.hashCode()
        result = 31 * result + offsetX
        result = 31 * result + offsetY
        return result
    }
}

data class PopupOverlayContent(
    val nodes: List<VNode>,
)
