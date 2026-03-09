package com.viewcompose.animation

import com.viewcompose.runtime.mutableStateOf

class MutableTransitionState<S>(
    initialState: S,
) {
    private val currentStateHolder = mutableStateOf(initialState)
    private val targetStateHolder = mutableStateOf(initialState)
    private val idleHolder = mutableStateOf(true)

    var currentState: S
        get() = currentStateHolder.value
        internal set(value) {
            currentStateHolder.value = value
        }

    var targetState: S
        get() = targetStateHolder.value
        set(value) {
            targetStateHolder.value = value
        }

    var isIdle: Boolean
        get() = idleHolder.value
        internal set(value) {
            idleHolder.value = value
        }
}
