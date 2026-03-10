package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationConverter
import com.viewcompose.animation.core.AnimationConverters
import com.viewcompose.animation.core.InfiniteRepeatableSpec
import com.viewcompose.animation.core.RepeatMode
import com.viewcompose.animation.core.infiniteRepeatable
import com.viewcompose.animation.core.runAnimation
import com.viewcompose.animation.core.tween
import com.viewcompose.runtime.State
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.DisposableEffect
import com.viewcompose.widget.core.LocalAnimationCoroutineContext
import com.viewcompose.widget.core.LocalMonotonicFrameClock
import com.viewcompose.widget.core.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class InfiniteTransition internal constructor()

fun rememberInfiniteTransition(
    label: String = "",
): InfiniteTransition {
    @Suppress("UNUSED_PARAMETER")
    val ignored = label
    return remember { InfiniteTransition() }
}

fun InfiniteTransition.animateFloat(
    initialValue: Float,
    targetValue: Float,
    animationSpec: InfiniteRepeatableSpec = infiniteRepeatable(
        animation = tween(),
    ),
): State<Float> {
    return animateValue(
        initialValue = initialValue,
        targetValue = targetValue,
        converter = AnimationConverters.Float,
        animationSpec = animationSpec,
    )
}

fun InfiniteTransition.animateInt(
    initialValue: Int,
    targetValue: Int,
    animationSpec: InfiniteRepeatableSpec = infiniteRepeatable(
        animation = tween(),
    ),
): State<Int> {
    return animateValue(
        initialValue = initialValue,
        targetValue = targetValue,
        converter = AnimationConverters.Int,
        animationSpec = animationSpec,
    )
}

fun InfiniteTransition.animateColor(
    initialValue: Int,
    targetValue: Int,
    animationSpec: InfiniteRepeatableSpec = infiniteRepeatable(
        animation = tween(),
    ),
): State<Int> {
    return animateValue(
        initialValue = initialValue,
        targetValue = targetValue,
        converter = AnimationConverters.ColorInt,
        animationSpec = animationSpec,
    )
}

fun InfiniteTransition.animateDp(
    initialValue: Int,
    targetValue: Int,
    animationSpec: InfiniteRepeatableSpec = infiniteRepeatable(
        animation = tween(),
    ),
): State<Int> {
    return animateValue(
        initialValue = initialValue,
        targetValue = targetValue,
        converter = AnimationConverters.Int,
        animationSpec = animationSpec,
    )
}

fun <T> InfiniteTransition.animateValue(
    initialValue: T,
    targetValue: T,
    converter: AnimationConverter<T>,
    animationSpec: InfiniteRepeatableSpec = infiniteRepeatable(
        animation = tween(),
    ),
): State<T> {
    val valueState = remember {
        mutableStateOf(initialValue)
    }
    val frameClock = LocalMonotonicFrameClock.current
    val animationCoroutineContext = LocalAnimationCoroutineContext.current
    DisposableEffect(initialValue, targetValue, animationSpec, frameClock, animationCoroutineContext) {
        val scope = CoroutineScope(SupervisorJob() + animationCoroutineContext)
        val job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            var from = initialValue
            var to = targetValue
            while (isActive) {
                runAnimation(
                    frameClock = frameClock,
                    startValue = from,
                    endValue = to,
                    animationSpec = animationSpec.animation,
                    converter = converter,
                ) { next ->
                    valueState.value = next
                }
                if (animationSpec.repeatMode == RepeatMode.Reverse) {
                    val swap = from
                    from = to
                    to = swap
                } else {
                    valueState.value = initialValue
                }
            }
        }
        return@DisposableEffect {
            job.cancel()
            scope.cancel()
        }
    }
    return valueState
}
