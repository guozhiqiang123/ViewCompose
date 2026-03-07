package com.gzq.uiframework.widget.core

enum class DialogPosition {
    Top,
    Center,
    Bottom,
}

class DialogOverlaySpec(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val position: DialogPosition = DialogPosition.Center,
    val scrimOpacity: Float = 0.32f,
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
            dismissOnClickOutside == other.dismissOnClickOutside &&
            position == other.position &&
            scrimOpacity == other.scrimOpacity
    }

    override fun hashCode(): Int {
        var result = dismissOnBackPress.hashCode()
        result = 31 * result + dismissOnClickOutside.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + scrimOpacity.hashCode()
        return result
    }
}

data class DialogOverlayContent(
    val surface: OverlaySurfaceContent,
)
