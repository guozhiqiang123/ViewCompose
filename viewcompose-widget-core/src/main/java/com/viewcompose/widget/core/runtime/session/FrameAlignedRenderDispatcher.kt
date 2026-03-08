package com.viewcompose.widget.core

import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

internal class FrameAlignedRenderDispatcher(
    private val frameClock: RenderFrameClock,
    private val onFrameRender: () -> Unit,
    private val mainHandler: Handler = Handler(Looper.getMainLooper()),
    private val isMainThread: () -> Boolean = { Looper.myLooper() == Looper.getMainLooper() },
) {
    private val disposed = AtomicBoolean(false)
    private val frameRequested = AtomicBoolean(false)

    private val frameCallback = RenderFrameCallback {
        if (disposed.get()) {
            frameRequested.set(false)
            return@RenderFrameCallback
        }
        frameRequested.set(false)
        onFrameRender()
    }

    private val requestOnMain = Runnable {
        if (disposed.get()) {
            frameRequested.set(false)
            return@Runnable
        }
        frameClock.postFrameCallback(frameCallback)
    }

    private val cancelOnMain = Runnable {
        if (frameRequested.compareAndSet(true, false)) {
            frameClock.removeFrameCallback(frameCallback)
        }
    }

    fun requestFrame() {
        if (disposed.get()) return
        if (!frameRequested.compareAndSet(false, true)) return
        if (isMainThread()) {
            requestOnMain.run()
        } else {
            mainHandler.post(requestOnMain)
        }
    }

    fun cancelPending() {
        if (!frameRequested.get()) return
        if (isMainThread()) {
            cancelOnMain.run()
        } else {
            mainHandler.post(cancelOnMain)
        }
    }

    fun dispose() {
        if (!disposed.compareAndSet(false, true)) return
        cancelPending()
    }
}
