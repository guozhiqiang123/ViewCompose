package com.viewcompose.host.android.runtime

import com.viewcompose.widget.core.RenderSessionRuntime
import java.util.concurrent.atomic.AtomicBoolean

internal class AndroidFrameAlignedRenderSessionRuntime(
    private val onRenderNow: () -> Unit,
    private val onDisposeNow: () -> Unit,
) : RenderSessionRuntime {
    private val disposed = AtomicBoolean(false)
    private val frameDispatcher = FrameAlignedRenderDispatcher(
        frameClock = AndroidChoreographerFrameClock(),
        onFrameRender = {
            if (!disposed.get()) {
                onRenderNow()
            }
        },
    )

    override fun requestRender() {
        if (disposed.get()) return
        frameDispatcher.requestFrame()
    }

    override fun render() {
        if (disposed.get()) return
        frameDispatcher.cancelPending()
        onRenderNow()
    }

    override fun dispose() {
        if (!disposed.compareAndSet(false, true)) return
        frameDispatcher.dispose()
        onDisposeNow()
    }
}
