package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.RemoteImageLoader

private val LocalRemoteImageLoader = LocalValue<RemoteImageLoader?> { null }

object ImageLoading {
    val current: RemoteImageLoader?
        get() = LocalContext.current(LocalRemoteImageLoader)
}

fun UiTreeBuilder.ProvideRemoteImageLoader(
    loader: RemoteImageLoader?,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalRemoteImageLoader, loader) {
        content()
    }
}
