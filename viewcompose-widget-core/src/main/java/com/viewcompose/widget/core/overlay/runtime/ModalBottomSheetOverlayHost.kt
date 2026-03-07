package com.viewcompose.widget.core

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
) : SessionBoundSurfaceOverlayHost<
    ModalBottomSheetOverlaySpec,
    ModalBottomSheetOverlayContent,
    ModalBottomSheetOverlayHandle,
>(
    overlayType = OverlayType.ModalBottomSheet,
    decode = { request ->
        val spec = request.payload as? ModalBottomSheetOverlaySpec
        val content = request.contentToken as? ModalBottomSheetOverlayContent
        if (spec == null || content == null) {
            null
        } else {
            spec to content
        }
    },
) {
    override fun onShow(
        entryId: OverlayEntryId,
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    ): ModalBottomSheetOverlayHandle = presenter.show(entryId, spec, content)

    override fun onUpdate(
        handle: ModalBottomSheetOverlayHandle,
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    ) {
        handle.update(spec, content)
    }

    override fun onDismiss(handle: ModalBottomSheetOverlayHandle) {
        handle.dismiss()
    }
}
