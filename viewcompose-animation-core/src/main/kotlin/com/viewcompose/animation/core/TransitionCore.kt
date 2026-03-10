package com.viewcompose.animation.core

class TransitionCore<S>(
    initialState: S,
) {
    var currentState: S = initialState
        private set

    var targetState: S = initialState
        private set

    var isRunning: Boolean = false
        private set

    fun updateTarget(target: S) {
        targetState = target
        isRunning = currentState != targetState
    }

    fun markCurrent(state: S) {
        currentState = state
        isRunning = currentState != targetState
    }
}
