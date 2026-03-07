package com.viewcompose.overlay.android.host

import android.view.View
import com.viewcompose.overlay.android.presenter.AndroidDialogOverlayPresenter
import com.viewcompose.overlay.android.presenter.AndroidModalBottomSheetPresenter
import com.viewcompose.overlay.android.presenter.AndroidPopupOverlayPresenter
import com.viewcompose.widget.core.DialogOverlayHost
import com.viewcompose.widget.core.ModalBottomSheetOverlayHost
import com.viewcompose.widget.core.OverlayHost
import com.viewcompose.widget.core.OverlayRequest
import com.viewcompose.widget.core.OverlaySessionId
import com.viewcompose.widget.core.PopupOverlayHost

class AndroidOverlayHost(
    rootView: View,
) : OverlayHost {
    private val delegate = CompositeOverlayHost(
        DialogOverlayHost(AndroidDialogOverlayPresenter(rootView)),
        PopupOverlayHost(AndroidPopupOverlayPresenter(rootView)),
        ModalBottomSheetOverlayHost(AndroidModalBottomSheetPresenter(rootView)),
        AndroidTransientFeedbackOverlayHost(rootView),
    )

    override fun commit(
        sessionId: OverlaySessionId,
        requests: List<OverlayRequest>,
    ) {
        delegate.commit(sessionId, requests)
    }

    override fun clear(sessionId: OverlaySessionId) {
        delegate.clear(sessionId)
    }
}

private class CompositeOverlayHost(
    private vararg val delegates: OverlayHost,
) : OverlayHost {
    override fun commit(
        sessionId: OverlaySessionId,
        requests: List<OverlayRequest>,
    ) {
        delegates.forEach { host ->
            host.commit(sessionId, requests)
        }
    }

    override fun clear(sessionId: OverlaySessionId) {
        delegates.forEach { host ->
            host.clear(sessionId)
        }
    }
}
