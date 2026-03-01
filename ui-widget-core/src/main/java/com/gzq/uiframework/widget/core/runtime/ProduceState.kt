package com.gzq.uiframework.widget.core

import com.gzq.uiframework.runtime.State
import com.gzq.uiframework.runtime.mutableStateOf

interface ProduceStateScope<T> {
    var value: T
}

private class ProduceStateScopeImpl<T>(
    private val state: com.gzq.uiframework.runtime.MutableState<T>,
) : ProduceStateScope<T> {
    override var value: T
        get() = state.value
        set(value) {
            state.value = value
        }
}

fun <T> produceState(
    initialValue: T,
    vararg keys: Any?,
    producer: ProduceStateScope<T>.() -> (() -> Unit)?,
): State<T> {
    val state = remember {
        mutableStateOf(initialValue)
    }
    val latestProducer = rememberUpdatedState(producer)
    DisposableEffect(*keys) {
        val scope = ProduceStateScopeImpl(state)
        scope.run(latestProducer.value) ?: {}
    }
    return state
}
