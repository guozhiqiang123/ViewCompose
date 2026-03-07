package com.gzq.uiframework.widget.core

enum class PopupAlignment {
    BelowStart,
    BelowCenter,
    BelowEnd,
    AboveStart,
    AboveCenter,
    AboveEnd,
}

class PopupOverlaySpec(
    val anchorId: String,
    val alignment: PopupAlignment = PopupAlignment.BelowStart,
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
            alignment == other.alignment &&
            dismissOnClickOutside == other.dismissOnClickOutside &&
            focusable == other.focusable &&
            offsetX == other.offsetX &&
            offsetY == other.offsetY
    }

    override fun hashCode(): Int {
        var result = anchorId.hashCode()
        result = 31 * result + alignment.hashCode()
        result = 31 * result + dismissOnClickOutside.hashCode()
        result = 31 * result + focusable.hashCode()
        result = 31 * result + offsetX
        result = 31 * result + offsetY
        return result
    }
}

data class PopupOverlayContent(
    val surface: OverlaySurfaceContent,
)
