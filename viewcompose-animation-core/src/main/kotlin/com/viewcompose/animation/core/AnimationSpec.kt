package com.viewcompose.animation.core

sealed interface AnimationSpec

data class TweenSpec(
    val durationMillis: Int = 300,
    val delayMillis: Int = 0,
    val easing: Easing = EasingDefaults.FastOutSlowIn,
) : AnimationSpec

data class SpringSpec(
    val dampingRatio: Float = 0.8f,
    val stiffness: Float = 250f,
    val durationMillis: Int = 550,
) : AnimationSpec

data class Keyframe(
    val timeMillis: Int,
    val valueFraction: Float,
)

data class KeyframesSpec(
    val durationMillis: Int,
    val keyframes: List<Keyframe>,
) : AnimationSpec

data object SnapSpec : AnimationSpec

enum class RepeatMode {
    Restart,
    Reverse,
}

data class RepeatableSpec(
    val iterations: Int,
    val animation: AnimationSpec,
    val repeatMode: RepeatMode = RepeatMode.Restart,
) : AnimationSpec

data class InfiniteRepeatableSpec(
    val animation: AnimationSpec,
    val repeatMode: RepeatMode = RepeatMode.Restart,
) : AnimationSpec

fun tween(
    durationMillis: Int = 300,
    delayMillis: Int = 0,
    easing: Easing = EasingDefaults.FastOutSlowIn,
): TweenSpec = TweenSpec(
    durationMillis = durationMillis,
    delayMillis = delayMillis,
    easing = easing,
)

fun spring(
    dampingRatio: Float = 0.8f,
    stiffness: Float = 250f,
    durationMillis: Int = 550,
): SpringSpec = SpringSpec(
    dampingRatio = dampingRatio,
    stiffness = stiffness,
    durationMillis = durationMillis,
)

fun keyframes(
    durationMillis: Int,
    vararg keyframes: Keyframe,
): KeyframesSpec {
    return KeyframesSpec(
        durationMillis = durationMillis,
        keyframes = keyframes.sortedBy { it.timeMillis },
    )
}

fun keyframe(
    timeMillis: Int,
    valueFraction: Float,
): Keyframe = Keyframe(
    timeMillis = timeMillis,
    valueFraction = valueFraction,
)

fun snap(): SnapSpec = SnapSpec

fun repeatable(
    iterations: Int,
    animation: AnimationSpec,
    repeatMode: RepeatMode = RepeatMode.Restart,
): RepeatableSpec = RepeatableSpec(
    iterations = iterations,
    animation = animation,
    repeatMode = repeatMode,
)

fun infiniteRepeatable(
    animation: AnimationSpec,
    repeatMode: RepeatMode = RepeatMode.Restart,
): InfiniteRepeatableSpec = InfiniteRepeatableSpec(
    animation = animation,
    repeatMode = repeatMode,
)
