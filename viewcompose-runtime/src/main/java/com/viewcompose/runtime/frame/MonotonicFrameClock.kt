package com.viewcompose.runtime.frame

/**
 * Platform-agnostic frame clock contract used by animation/gesture runtime.
 */
interface MonotonicFrameClock {
    suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R,
    ): R
}

object FallbackMonotonicFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R,
    ): R {
        return onFrame(System.nanoTime())
    }
}
