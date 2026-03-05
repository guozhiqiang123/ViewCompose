package com.gzq.uiframework.widget.core

interface ModalBottomSheetOverlayHandle {
    fun update(
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    )

    fun dismiss()
}

interface ModalBottomSheetOverlayPresenter {
    fun show(
        entryId: OverlayEntryId,
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    ): ModalBottomSheetOverlayHandle
}

class ModalBottomSheetOverlayHost(
    private val presenter: ModalBottomSheetOverlayPresenter,
) : OverlayHost {
    private val activeRequests = mutableMapOf<OverlayEntryId, ActiveBottomSheetEntry>()

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
            val spec = nextRequest.payload as? ModalBottomSheetOverlaySpec ?: return@forEach
            val content = nextRequest.contentToken as? ModalBottomSheetOverlayContent ?: return@forEach
            val previous = activeRequests[entryId]
            if (previous == null) {
                activeRequests[entryId] = ActiveBottomSheetEntry(
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
        if (type != OverlayType.ModalBottomSheet) {
            return null
        }
        val spec = payload as? ModalBottomSheetOverlaySpec ?: return null
        val content = contentToken as? ModalBottomSheetOverlayContent ?: return null
        val entryId = OverlayEntryId(
            sessionId = sessionId,
            requestKey = key,
        )
        return entryId to copy(
            payload = spec,
            contentToken = content,
        )
    }

    private data class ActiveBottomSheetEntry(
        var request: OverlayRequest,
        val handle: ModalBottomSheetOverlayHandle,
    )
}
