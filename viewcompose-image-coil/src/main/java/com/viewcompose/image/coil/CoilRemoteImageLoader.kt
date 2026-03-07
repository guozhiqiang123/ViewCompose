package com.viewcompose.image.coil

import android.content.Context
import android.widget.ImageView
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.target
import com.viewcompose.renderer.node.RemoteImageLoader
import com.viewcompose.renderer.node.RemoteImageRequest

class CoilRemoteImageLoader(
    private val imageLoader: ImageLoader,
) : RemoteImageLoader {
    constructor(context: Context) : this(
        imageLoader = ImageLoader.Builder(context).build(),
    )

    override fun load(
        imageView: ImageView,
        request: RemoteImageRequest,
    ) {
        imageLoader.enqueue(
            ImageRequest.Builder(imageView.context)
                .data(request.url)
                .apply {
                    request.placeholderResId?.let { placeholder(it) }
                    request.errorResId?.let { error(it) }
                    request.fallbackResId?.let { fallback(it) }
                }
                .target(imageView)
                .build(),
        )
    }
}
