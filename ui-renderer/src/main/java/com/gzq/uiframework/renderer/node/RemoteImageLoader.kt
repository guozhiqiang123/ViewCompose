package com.gzq.uiframework.renderer.node

import android.widget.ImageView

fun interface RemoteImageLoader {
    fun load(
        imageView: ImageView,
        request: RemoteImageRequest,
    )
}
