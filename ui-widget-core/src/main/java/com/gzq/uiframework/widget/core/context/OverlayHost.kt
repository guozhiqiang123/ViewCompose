package com.gzq.uiframework.widget.core

data class OverlaySessionId(
    val value: String,
)

enum class OverlayType {
    Dialog,
    Snackbar,
    Toast,
    Popup,
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
    val noOp: OverlayHost = object : OverlayHost {
        override fun commit(
            sessionId: OverlaySessionId,
            requests: List<OverlayRequest>,
        ) = Unit

        override fun clear(sessionId: OverlaySessionId) = Unit
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
