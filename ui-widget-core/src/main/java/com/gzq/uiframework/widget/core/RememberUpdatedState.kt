package com.gzq.uiframework.widget.core

import com.gzq.uiframework.runtime.State
import com.gzq.uiframework.runtime.mutableStateOf

fun <T> rememberUpdatedState(
    newValue: T,
): State<T> {
    val state = remember {
        mutableStateOf(newValue)
    }
    state.value = newValue
    return state
}
