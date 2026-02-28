package com.gzq.uiframework.renderer.node

data class RemoteImageRequest(
    val url: String,
    val placeholderResId: Int? = null,
    val errorResId: Int? = null,
    val fallbackResId: Int? = null,
)
