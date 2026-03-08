package com.viewcompose.ui.node

fun interface RemoteImageLoader {
    fun load(
        target: RemoteImageTarget,
        request: RemoteImageRequest,
    )
}

interface RemoteImageTarget

interface PlatformRemoteImageTarget : RemoteImageTarget {
    val target: Any
}
