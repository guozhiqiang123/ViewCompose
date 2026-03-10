package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationConverters
import com.viewcompose.animation.core.tween
import com.viewcompose.runtime.frame.MonotonicFrameClock
import kotlin.math.abs
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimatableTest {
    @Test
    fun `animateTo requires bound frame clock`() = runBlocking {
        val animatable = Animatable(
            initialValue = 0f,
            converter = AnimationConverters.Float,
        )
        var thrown = false
        try {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 32),
            )
        } catch (_: IllegalArgumentException) {
            thrown = true
        }
        assertTrue(thrown)
    }

    @Test
    fun `animateTo reaches target with constructor provided frame clock`() = runBlocking {
        val animatable = Animatable(
            initialValue = 0f,
            converter = AnimationConverters.Float,
            defaultFrameClock = FakeClock(),
        )
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 64),
        )
        assertTrue(abs(animatable.value - 1f) < 0.0001f)
    }

    private class FakeClock(
        private val frameStepNanos: Long = 16_000_000L,
    ) : MonotonicFrameClock {
        private var nowNanos: Long = 0L

        override suspend fun <R> withFrameNanos(
            onFrame: (frameTimeNanos: Long) -> R,
        ): R {
            nowNanos += frameStepNanos
            return onFrame(nowNanos)
        }
    }
}
