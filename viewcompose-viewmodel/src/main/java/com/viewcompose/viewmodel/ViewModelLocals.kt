package com.viewcompose.viewmodel

import androidx.lifecycle.ViewModelStoreOwner
import com.viewcompose.widget.core.ProvideLocal
import com.viewcompose.widget.core.UiLocals
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.uiLocalOf

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
