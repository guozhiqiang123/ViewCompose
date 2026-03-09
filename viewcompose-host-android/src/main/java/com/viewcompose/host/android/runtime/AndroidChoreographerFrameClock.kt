package com.viewcompose.host.android.runtime

import android.view.Choreographer
import java.util.WeakHashMap

internal class AndroidChoreographerFrameClock(
    private val choreographer: Choreographer = Choreographer.getInstance(),
) : RenderFrameClock {
    private val callbacks = WeakHashMap<RenderFrameCallback, Choreographer.FrameCallback>()

    override fun postFrameCallback(callback: RenderFrameCallback) {
        val choreographerCallback = callbacks.getOrPut(callback) {
            Choreographer.FrameCallback { frameTimeNanos ->
                callback.doFrame(frameTimeNanos)
            }
        }
        choreographer.postFrameCallback(choreographerCallback)
    }

    override fun removeFrameCallback(callback: RenderFrameCallback) {
        callbacks.remove(callback)?.let { choreographerCallback ->
            choreographer.removeFrameCallback(choreographerCallback)
        }
    }
}

