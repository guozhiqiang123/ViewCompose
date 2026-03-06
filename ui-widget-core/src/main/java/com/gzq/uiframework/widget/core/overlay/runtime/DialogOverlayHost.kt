package com.gzq.uiframework.widget.core

interface DialogOverlayHandle {
    fun update(
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    )

    fun dismiss()
}

interface DialogOverlayPresenter {
    fun show(
        entryId: OverlayEntryId,
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    ): DialogOverlayHandle
}

class DialogOverlayHost(
    private val presenter: DialogOverlayPresenter,
) : SessionBoundSurfaceOverlayHost<DialogOverlaySpec, DialogOverlayContent, DialogOverlayHandle>(
    overlayType = OverlayType.Dialog,
    decode = { request ->
        val spec = request.payload as? DialogOverlaySpec
        val content = request.contentToken as? DialogOverlayContent
        if (spec == null || content == null) {
            null
        } else {
            spec to content
        }
    },
) {
    override fun onShow(
        entryId: OverlayEntryId,
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    ): DialogOverlayHandle = presenter.show(entryId, spec, content)

    override fun onUpdate(
        handle: DialogOverlayHandle,
        spec: DialogOverlaySpec,
        content: DialogOverlayContent,
    ) {
        handle.update(spec, content)
    }

    override fun onDismiss(handle: DialogOverlayHandle) {
        handle.dismiss()
    }
}
