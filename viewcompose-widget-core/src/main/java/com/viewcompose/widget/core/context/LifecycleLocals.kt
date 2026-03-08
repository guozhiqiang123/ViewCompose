package com.viewcompose.widget.core

import androidx.lifecycle.LifecycleOwner

private val LocalLifecycleOwnerValue = uiLocalOf<LifecycleOwner?> { null }

object LocalLifecycleOwner {
    val current: LifecycleOwner?
        get() = UiLocals.current(LocalLifecycleOwnerValue)
}

fun UiTreeBuilder.ProvideLifecycleOwner(
    owner: LifecycleOwner,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(
        local = LocalLifecycleOwnerValue,
        value = owner,
        content = content,
    )
}
