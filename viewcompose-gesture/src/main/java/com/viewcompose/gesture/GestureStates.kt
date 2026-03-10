package com.viewcompose.gesture

import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.ui.gesture.TransformDelta
import com.viewcompose.widget.core.remember
import com.viewcompose.widget.core.rememberUpdatedState

class DraggableState internal constructor(
    private val onDeltaState: State<(Float) -> Unit>,
) {
    fun dispatchRawDelta(delta: Float) {
        onDeltaState.value(delta)
    }
}

class SwipeableState<T> internal constructor(
    initialValue: T,
) {
    private val currentState = mutableStateOf(initialValue)

    val currentValue: State<T>
        get() = currentState

    fun snapTo(target: T) {
        currentState.value = target
    }

    internal fun updateCurrent(target: T) {
        currentState.value = target
    }
}

class TransformableState internal constructor(
    private val onTransformState: State<(TransformDelta) -> Unit>,
) {
    fun dispatchTransform(delta: TransformDelta) {
        onTransformState.value(delta)
    }
}

fun rememberDraggableState(
    onDelta: (Float) -> Unit,
): DraggableState {
    val latest = rememberUpdatedState(onDelta)
    return remember {
        DraggableState(onDeltaState = latest)
    }
}

fun <T> rememberSwipeableState(
    initialValue: T,
): SwipeableState<T> {
    return remember {
        SwipeableState(initialValue)
    }
}

fun rememberTransformableState(
    onTransformation: (
        zoomChange: Float,
        panChangeX: Float,
        panChangeY: Float,
        rotationChange: Float,
    ) -> Unit,
): TransformableState {
    val latest = rememberUpdatedState(onTransformation)
    val callback = rememberUpdatedState<(TransformDelta) -> Unit> { delta ->
        latest.value(
            delta.zoom,
            delta.panX,
            delta.panY,
            delta.rotation,
        )
    }
    return remember {
        TransformableState(onTransformState = callback)
    }
}
