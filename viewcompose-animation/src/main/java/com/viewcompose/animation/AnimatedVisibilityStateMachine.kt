package com.viewcompose.animation

internal enum class AnimatedVisibilityPhase {
    PreEnter,
    Visible,
    PostExit,
    Idle,
}

internal data class AnimatedVisibilityMachineSnapshot(
    val phase: AnimatedVisibilityPhase,
    val shouldRender: Boolean,
)

internal class AnimatedVisibilityStateMachine(
    initialVisible: Boolean,
) {
    private var shouldRender: Boolean = initialVisible
    private var phase: AnimatedVisibilityPhase = if (initialVisible) {
        AnimatedVisibilityPhase.Visible
    } else {
        AnimatedVisibilityPhase.Idle
    }

    fun beforeAnimation(targetVisible: Boolean): AnimatedVisibilityMachineSnapshot {
        when {
            targetVisible && !shouldRender -> {
                shouldRender = true
                phase = AnimatedVisibilityPhase.PreEnter
            }

            !targetVisible && shouldRender -> {
                phase = AnimatedVisibilityPhase.PostExit
            }

            targetVisible && shouldRender && phase == AnimatedVisibilityPhase.Idle -> {
                phase = AnimatedVisibilityPhase.PreEnter
            }
        }
        return snapshot()
    }

    fun afterAnimation(
        targetVisible: Boolean,
        enterFinished: Boolean,
        exitFinished: Boolean,
    ): AnimatedVisibilityMachineSnapshot {
        when {
            targetVisible -> {
                if (!shouldRender) {
                    shouldRender = true
                    phase = AnimatedVisibilityPhase.PreEnter
                } else if (enterFinished) {
                    phase = AnimatedVisibilityPhase.Visible
                } else {
                    phase = AnimatedVisibilityPhase.PreEnter
                }
            }

            shouldRender && exitFinished -> {
                shouldRender = false
                phase = AnimatedVisibilityPhase.Idle
            }

            shouldRender -> {
                phase = AnimatedVisibilityPhase.PostExit
            }

            else -> {
                phase = AnimatedVisibilityPhase.Idle
            }
        }
        return snapshot()
    }

    private fun snapshot(): AnimatedVisibilityMachineSnapshot {
        return AnimatedVisibilityMachineSnapshot(
            phase = phase,
            shouldRender = shouldRender,
        )
    }
}
