package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.VNode

class DialogOverlaySpec(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val onDismissRequest: (() -> Unit)? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is DialogOverlaySpec) {
            return false
        }
        return dismissOnBackPress == other.dismissOnBackPress &&
            dismissOnClickOutside == other.dismissOnClickOutside
    }

    override fun hashCode(): Int {
        var result = dismissOnBackPress.hashCode()
        result = 31 * result + dismissOnClickOutside.hashCode()
        return result
    }
}

data class DialogOverlayContent(
    val nodes: List<VNode>,
)
