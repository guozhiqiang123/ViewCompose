package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.node.ImageContentScale
import com.viewcompose.renderer.node.ImageSource
import com.viewcompose.renderer.node.RemoteImageLoader

interface ImageNodeSpec : NodeSpec {
    val contentDescription: String?
    val contentScale: ImageContentScale
    val tint: Int?
    val source: ImageSource?
    val placeholder: ImageSource.Resource?
    val error: ImageSource.Resource?
    val fallback: ImageSource.Resource?
    val remoteImageLoader: RemoteImageLoader?
}
