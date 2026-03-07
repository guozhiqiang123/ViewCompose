package com.viewcompose.widget.core

import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf

fun <T> rememberUpdatedState(
    newValue: T,
): State<T> {
    val state = remember {
        mutableStateOf(newValue)
    }
    state.value = newValue
    return state
}
