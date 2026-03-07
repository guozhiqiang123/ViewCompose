package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.RemoteImageLoader

data class IconButtonNodeProps(
    override val contentDescription: String?,
    override val contentScale: ImageContentScale,
    override val tint: Int?,
    override val source: ImageSource?,
    override val placeholder: ImageSource.Resource?,
    override val error: ImageSource.Resource?,
    override val fallback: ImageSource.Resource?,
    override val remoteImageLoader: RemoteImageLoader?,
    val enabled: Boolean,
    val backgroundColor: Int,
    val borderWidth: Int,
    val borderColor: Int,
    val cornerRadius: Int,
    val rippleColor: Int,
    val contentPadding: Int,
) : ImageNodeSpec
