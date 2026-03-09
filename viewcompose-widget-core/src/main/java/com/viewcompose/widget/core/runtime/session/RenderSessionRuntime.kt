package com.viewcompose.widget.core

import android.util.Log

interface RenderSessionRuntime {
    fun requestRender()

    fun render()

    fun dispose()
}

fun interface RenderSessionRuntimeFactory {
    fun create(
        onRenderNow: () -> Unit,
        onDisposeNow: () -> Unit,
    ): RenderSessionRuntime
}

fun installRenderSessionRuntimeFactory(
    factory: RenderSessionRuntimeFactory,
) {
    RenderSessionRuntimeProvider.install(factory)
}

internal object RenderSessionRuntimeProvider {
    private const val TAG = "ViewCompose"
    @Volatile
    private var installedFactory: RenderSessionRuntimeFactory? = null
    @Volatile
    private var warnedMissingFactory: Boolean = false

    fun create(
        onRenderNow: () -> Unit,
        onDisposeNow: () -> Unit,
    ): RenderSessionRuntime {
        val factory = installedFactory
        if (factory != null) {
            return factory.create(
                onRenderNow = onRenderNow,
                onDisposeNow = onDisposeNow,
            )
        }
        warnMissingFactoryOnce()
        return ImmediateRenderSessionRuntime(
            onRenderNow = onRenderNow,
            onDisposeNow = onDisposeNow,
        )
    }

    fun install(
        factory: RenderSessionRuntimeFactory,
    ) {
        installedFactory = factory
    }

    private fun warnMissingFactoryOnce() {
        if (warnedMissingFactory) {
            return
        }
        warnedMissingFactory = true
        Log.w(
            TAG,
            "RenderSession runtime factory is not installed; falling back to immediate runtime.",
        )
    }

    private class ImmediateRenderSessionRuntime(
        private val onRenderNow: () -> Unit,
        private val onDisposeNow: () -> Unit,
    ) : RenderSessionRuntime {
        private var disposed: Boolean = false
        private var rendering: Boolean = false
        private var pending: Boolean = false

        override fun requestRender() {
            if (disposed) return
            if (rendering) {
                pending = true
                return
            }
            runRenderLoop()
        }

        override fun render() {
            if (disposed) return
            pending = false
            runRenderLoop()
        }

        override fun dispose() {
            if (disposed) return
            disposed = true
            pending = false
            onDisposeNow()
        }

        private fun runRenderLoop() {
            if (disposed || rendering) return
            rendering = true
            try {
                do {
                    pending = false
                    onRenderNow()
                } while (pending && !disposed)
            } finally {
                rendering = false
            }
        }
    }
}

