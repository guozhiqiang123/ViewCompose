package com.gzq.uiframework.image.coil

import android.content.Context
import android.widget.ImageView
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.target
import com.gzq.uiframework.renderer.node.RemoteImageLoader

class CoilRemoteImageLoader(
    private val imageLoader: ImageLoader,
) : RemoteImageLoader {
    constructor(context: Context) : this(
        imageLoader = ImageLoader.Builder(context).build(),
    )

    override fun load(
        imageView: ImageView,
        url: String,
    ) {
        imageLoader.enqueue(
            ImageRequest.Builder(imageView.context)
                .data(url)
                .target(imageView)
                .build(),
        )
    }
}
