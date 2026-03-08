package com.viewcompose.ui.node

sealed interface ImageSource {
    data class Resource(
        val resId: Int,
    ) : ImageSource

    data class Remote(
        val url: String?,
    ) : ImageSource
}
