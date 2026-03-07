package com.viewcompose.widget.core

class ModalBottomSheetOverlaySpec(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val skipPartiallyExpanded: Boolean = false,
    val scrimOpacity: Float = 0.32f,
    val navigationBarColor: Int? = null,
    val onDismissRequest: (() -> Unit)? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is ModalBottomSheetOverlaySpec) {
            return false
        }
        return dismissOnBackPress == other.dismissOnBackPress &&
            dismissOnClickOutside == other.dismissOnClickOutside &&
            skipPartiallyExpanded == other.skipPartiallyExpanded &&
            scrimOpacity == other.scrimOpacity &&
            navigationBarColor == other.navigationBarColor
    }

    override fun hashCode(): Int {
        var result = dismissOnBackPress.hashCode()
        result = 31 * result + dismissOnClickOutside.hashCode()
        result = 31 * result + skipPartiallyExpanded.hashCode()
        result = 31 * result + scrimOpacity.hashCode()
        result = 31 * result + (navigationBarColor ?: 0)
        return result
    }
}

data class ModalBottomSheetOverlayContent(
    val surface: OverlaySurfaceContent,
)
