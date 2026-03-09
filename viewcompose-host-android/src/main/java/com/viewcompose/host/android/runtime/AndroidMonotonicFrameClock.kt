package com.viewcompose.host.android.runtime

import android.view.Choreographer
import com.viewcompose.runtime.frame.MonotonicFrameClock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidMonotonicFrameClock(
    private val choreographer: Choreographer = Choreographer.getInstance(),
) : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R,
    ): R {
        return suspendCancellableCoroutine { continuation ->
            val callback = Choreographer.FrameCallback { frameTimeNanos ->
                if (!continuation.isActive) {
                    return@FrameCallback
                }
                try {
                    continuation.resume(onFrame(frameTimeNanos))
                } catch (throwable: Throwable) {
                    continuation.resumeWithException(throwable)
                }
            }
            continuation.invokeOnCancellation {
                choreographer.removeFrameCallback(callback)
            }
            choreographer.postFrameCallback(callback)
        }
    }
}
