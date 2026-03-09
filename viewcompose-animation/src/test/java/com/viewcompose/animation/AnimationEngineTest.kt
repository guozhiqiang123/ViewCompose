package com.viewcompose.animation

import com.viewcompose.runtime.frame.MonotonicFrameClock
import kotlin.math.abs
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimationEngineTest {
    @Test
    fun `runAnimation reaches target for tween`() = runBlocking {
        val clock = FakeClock()
        var last = 0f
        runAnimation(
            frameClock = clock,
            startValue = 0f,
            endValue = 1f,
            animationSpec = tween(durationMillis = 64),
            converter = AnimationConverters.Float,
        ) { value ->
            last = value
        }
        assertTrue(abs(last - 1f) < 0.0001f)
    }

    @Test
    fun `repeatable reverse ends at start value on even iterations`() = runBlocking {
        val clock = FakeClock()
        var last = 0f
        runAnimation(
            frameClock = clock,
            startValue = 0f,
            endValue = 10f,
            animationSpec = repeatable(
                iterations = 2,
                animation = tween(durationMillis = 32),
                repeatMode = RepeatMode.Reverse,
            ),
            converter = AnimationConverters.Float,
        ) { value ->
            last = value
        }
        assertTrue(abs(last - 0f) < 0.0001f)
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
