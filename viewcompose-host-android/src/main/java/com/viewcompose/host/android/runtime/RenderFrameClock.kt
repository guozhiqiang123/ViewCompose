package com.viewcompose.host.android.runtime

internal fun interface RenderFrameCallback {
    fun doFrame(frameTimeNanos: Long)
}

internal interface RenderFrameClock {
    fun postFrameCallback(callback: RenderFrameCallback)

    fun removeFrameCallback(callback: RenderFrameCallback)
}

