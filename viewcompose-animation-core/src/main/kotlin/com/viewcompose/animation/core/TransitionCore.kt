package com.viewcompose.animation.core

class TransitionCore<S>(
    initialState: S,
) {
    var currentState: S = initialState
        private set

    var targetState: S = initialState
        private set

    var segmentInitialState: S = initialState
        private set

    var segmentTargetState: S = initialState
        private set

    var segmentVersion: Long = 0L
        private set

    var playTimeNanos: Long = 0L
        private set

    var segmentDurationNanos: Long = 1L
        private set

    var isRunning: Boolean = false
        private set

    fun updateTarget(target: S) {
        if (target == segmentTargetState) {
            this.targetState = target
            return
        }
        segmentInitialState = if (isRunning) {
            segmentTargetState
        } else {
            currentState
        }
        segmentTargetState = target
        targetState = target
        playTimeNanos = 0L
        segmentDurationNanos = 1L
        segmentVersion += 1L
        isRunning = segmentInitialState != segmentTargetState
        if (!isRunning) {
            currentState = target
        }
    }

    fun registerDuration(durationNanos: Long) {
        if (!isRunning) return
        val normalized = durationNanos.coerceAtLeast(1L)
        if (normalized > segmentDurationNanos) {
            segmentDurationNanos = normalized
        }
    }

    fun updatePlayTime(playTimeNanos: Long) {
        if (!isRunning) return
        this.playTimeNanos = playTimeNanos.coerceAtLeast(0L)
        if (this.playTimeNanos >= segmentDurationNanos) {
            finishRunningSegment()
        }
    }

    fun finishRunningSegment() {
        if (!isRunning) return
        currentState = segmentTargetState
        targetState = segmentTargetState
        playTimeNanos = segmentDurationNanos
        isRunning = false
    }
}
