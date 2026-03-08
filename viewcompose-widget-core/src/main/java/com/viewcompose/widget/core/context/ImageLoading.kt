package com.viewcompose.widget.core

import com.viewcompose.ui.node.RemoteImageLoader

private val LocalRemoteImageLoader = uiLocalOf<RemoteImageLoader?> { null }

object ImageLoading {
    val current: RemoteImageLoader?
        get() = UiLocals.current(LocalRemoteImageLoader)
}

fun UiTreeBuilder.ProvideRemoteImageLoader(
    loader: RemoteImageLoader?,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalRemoteImageLoader, loader) {
        content()
    }
}
