package com.viewcompose.widget.core

import android.util.Log
import android.view.ViewGroup
import com.viewcompose.ui.node.VNode

/**
 * Android rendering engine contract loaded from host-android at runtime.
 * widget-core keeps this contract to avoid a direct renderer dependency.
 */
interface CoreRenderEngine {
    fun renderInto(
        container: ViewGroup,
        previousMountedNodes: List<Any>,
        nodes: List<VNode>,
    ): CoreRenderFrame

    fun disposeMounted(
        container: ViewGroup,
        mountedNodes: List<Any>,
    )
}

data class CoreRenderFrame(
    val mountedNodes: List<Any>,
    val renderStats: Any? = null,
    val renderResult: Any? = null,
)

internal object CoreRenderEngineProvider {
    private const val TAG = "ViewCompose"
    private const val ENGINE_CLASS_NAME = "com.viewcompose.host.android.runtime.AndroidCoreRenderEngine"
    private var warnedMissingEngine: Boolean = false

    val engine: CoreRenderEngine by lazy {
        resolveEngine()
    }

    private fun resolveEngine(): CoreRenderEngine {
        return try {
            val clazz = Class.forName(ENGINE_CLASS_NAME)
            val constructor = clazz.getDeclaredConstructor()
            val instance = constructor.newInstance()
            instance as CoreRenderEngine
        } catch (throwable: Throwable) {
            warnMissingEngine(throwable)
            NoOpCoreRenderEngine
        }
    }

    private fun warnMissingEngine(throwable: Throwable) {
        if (warnedMissingEngine) {
            return
        }
        warnedMissingEngine = true
        Log.w(
            TAG,
            "Host render engine not found on classpath, widget-core render sessions will no-op.",
            throwable,
        )
    }

    private object NoOpCoreRenderEngine : CoreRenderEngine {
        override fun renderInto(
            container: ViewGroup,
            previousMountedNodes: List<Any>,
            nodes: List<VNode>,
        ): CoreRenderFrame {
            return CoreRenderFrame(
                mountedNodes = previousMountedNodes,
            )
        }

        override fun disposeMounted(
            container: ViewGroup,
            mountedNodes: List<Any>,
        ) = Unit
    }
}
