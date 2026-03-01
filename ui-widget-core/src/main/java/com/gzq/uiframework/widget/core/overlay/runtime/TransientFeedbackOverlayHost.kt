package com.gzq.uiframework.widget.core

data class OverlayEntryId(
    val sessionId: OverlaySessionId,
    val requestKey: String,
)

interface SnackbarOverlayPresenter {
    fun show(
        entryId: OverlayEntryId,
        spec: SnackbarOverlaySpec,
    )

    fun dismiss(entryId: OverlayEntryId)
}

interface ToastOverlayPresenter {
    fun show(
        entryId: OverlayEntryId,
        spec: ToastOverlaySpec,
    )

    fun dismiss(entryId: OverlayEntryId)
}

class TransientFeedbackOverlayHost(
    private val snackbarPresenter: SnackbarOverlayPresenter,
    private val toastPresenter: ToastOverlayPresenter,
) : OverlayHost {
    private val activeRequests = mutableMapOf<OverlayEntryId, OverlayRequest>()

    override fun commit(
        sessionId: OverlaySessionId,
        requests: List<OverlayRequest>,
    ) {
        val nextRequests = requests.mapNotNull { request ->
            request.toSupportedEntry(sessionId)
        }.associateBy(
            keySelector = { it.first },
            valueTransform = { it.second },
        )
        val previousKeys = activeRequests.keys.filter { it.sessionId == sessionId }

        previousKeys.filter { it !in nextRequests.keys }.forEach { entryId ->
            activeRequests.remove(entryId)?.let { request ->
                dismiss(
                    entryId = entryId,
                    request = request,
                )
            }
        }

        nextRequests.forEach { (entryId, nextRequest) ->
            val previous = activeRequests[entryId]
            if (previous == nextRequest) {
                return@forEach
            }
            if (previous != null && previous.type != nextRequest.type) {
                dismiss(
                    entryId = entryId,
                    request = previous,
                )
            }
            show(
                entryId = entryId,
                request = nextRequest,
            )
            activeRequests[entryId] = nextRequest
        }
    }

    override fun clear(sessionId: OverlaySessionId) {
        val keys = activeRequests.keys.filter { it.sessionId == sessionId }
        keys.forEach { entryId ->
            activeRequests.remove(entryId)?.let { request ->
                dismiss(
                    entryId = entryId,
                    request = request,
                )
            }
        }
    }

    private fun show(
        entryId: OverlayEntryId,
        request: OverlayRequest,
    ) {
        when (request.type) {
            OverlayType.Snackbar -> {
                val spec = request.payload as? SnackbarOverlaySpec ?: return
                snackbarPresenter.show(entryId, spec)
            }

            OverlayType.Toast -> {
                val spec = request.payload as? ToastOverlaySpec ?: return
                toastPresenter.show(entryId, spec)
            }

            OverlayType.Dialog,
            OverlayType.Popup,
            -> Unit
        }
    }

    private fun dismiss(
        entryId: OverlayEntryId,
        request: OverlayRequest,
    ) {
        when (request.type) {
            OverlayType.Snackbar -> snackbarPresenter.dismiss(entryId)
            OverlayType.Toast -> toastPresenter.dismiss(entryId)
            OverlayType.Dialog,
            OverlayType.Popup,
            -> Unit
        }
    }

    private fun OverlayRequest.toSupportedEntry(sessionId: OverlaySessionId): Pair<OverlayEntryId, OverlayRequest>? {
        return when (type) {
            OverlayType.Snackbar -> {
                val spec = payload as? SnackbarOverlaySpec ?: return null
                val entryId = OverlayEntryId(
                    sessionId = sessionId,
                    requestKey = key,
                )
                entryId to copy(payload = spec, contentToken = contentToken ?: spec)
            }

            OverlayType.Toast -> {
                val spec = payload as? ToastOverlaySpec ?: return null
                val entryId = OverlayEntryId(
                    sessionId = sessionId,
                    requestKey = key,
                )
                entryId to copy(payload = spec, contentToken = contentToken ?: spec)
            }

            OverlayType.Dialog,
            OverlayType.Popup,
            -> null
        }
    }
}
