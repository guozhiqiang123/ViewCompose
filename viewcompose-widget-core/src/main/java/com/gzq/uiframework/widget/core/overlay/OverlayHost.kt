package com.gzq.uiframework.widget.core

import android.util.Log
import android.view.View
import java.lang.reflect.Constructor
import java.util.concurrent.atomic.AtomicBoolean

data class OverlaySessionId(
    val value: String,
)

enum class OverlayType {
    Dialog,
    Snackbar,
    Toast,
    Popup,
    ModalBottomSheet,
}

data class OverlayRequest(
    val key: String,
    val type: OverlayType,
    val payload: Any? = null,
    val contentToken: Any? = null,
)

interface OverlayHost {
    fun commit(
        sessionId: OverlaySessionId,
        requests: List<OverlayRequest>,
    )

    fun clear(sessionId: OverlaySessionId)
}

object OverlayHostDefaults {
    private const val TAG = "UIFramework"
    // Reflection contract: keep this class name synchronized with app unit test.
    private const val ANDROID_OVERLAY_HOST_CLASS_NAME =
        "com.gzq.uiframework.overlay.android.host.AndroidOverlayHost"
    private val missingAndroidHostWarningLogged = AtomicBoolean(false)

    private val androidOverlayHostConstructor: Constructor<out OverlayHost>? by lazy {
        resolveAndroidOverlayHostConstructor()
    }

    val noOp: OverlayHost = object : OverlayHost {
        override fun commit(
            sessionId: OverlaySessionId,
            requests: List<OverlayRequest>,
        ) = Unit

        override fun clear(sessionId: OverlaySessionId) = Unit
    }

    fun androidOrNoOp(rootView: View): OverlayHost {
        val constructor = androidOverlayHostConstructor
        if (constructor == null) {
            warnMissingAndroidOverlayHostOnce()
            return noOp
        }
        return runCatching {
            constructor.newInstance(rootView)
        }.getOrElse {
            Log.w(
                TAG,
                "Failed to create AndroidOverlayHost via reflection. Falling back to no-op host.",
                it,
            )
            noOp
        }
    }

    private fun resolveAndroidOverlayHostConstructor(): Constructor<out OverlayHost>? {
        return runCatching {
            val clazz = Class.forName(ANDROID_OVERLAY_HOST_CLASS_NAME)
                .asSubclass(OverlayHost::class.java)
            clazz.getConstructor(View::class.java)
        }.getOrNull()
    }

    private fun warnMissingAndroidOverlayHostOnce() {
        if (!missingAndroidHostWarningLogged.compareAndSet(false, true)) {
            return
        }
        Log.i(
            TAG,
            "AndroidOverlayHost not found on classpath; falling back to no-op overlay host. " +
                "Overlay widgets (Dialog/Popup/Snackbar/Toast/BottomSheet) require ui-overlay-android.",
        )
    }
}

internal val LocalOverlayHost = LocalValue { OverlayHostDefaults.noOp }

object OverlayHostContext {
    val current: OverlayHost
        get() = LocalContext.current(LocalOverlayHost)
}

fun UiTreeBuilder.ProvideOverlayHost(
    host: OverlayHost,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalOverlayHost, host) {
        content()
    }
}
