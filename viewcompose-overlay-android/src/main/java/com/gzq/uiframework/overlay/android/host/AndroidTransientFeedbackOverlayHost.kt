package com.gzq.uiframework.overlay.android.host

import android.view.View
import com.gzq.uiframework.overlay.android.presenter.AndroidSnackbarOverlayPresenter
import com.gzq.uiframework.overlay.android.presenter.AndroidToastOverlayPresenter
import com.gzq.uiframework.widget.core.OverlayHost
import com.gzq.uiframework.widget.core.TransientFeedbackOverlayHost

class AndroidTransientFeedbackOverlayHost(
    anchorView: View,
) : OverlayHost by TransientFeedbackOverlayHost(
    snackbarPresenter = AndroidSnackbarOverlayPresenter(anchorView),
    toastPresenter = AndroidToastOverlayPresenter(anchorView.context.applicationContext),
)
