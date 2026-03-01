package com.gzq.uiframework.overlay.android.host

import android.view.View
import com.gzq.uiframework.overlay.android.presenter.AndroidDialogOverlayPresenter
import com.gzq.uiframework.overlay.android.presenter.AndroidPopupOverlayPresenter
import com.gzq.uiframework.widget.core.DialogOverlayHost
import com.gzq.uiframework.widget.core.OverlayHost
import com.gzq.uiframework.widget.core.OverlayRequest
import com.gzq.uiframework.widget.core.OverlaySessionId
import com.gzq.uiframework.widget.core.PopupOverlayHost

class AndroidOverlayHost(
    rootView: View,
) : OverlayHost {
    private val delegate = CompositeOverlayHost(
        DialogOverlayHost(AndroidDialogOverlayPresenter(rootView)),
        PopupOverlayHost(AndroidPopupOverlayPresenter(rootView)),
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
