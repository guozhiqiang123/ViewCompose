package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationConverter
import com.viewcompose.animation.core.AnimationSpec
import com.viewcompose.animation.core.runAnimation
import com.viewcompose.animation.core.spring
import com.viewcompose.runtime.MutableState
import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.runtime.frame.MonotonicFrameClock
import com.viewcompose.widget.core.LocalMonotonicFrameClock
import com.viewcompose.widget.core.remember

class Animatable<T>(
    initialValue: T,
    private val converter: AnimationConverter<T>,
    defaultFrameClock: MonotonicFrameClock? = null,
) {
    private val internalState: MutableState<T> = mutableStateOf(initialValue)
    private var boundFrameClock: MonotonicFrameClock? = defaultFrameClock

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
    ) {
        val frameClock = requireNotNull(boundFrameClock) {
            "Animatable has no frame clock. Use rememberAnimatable(...) or pass a clock in constructor."
        }
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

    internal fun bindFrameClock(frameClock: MonotonicFrameClock) {
        boundFrameClock = frameClock
    }
}

fun <T> rememberAnimatable(
    initialValue: T,
    converter: AnimationConverter<T>,
): Animatable<T> {
    val frameClock = LocalMonotonicFrameClock.current
    val animatable = remember(converter) {
        Animatable(
            initialValue = initialValue,
            converter = converter,
            defaultFrameClock = frameClock,
        )
    }
    animatable.bindFrameClock(frameClock)
    return animatable
}
