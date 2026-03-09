package com.viewcompose.animation

import com.viewcompose.runtime.frame.MonotonicFrameClock
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlinx.coroutines.isActive

internal suspend fun MonotonicFrameClock.awaitFrameNanos(): Long {
    return withFrameNanos { it }
}

internal suspend fun <T> runAnimation(
    frameClock: MonotonicFrameClock,
    startValue: T,
    endValue: T,
    animationSpec: AnimationSpec,
    converter: AnimationConverter<T>,
    onValue: (T) -> Unit,
) {
    when (animationSpec) {
        is RepeatableSpec -> {
            var from = startValue
            var to = endValue
            repeat(max(0, animationSpec.iterations)) { iteration ->
                runOneShotAnimation(
                    frameClock = frameClock,
                    startValue = from,
                    endValue = to,
                    animationSpec = animationSpec.animation,
                    converter = converter,
                    onValue = onValue,
                )
                if (animationSpec.repeatMode == RepeatMode.Reverse && iteration < animationSpec.iterations - 1) {
                    val temp = from
                    from = to
                    to = temp
                }
            }
        }

        is InfiniteRepeatableSpec -> {
            var from = startValue
            var to = endValue
            while (kotlin.coroutines.coroutineContext.isActive) {
                runOneShotAnimation(
                    frameClock = frameClock,
                    startValue = from,
                    endValue = to,
                    animationSpec = animationSpec.animation,
                    converter = converter,
                    onValue = onValue,
                )
                if (animationSpec.repeatMode == RepeatMode.Reverse) {
                    val temp = from
                    from = to
                    to = temp
                } else {
                    onValue(startValue)
                }
            }
        }

        else -> {
            runOneShotAnimation(
                frameClock = frameClock,
                startValue = startValue,
                endValue = endValue,
                animationSpec = animationSpec,
                converter = converter,
                onValue = onValue,
            )
        }
    }
}

private suspend fun <T> runOneShotAnimation(
    frameClock: MonotonicFrameClock,
    startValue: T,
    endValue: T,
    animationSpec: AnimationSpec,
    converter: AnimationConverter<T>,
    onValue: (T) -> Unit,
) {
    if (animationSpec is SnapSpec) {
        onValue(endValue)
        return
    }
    val startVector = converter.toVector(startValue)
    val endVector = converter.toVector(endValue)
    val durationNanos = durationNanos(animationSpec).coerceAtLeast(1L)
    val startNanos = frameClock.awaitFrameNanos()
    while (kotlin.coroutines.coroutineContext.isActive) {
        val frameNanos = frameClock.awaitFrameNanos()
        val playNanos = (frameNanos - startNanos).coerceAtLeast(0L)
        val fraction = (playNanos.toDouble() / durationNanos.toDouble()).toFloat().coerceIn(0f, 1f)
        val normalized = interpolateFraction(
            animationSpec = animationSpec,
            fraction = fraction,
        )
        val vector = FloatArray(startVector.size) { index ->
            lerp(
                start = startVector[index],
                stop = endVector.getOrElse(index) { startVector[index] },
                fraction = normalized,
            )
        }
        onValue(converter.fromVector(vector))
        if (fraction >= 1f) {
            break
        }
    }
    onValue(endValue)
}

private fun durationNanos(spec: AnimationSpec): Long {
    return when (spec) {
        is TweenSpec -> ((spec.durationMillis + spec.delayMillis).coerceAtLeast(0)).toLong() * 1_000_000L
        is SpringSpec -> spec.durationMillis.toLong().coerceAtLeast(1L) * 1_000_000L
        is KeyframesSpec -> spec.durationMillis.toLong().coerceAtLeast(1L) * 1_000_000L
        is RepeatableSpec -> durationNanos(spec.animation)
        is InfiniteRepeatableSpec -> durationNanos(spec.animation)
        SnapSpec -> 1L
    }
}

private fun interpolateFraction(
    animationSpec: AnimationSpec,
    fraction: Float,
): Float {
    return when (animationSpec) {
        is TweenSpec -> animationSpec.easing.transform(fraction.coerceIn(0f, 1f)).coerceIn(0f, 1f)

        is SpringSpec -> {
            val t = fraction.coerceIn(0f, 1f)
            val damping = exp((-animationSpec.dampingRatio * 6f * t).toDouble()).toFloat()
            val oscillation = cos((animationSpec.stiffness * 0.06f * t).toDouble()).toFloat()
            (1f - damping * oscillation).coerceIn(0f, 1f)
        }

        is KeyframesSpec -> interpolateKeyframes(
            spec = animationSpec,
            fraction = fraction.coerceIn(0f, 1f),
        )

        is RepeatableSpec -> interpolateFraction(animationSpec.animation, fraction)
        is InfiniteRepeatableSpec -> interpolateFraction(animationSpec.animation, fraction)
        SnapSpec -> 1f
    }
}

private fun interpolateKeyframes(
    spec: KeyframesSpec,
    fraction: Float,
): Float {
    if (spec.keyframes.isEmpty()) {
        return fraction
    }
    val duration = spec.durationMillis.coerceAtLeast(1)
    val time = (duration * fraction).toInt()
    val sorted = spec.keyframes.sortedBy { it.timeMillis }
    val before = sorted.lastOrNull { it.timeMillis <= time } ?: Keyframe(0, 0f)
    val after = sorted.firstOrNull { it.timeMillis >= time } ?: Keyframe(duration, 1f)
    if (after.timeMillis == before.timeMillis) {
        return before.valueFraction.coerceIn(0f, 1f)
    }
    val local = ((time - before.timeMillis).toFloat() / (after.timeMillis - before.timeMillis).toFloat())
        .coerceIn(0f, 1f)
    return lerp(before.valueFraction, after.valueFraction, local).coerceIn(0f, 1f)
}

private fun lerp(
    start: Float,
    stop: Float,
    fraction: Float,
): Float = start + (stop - start) * fraction
