package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.RemoteImageLoader

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
