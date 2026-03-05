package com.gzq.uiframework.overlay.android.presenter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gzq.uiframework.widget.core.AndroidEnvironmentBridge
import com.gzq.uiframework.widget.core.ModalBottomSheetOverlayContent
import com.gzq.uiframework.widget.core.ModalBottomSheetOverlayHandle
import com.gzq.uiframework.widget.core.ModalBottomSheetOverlayPresenter
import com.gzq.uiframework.widget.core.ModalBottomSheetOverlaySpec
import com.gzq.uiframework.widget.core.OverlayEntryId
import com.gzq.uiframework.widget.core.OverlaySurfaceSession
import com.gzq.uiframework.widget.core.createOverlaySurfaceSession

class AndroidModalBottomSheetPresenter(
    private val rootView: View,
) : ModalBottomSheetOverlayPresenter {
    override fun show(
        entryId: OverlayEntryId,
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    ): ModalBottomSheetOverlayHandle {
        return AndroidModalBottomSheetHandle(
            rootView = rootView,
            spec = spec,
            content = content,
        )
    }
}

private class AndroidModalBottomSheetHandle(
    rootView: View,
    spec: ModalBottomSheetOverlaySpec,
    content: ModalBottomSheetOverlayContent,
) : ModalBottomSheetOverlayHandle {
    private val dialogContainer = FrameLayout(rootView.context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }
    private val dialog = BottomSheetDialog(rootView.context).apply {
        setContentView(dialogContainer)
    }
    private val surfaceSession: OverlaySurfaceSession = createOverlaySurfaceSession(
        container = dialogContainer,
        content = content.surface,
    )
    private var currentSpec = spec
    private var programmaticDismiss = false

    init {
        dialog.setOnDismissListener {
            if (!programmaticDismiss) {
                currentSpec.onDismissRequest?.invoke()
            }
        }
        update(
            spec = spec,
            content = content,
        )
        dialog.show()
    }

    override fun update(
        spec: ModalBottomSheetOverlaySpec,
        content: ModalBottomSheetOverlayContent,
    ) {
        currentSpec = spec
        dialog.setCancelable(spec.dismissOnBackPress)
        dialog.setCanceledOnTouchOutside(spec.dismissOnClickOutside)
        dialog.window?.apply {
            setDimAmount(spec.scrimOpacity.coerceIn(0f, 1f))
        }
        if (spec.skipPartiallyExpanded) {
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.behavior.skipCollapsed = true
        }
        surfaceSession.update(content.surface)
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun dismiss() {
        programmaticDismiss = true
        dialog.setOnDismissListener(null)
        surfaceSession.dispose()
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        programmaticDismiss = false
    }
}
