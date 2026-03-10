package com.viewcompose.animation.core

import com.viewcompose.runtime.frame.MonotonicFrameClock
import kotlin.math.abs
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimationEngineTest {
    @Test
    fun `runAnimation reaches target for tween`() = runBlocking {
        val clock = FakeClock()
        var last = 0f
        val result = runAnimation(
            frameClock = clock,
            startValue = 0f,
            endValue = 1f,
            animationSpec = tween(durationMillis = 64),
            converter = AnimationConverters.Float,
        ) { value ->
            last = value
        }
        assertEquals(AnimationRunResult.Completed, result)
        assertTrue(abs(last - 1f) < 0.0001f)
    }

    @Test
    fun `repeatable reverse ends at start value on even iterations`() = runBlocking {
        val clock = FakeClock()
        var last = 0f
        val result = runAnimation(
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
        assertEquals(AnimationRunResult.Completed, result)
        assertTrue(abs(last - 0f) < 0.0001f)
    }

    @Test
    fun `tween delay keeps start value before interpolation`() = runBlocking {
        val clock = FakeClock()
        val samples = mutableListOf<Float>()
        val result = runAnimation(
            frameClock = clock,
            startValue = 0f,
            endValue = 1f,
            animationSpec = tween(
                durationMillis = 64,
                delayMillis = 48,
            ),
            converter = AnimationConverters.Float,
        ) { value ->
            samples += value
        }
        assertEquals(AnimationRunResult.Completed, result)
        assertTrue("Expected delay samples before interpolation", samples.size >= 4)
        assertTrue(abs(samples[0] - 0f) < 0.0001f)
        assertTrue(abs(samples[1] - 0f) < 0.0001f)
        assertTrue(abs(samples[2] - 0f) < 0.0001f)
    }

    @Test
    fun `cancelled animation does not force terminal value`() = runBlocking {
        val clock = SuspendingFakeClock()
        val samples = mutableListOf<Float>()
        lateinit var animationJob: Job
        animationJob = launch {
            runAnimation(
                frameClock = clock,
                startValue = 0f,
                endValue = 1f,
                animationSpec = tween(durationMillis = 320),
                converter = AnimationConverters.Float,
            ) { value ->
                samples += value
                if (samples.size == 1) {
                    animationJob.cancel()
                }
            }
        }
        animationJob.join()
        assertTrue(samples.isNotEmpty())
        assertNotEquals(1f, samples.last(), 0.0001f)
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

    private class SuspendingFakeClock(
        private val frameStepNanos: Long = 16_000_000L,
    ) : MonotonicFrameClock {
        private var nowNanos: Long = 0L

        override suspend fun <R> withFrameNanos(
            onFrame: (frameTimeNanos: Long) -> R,
        ): R {
            delay(1)
            nowNanos += frameStepNanos
            return onFrame(nowNanos)
        }
    }
}
