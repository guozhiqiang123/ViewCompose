package com.gzq.uiframework.renderer.node

sealed interface ImageSource {
    data class Resource(
        val resId: Int,
    ) : ImageSource

    data class Remote(
        val url: String,
    ) : ImageSource
}
