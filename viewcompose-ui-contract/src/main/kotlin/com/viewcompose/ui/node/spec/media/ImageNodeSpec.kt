package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.ImageContentScale
import com.viewcompose.ui.node.ImageSource
import com.viewcompose.ui.node.RemoteImageLoader

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
