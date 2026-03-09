package com.viewcompose.animation

import com.viewcompose.runtime.State

class Transition<S> internal constructor(
    private val targetState: S,
) {
    fun animateFloat(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Float,
    ): State<Float> {
        return animateFloatAsState(
            targetValue = targetValueByState(targetState),
            animationSpec = animationSpec(),
        )
    }

    fun animateInt(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Int,
    ): State<Int> {
        return animateIntAsState(
            targetValue = targetValueByState(targetState),
            animationSpec = animationSpec(),
        )
    }

    fun animateColor(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Int,
    ): State<Int> {
        return animateColorAsState(
            targetValue = targetValueByState(targetState),
            animationSpec = animationSpec(),
        )
    }

    fun animateDp(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Int,
    ): State<Int> {
        return animateDpAsState(
            targetValue = targetValueByState(targetState),
            animationSpec = animationSpec(),
        )
    }
}

fun <S> updateTransition(
    targetState: S,
    label: String = "",
): Transition<S> {
    @Suppress("UNUSED_PARAMETER")
    val ignored = label
    return Transition(targetState = targetState)
}
