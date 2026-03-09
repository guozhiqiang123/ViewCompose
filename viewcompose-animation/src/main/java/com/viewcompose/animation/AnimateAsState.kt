package com.viewcompose.animation

import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.LocalAnimationCoroutineContext
import com.viewcompose.widget.core.DisposableEffect
import com.viewcompose.widget.core.LocalMonotonicFrameClock
import com.viewcompose.widget.core.remember
import com.viewcompose.widget.core.rememberUpdatedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

fun <T> animateValueAsState(
    targetValue: T,
    converter: AnimationConverter<T>,
    animationSpec: AnimationSpec = tween(),
): State<T> {
    val state = remember {
        mutableStateOf(targetValue)
    }
    val latestTarget = rememberUpdatedState(targetValue)
    val latestSpec = rememberUpdatedState(animationSpec)
    val frameClock = LocalMonotonicFrameClock.current
    val animationCoroutineContext = LocalAnimationCoroutineContext.current
    DisposableEffect(targetValue, animationSpec, converter, frameClock) {
        val scope = CoroutineScope(SupervisorJob() + animationCoroutineContext)
        val job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            runAnimation(
                frameClock = frameClock,
                startValue = state.value,
                endValue = latestTarget.value,
                animationSpec = latestSpec.value,
                converter = converter,
            ) { next ->
                state.value = next
            }
        }
        return@DisposableEffect {
            job.cancel()
            scope.cancel()
        }
    }
    return state
}

fun animateFloatAsState(
    targetValue: Float,
    animationSpec: AnimationSpec = tween(),
): State<Float> {
    return animateValueAsState(
        targetValue = targetValue,
        converter = AnimationConverters.Float,
        animationSpec = animationSpec,
    )
}

fun animateIntAsState(
    targetValue: Int,
    animationSpec: AnimationSpec = tween(),
): State<Int> {
    return animateValueAsState(
        targetValue = targetValue,
        converter = AnimationConverters.Int,
        animationSpec = animationSpec,
    )
}

fun animateColorAsState(
    targetValue: Int,
    animationSpec: AnimationSpec = tween(),
): State<Int> {
    return animateValueAsState(
        targetValue = targetValue,
        converter = AnimationConverters.ColorInt,
        animationSpec = animationSpec,
    )
}

fun animateDpAsState(
    targetValue: Int,
    animationSpec: AnimationSpec = tween(),
): State<Int> {
    return animateIntAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
    )
}
