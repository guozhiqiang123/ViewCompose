package com.viewcompose.animation.core

import com.viewcompose.runtime.frame.MonotonicFrameClock
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlinx.coroutines.isActive

internal suspend fun MonotonicFrameClock.awaitFrameNanos(): Long {
    return withFrameNanos { it }
}

enum class AnimationRunResult {
    Completed,
    Cancelled,
}

suspend fun <T> runAnimation(
    frameClock: MonotonicFrameClock,
    startValue: T,
    endValue: T,
    animationSpec: AnimationSpec,
    converter: AnimationConverter<T>,
    onValue: (T) -> Unit,
): AnimationRunResult {
    when (animationSpec) {
        is RepeatableSpec -> {
            var from = startValue
            var to = endValue
            repeat(max(0, animationSpec.iterations)) { iteration ->
                val result = runOneShotAnimation(
                    frameClock = frameClock,
                    startValue = from,
                    endValue = to,
                    animationSpec = animationSpec.animation,
                    converter = converter,
                    onValue = onValue,
                )
                if (result == AnimationRunResult.Cancelled) {
                    return AnimationRunResult.Cancelled
                }
                if (animationSpec.repeatMode == RepeatMode.Reverse && iteration < animationSpec.iterations - 1) {
                    val temp = from
                    from = to
                    to = temp
                }
            }
            return AnimationRunResult.Completed
        }

        is InfiniteRepeatableSpec -> {
            var from = startValue
            var to = endValue
            while (kotlin.coroutines.coroutineContext.isActive) {
                val result = runOneShotAnimation(
                    frameClock = frameClock,
                    startValue = from,
                    endValue = to,
                    animationSpec = animationSpec.animation,
                    converter = converter,
                    onValue = onValue,
                )
                if (result == AnimationRunResult.Cancelled) {
                    return AnimationRunResult.Cancelled
                }
                if (animationSpec.repeatMode == RepeatMode.Reverse) {
                    val temp = from
                    from = to
                    to = temp
                } else {
                    onValue(startValue)
                }
            }
            return AnimationRunResult.Cancelled
        }

        else -> {
            return runOneShotAnimation(
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
): AnimationRunResult {
    if (animationSpec is SnapSpec) {
        onValue(endValue)
        return AnimationRunResult.Completed
    }
    val startVector = converter.toVector(startValue)
    val endVector = converter.toVector(endValue)
    val timing = timingNanos(animationSpec)
    val startNanos = frameClock.awaitFrameNanos()
    var completed = false
    while (kotlin.coroutines.coroutineContext.isActive) {
        val frameNanos = frameClock.awaitFrameNanos()
        val playNanos = (frameNanos - startNanos).coerceAtLeast(0L)
        val delayedPlayNanos = playNanos - timing.delayNanos
        if (delayedPlayNanos < 0L) {
            onValue(startValue)
            continue
        }
        val fraction = (delayedPlayNanos.toDouble() / timing.durationNanos.toDouble()).toFloat().coerceIn(0f, 1f)
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
            completed = true
            break
        }
    }
    if (completed) {
        onValue(endValue)
        return AnimationRunResult.Completed
    }
    return AnimationRunResult.Cancelled
}

private data class AnimationTimingNanos(
    val delayNanos: Long,
    val durationNanos: Long,
)

private fun timingNanos(spec: AnimationSpec): AnimationTimingNanos {
    return when (spec) {
        is TweenSpec -> AnimationTimingNanos(
            delayNanos = spec.delayMillis.toLong().coerceAtLeast(0L) * 1_000_000L,
            durationNanos = spec.durationMillis.toLong().coerceAtLeast(1L) * 1_000_000L,
        )

        is SpringSpec -> AnimationTimingNanos(
            delayNanos = 0L,
            durationNanos = spec.durationMillis.toLong().coerceAtLeast(1L) * 1_000_000L,
        )

        is KeyframesSpec -> AnimationTimingNanos(
            delayNanos = 0L,
            durationNanos = spec.durationMillis.toLong().coerceAtLeast(1L) * 1_000_000L,
        )

        is RepeatableSpec -> timingNanos(spec.animation)
        is InfiniteRepeatableSpec -> timingNanos(spec.animation)
        SnapSpec -> AnimationTimingNanos(delayNanos = 0L, durationNanos = 1L)
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
