package com.viewcompose.widget.core

import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.collectAsState(
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    return (this as Flow<T>).collectAsState(
        initial = value,
        context = context,
    )
}

fun <T> Flow<T>.collectAsState(
    initial: T,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    val state = remember {
        mutableStateOf(initial)
    }
    val latestFlow = rememberUpdatedState(this)
    DisposableEffect(this, context) {
        val scope = CoroutineScope(context + SupervisorJob())
        val collectJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            latestFlow.value.collect { next ->
                state.value = next
            }
        }
        return@DisposableEffect {
            collectJob.cancel()
            scope.cancel()
        }
    }
    return state
}
