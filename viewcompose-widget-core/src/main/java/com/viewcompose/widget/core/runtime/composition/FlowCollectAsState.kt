package com.viewcompose.widget.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
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

fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner = currentLifecycleOwnerOrThrow(),
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    return (this as Flow<T>).collectAsStateWithLifecycle(
        initial = value,
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = minActiveState,
        context = context,
    )
}

fun <T> Flow<T>.collectAsStateWithLifecycle(
    initial: T,
    lifecycleOwner: LifecycleOwner = currentLifecycleOwnerOrThrow(),
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    return collectAsStateWithLifecycle(
        initial = initial,
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = minActiveState,
        context = context,
    )
}

fun <T> Flow<T>.collectAsStateWithLifecycle(
    initial: T,
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    require(minActiveState != Lifecycle.State.INITIALIZED) {
        "minActiveState must be at least CREATED."
    }
    val state = remember {
        mutableStateOf(initial)
    }
    val latestFlow = rememberUpdatedState(this)
    DisposableEffect(this, lifecycle, minActiveState, context) {
        val scope = CoroutineScope(context + SupervisorJob())
        var collectJob: Job? = null

        fun startCollectIfNeeded() {
            if (collectJob?.isActive == true) {
                return
            }
            collectJob = scope.launch(start = CoroutineStart.UNDISPATCHED) {
                latestFlow.value.collect { next ->
                    state.value = next
                }
            }
        }

        fun stopCollectIfNeeded() {
            collectJob?.cancel()
            collectJob = null
        }

        fun syncCollectionWithLifecycle() {
            if (lifecycle.currentState.isAtLeast(minActiveState)) {
                startCollectIfNeeded()
            } else {
                stopCollectIfNeeded()
            }
        }

        val observer = LifecycleEventObserver { _, _ ->
            syncCollectionWithLifecycle()
        }
        lifecycle.addObserver(observer)
        syncCollectionWithLifecycle()

        return@DisposableEffect {
            lifecycle.removeObserver(observer)
            stopCollectIfNeeded()
            scope.cancel()
        }
    }
    return state
}

private fun currentLifecycleOwnerOrThrow(): LifecycleOwner {
    return requireNotNull(LocalLifecycleOwner.current) {
        "No LifecycleOwner found. Use ComponentActivity/Fragment.setUiContent " +
            "or wrap with ProvideLifecycleOwner."
    }
}
