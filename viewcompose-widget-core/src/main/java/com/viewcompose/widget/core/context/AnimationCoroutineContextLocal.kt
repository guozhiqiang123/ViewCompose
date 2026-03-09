package com.viewcompose.widget.core

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private val LocalAnimationCoroutineContextValue = uiLocalOf<CoroutineContext> { EmptyCoroutineContext }

object LocalAnimationCoroutineContext {
    val current: CoroutineContext
        get() = UiLocals.current(LocalAnimationCoroutineContextValue)
}

fun UiTreeBuilder.ProvideAnimationCoroutineContext(
    context: CoroutineContext,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(
        local = LocalAnimationCoroutineContextValue,
        value = context,
        content = content,
    )
}
