package com.gzq.uiframework.widget.core

internal interface PopupOverlayHandle {
    fun update(
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    )

    fun dismiss()
}

internal interface PopupOverlayPresenter {
    fun show(
        entryId: OverlayEntryId,
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    ): PopupOverlayHandle
}

internal class PopupOverlayHost(
    private val presenter: PopupOverlayPresenter,
) : OverlayHost {
    private val activeRequests = mutableMapOf<OverlayEntryId, ActivePopupEntry>()

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
            activeRequests.remove(entryId)?.handle?.dismiss()
        }

        nextRequests.forEach { (entryId, nextRequest) ->
            val spec = nextRequest.payload as? PopupOverlaySpec ?: return@forEach
            val content = nextRequest.contentToken as? PopupOverlayContent ?: return@forEach
            val previous = activeRequests[entryId]
            if (previous == null) {
                activeRequests[entryId] = ActivePopupEntry(
                    request = nextRequest,
                    handle = presenter.show(
                        entryId = entryId,
                        spec = spec,
                        content = content,
                    ),
                )
                return@forEach
            }
            if (previous.request == nextRequest) {
                return@forEach
            }
            previous.handle.update(
                spec = spec,
                content = content,
            )
            previous.request = nextRequest
        }
    }

    override fun clear(sessionId: OverlaySessionId) {
        val keys = activeRequests.keys.filter { it.sessionId == sessionId }
        keys.forEach { entryId ->
            activeRequests.remove(entryId)?.handle?.dismiss()
        }
    }

    private fun OverlayRequest.toSupportedEntry(
        sessionId: OverlaySessionId,
    ): Pair<OverlayEntryId, OverlayRequest>? {
        if (type != OverlayType.Popup) {
            return null
        }
        val spec = payload as? PopupOverlaySpec ?: return null
        val content = contentToken as? PopupOverlayContent ?: return null
        val entryId = OverlayEntryId(
            sessionId = sessionId,
            requestKey = key,
        )
        return entryId to copy(
            payload = spec,
            contentToken = content,
        )
    }

    private data class ActivePopupEntry(
        var request: OverlayRequest,
        val handle: PopupOverlayHandle,
    )
}
