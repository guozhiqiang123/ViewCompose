package com.gzq.uiframework.widget.core

internal interface DialogOverlayHandle {
    fun update(
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    )

    fun dismiss()
}

internal interface DialogOverlayPresenter {
    fun show(
        entryId: OverlayEntryId,
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    ): DialogOverlayHandle
}

internal class DialogOverlayHost(
    private val presenter: DialogOverlayPresenter,
) : OverlayHost {
    private val activeRequests = mutableMapOf<OverlayEntryId, ActiveDialogEntry>()

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
            val spec = nextRequest.payload as? DialogOverlaySpec ?: return@forEach
            val content = nextRequest.contentToken as? DialogOverlayContent ?: return@forEach
            val previous = activeRequests[entryId]
            if (previous == null) {
                activeRequests[entryId] = ActiveDialogEntry(
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
        if (type != OverlayType.Dialog) {
            return null
        }
        val spec = payload as? DialogOverlaySpec ?: return null
        val content = contentToken as? DialogOverlayContent ?: return null
        val entryId = OverlayEntryId(
            sessionId = sessionId,
            requestKey = key,
        )
        return entryId to copy(
            payload = spec,
            contentToken = content,
        )
    }

    private data class ActiveDialogEntry(
        var request: OverlayRequest,
        val handle: DialogOverlayHandle,
    )
}
