package com.viewcompose.widget.core

import android.view.Choreographer
import java.util.WeakHashMap

internal class AndroidChoreographerFrameClock(
    private val choreographerProvider: () -> Choreographer = { Choreographer.getInstance() },
) : RenderFrameClock {
    private val callbacks = WeakHashMap<RenderFrameCallback, Choreographer.FrameCallback>()

    override fun postFrameCallback(callback: RenderFrameCallback) {
        val frameCallback = callbacks.getOrPut(callback) {
            Choreographer.FrameCallback { frameTimeNanos ->
                callback.doFrame(frameTimeNanos)
            }
        }
        choreographerProvider().postFrameCallback(frameCallback)
    }

    override fun removeFrameCallback(callback: RenderFrameCallback) {
        val frameCallback = callbacks.remove(callback) ?: return
        choreographerProvider().removeFrameCallback(frameCallback)
    }
}
