package com.viewcompose.widget.core

import androidx.lifecycle.ViewModelStoreOwner

private val LocalViewModelStoreOwnerValue = uiLocalOf<ViewModelStoreOwner?> { null }

object LocalViewModelStoreOwner {
    val current: ViewModelStoreOwner?
        get() = UiLocals.current(LocalViewModelStoreOwnerValue)
}

fun UiTreeBuilder.ProvideViewModelStoreOwner(
    owner: ViewModelStoreOwner,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(
        local = LocalViewModelStoreOwnerValue,
        value = owner,
        content = content,
    )
}
