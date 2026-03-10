package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationConverter
import com.viewcompose.animation.core.AnimationSpec
import com.viewcompose.animation.core.runAnimation
import com.viewcompose.animation.core.spring
import com.viewcompose.runtime.MutableState
import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.runtime.frame.MonotonicFrameClock

class Animatable<T>(
    initialValue: T,
    private val converter: AnimationConverter<T>,
) {
    private val internalState: MutableState<T> = mutableStateOf(initialValue)

    val value: T
        get() = internalState.value

    val asState: State<T>
        get() = internalState

    suspend fun snapTo(targetValue: T) {
        internalState.value = targetValue
    }

    suspend fun animateTo(
        targetValue: T,
        animationSpec: AnimationSpec = spring(),
        frameClock: MonotonicFrameClock,
    ) {
        runAnimation(
            frameClock = frameClock,
            startValue = internalState.value,
            endValue = targetValue,
            animationSpec = animationSpec,
            converter = converter,
        ) { next ->
            internalState.value = next
        }
    }
}
