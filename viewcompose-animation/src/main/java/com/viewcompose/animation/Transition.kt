package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationConverter
import com.viewcompose.animation.core.AnimationConverters
import com.viewcompose.animation.core.AnimationSpec
import com.viewcompose.animation.core.TransitionCore
import com.viewcompose.animation.core.animationDurationNanos
import com.viewcompose.animation.core.sampleAnimationValue
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

class Transition<S> internal constructor(
    initialState: S,
    label: String,
) {
    @Suppress("unused")
    private val transitionLabel: String = label
    private val core = TransitionCore(initialState)
    private val currentStateHolder = mutableStateOf(initialState)
    private val targetStateHolder = mutableStateOf(initialState)
    private val runningHolder = mutableStateOf(false)
    private val segmentVersionHolder = mutableStateOf(0L)
    private val playTimeNanosHolder = mutableStateOf(0L)
    private val segmentDurationNanosHolder = mutableStateOf(1L)
    private val segmentInitialStateHolder = mutableStateOf(initialState)
    private val segmentTargetStateHolder = mutableStateOf(initialState)

    val currentState: S
        get() = currentStateHolder.value

    val targetState: S
        get() = targetStateHolder.value

    val isRunning: Boolean
        get() = runningHolder.value

    internal val segmentInitialState: S
        get() = segmentInitialStateHolder.value

    internal val segmentTargetState: S
        get() = segmentTargetStateHolder.value

    internal val segmentVersion: Long
        get() = segmentVersionHolder.value

    internal val playTimeNanos: Long
        get() = playTimeNanosHolder.value

    internal val segmentDurationNanos: Long
        get() = segmentDurationNanosHolder.value

    internal fun updateTarget(target: S) {
        core.updateTarget(target)
        syncFromCore()
    }

    internal fun registerChannelDuration(durationNanos: Long) {
        core.registerDuration(durationNanos)
        if (segmentDurationNanosHolder.value != core.segmentDurationNanos) {
            segmentDurationNanosHolder.value = core.segmentDurationNanos
        }
    }

    internal fun advanceFrame(version: Long, playTimeNanos: Long) {
        if (core.segmentVersion != version || !core.isRunning) return
        core.updatePlayTime(playTimeNanos)
        syncFromCore()
    }

    internal fun isRunningOn(version: Long): Boolean {
        return core.segmentVersion == version && core.isRunning
    }

    private fun syncFromCore() {
        currentStateHolder.value = core.currentState
        targetStateHolder.value = core.targetState
        runningHolder.value = core.isRunning
        segmentVersionHolder.value = core.segmentVersion
        playTimeNanosHolder.value = core.playTimeNanos
        segmentDurationNanosHolder.value = core.segmentDurationNanos
        segmentInitialStateHolder.value = core.segmentInitialState
        segmentTargetStateHolder.value = core.segmentTargetState
    }

    private class ChannelState<T>(
        var segmentVersion: Long,
        var startValue: T,
        var endValue: T,
        var animationSpec: AnimationSpec,
    )

    private fun <T> animateValueInternal(
        converter: AnimationConverter<T>,
        transitionSpec: (initialState: S, targetState: S) -> AnimationSpec,
        segmentEndpoints: (initialState: S, targetState: S, currentValue: T) -> Pair<T, T>,
        valueForSettledState: (S) -> T,
    ): State<T> {
        val outputState = remember(this, converter) {
            mutableStateOf(valueForSettledState(currentState))
        }
        val channelState = remember(this, converter) {
            ChannelState(
                segmentVersion = -1L,
                startValue = outputState.value,
                endValue = outputState.value,
                animationSpec = tween(),
            )
        }
        val running = isRunning
        val version = segmentVersion
        if (channelState.segmentVersion != version) {
            val spec = transitionSpec(segmentInitialState, segmentTargetState)
            val (start, end) = segmentEndpoints(
                segmentInitialState,
                segmentTargetState,
                outputState.value,
            )
            channelState.segmentVersion = version
            channelState.animationSpec = spec
            channelState.startValue = start
            channelState.endValue = end
        }
        if (running) {
            registerChannelDuration(
                durationNanos = animationDurationNanos(channelState.animationSpec),
            )
            outputState.value = sampleAnimationValue(
                startValue = channelState.startValue,
                endValue = channelState.endValue,
                animationSpec = channelState.animationSpec,
                converter = converter,
                playTimeNanos = playTimeNanos,
            )
        } else {
            outputState.value = valueForSettledState(targetState)
        }
        return outputState
    }

    fun animateFloat(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Float,
    ): State<Float> {
        return animateValueInternal(
            converter = AnimationConverters.Float,
            transitionSpec = { _, _ -> animationSpec() },
            segmentEndpoints = { _, target, current ->
                current to targetValueByState(target)
            },
            valueForSettledState = targetValueByState,
        )
    }

    internal fun animateFloatBySegment(
        transitionSpec: (initialState: S, targetState: S) -> AnimationSpec,
        segmentEndpoints: (initialState: S, targetState: S, currentValue: Float) -> Pair<Float, Float>,
        valueForSettledState: (S) -> Float,
    ): State<Float> {
        return animateValueInternal(
            converter = AnimationConverters.Float,
            transitionSpec = transitionSpec,
            segmentEndpoints = segmentEndpoints,
            valueForSettledState = valueForSettledState,
        )
    }

    fun animateInt(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Int,
    ): State<Int> {
        return animateValueInternal(
            converter = AnimationConverters.Int,
            transitionSpec = { _, _ -> animationSpec() },
            segmentEndpoints = { _, target, current ->
                current to targetValueByState(target)
            },
            valueForSettledState = targetValueByState,
        )
    }

    fun animateColor(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Int,
    ): State<Int> {
        return animateValueInternal(
            converter = AnimationConverters.ColorInt,
            transitionSpec = { _, _ -> animationSpec() },
            segmentEndpoints = { _, target, current ->
                current to targetValueByState(target)
            },
            valueForSettledState = targetValueByState,
        )
    }

    fun animateDp(
        animationSpec: () -> AnimationSpec = { tween() },
        targetValueByState: (S) -> Int,
    ): State<Int> {
        return animateInt(
            animationSpec = animationSpec,
            targetValueByState = targetValueByState,
        )
    }
}

fun <S> updateTransition(
    targetState: S,
    label: String = "",
): Transition<S> {
    val transition = remember {
        Transition(
            initialState = targetState,
            label = label,
        )
    }
    transition.updateTarget(targetState)
    val frameClock = LocalMonotonicFrameClock.current
    val animationCoroutineContext = LocalAnimationCoroutineContext.current
    val running = transition.isRunning
    val segmentVersion = transition.segmentVersion
    DisposableEffect(transition, running, segmentVersion, frameClock, animationCoroutineContext) {
        if (!running) {
            return@DisposableEffect { }
        }
        val scope = CoroutineScope(SupervisorJob() + animationCoroutineContext)
        val launchedVersion = segmentVersion
        val job = scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val startNanos = frameClock.withFrameNanos { it }
            while (isActive && transition.isRunningOn(launchedVersion)) {
                val frameNanos = frameClock.withFrameNanos { it }
                val playTime = (frameNanos - startNanos).coerceAtLeast(0L)
                transition.advanceFrame(
                    version = launchedVersion,
                    playTimeNanos = playTime,
                )
            }
        }
        return@DisposableEffect {
            job.cancel()
            scope.cancel()
        }
    }
    return transition
}
