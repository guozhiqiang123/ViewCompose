package com.viewcompose.renderer.node

import android.widget.ImageView

fun interface RemoteImageLoader {
    fun load(
        imageView: ImageView,
        request: RemoteImageRequest,
    )
}
