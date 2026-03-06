package com.gzq.uiframework.widget.core

interface PopupOverlayHandle {
    fun update(
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    )

    fun dismiss()
}

interface PopupOverlayPresenter {
    fun show(
        entryId: OverlayEntryId,
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    ): PopupOverlayHandle
}

class PopupOverlayHost(
    private val presenter: PopupOverlayPresenter,
) : SessionBoundSurfaceOverlayHost<PopupOverlaySpec, PopupOverlayContent, PopupOverlayHandle>(
    overlayType = OverlayType.Popup,
    decode = { request ->
        val spec = request.payload as? PopupOverlaySpec
        val content = request.contentToken as? PopupOverlayContent
        if (spec == null || content == null) {
            null
        } else {
            spec to content
        }
    },
) {
    override fun onShow(
        entryId: OverlayEntryId,
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    ): PopupOverlayHandle = presenter.show(entryId, spec, content)

    override fun onUpdate(
        handle: PopupOverlayHandle,
        spec: PopupOverlaySpec,
        content: PopupOverlayContent,
    ) {
        handle.update(spec, content)
    }

    override fun onDismiss(handle: PopupOverlayHandle) {
        handle.dismiss()
    }
}
