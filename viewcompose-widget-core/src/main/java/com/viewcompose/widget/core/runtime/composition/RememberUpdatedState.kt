package com.viewcompose.widget.core

import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf

fun <T> rememberUpdatedState(
    newValue: T,
): State<T> {
    val state = remember {
        mutableStateOf(newValue)
    }
    // 注意：这里在组合阶段写入最新值；若 effect 在同一组合阶段立刻读取，可能观察到旧快照。
    // Note: this writes during composition; an effect that reads immediately in the same composition
    // phase may still observe the previous snapshot value.
    state.value = newValue
    return state
}
