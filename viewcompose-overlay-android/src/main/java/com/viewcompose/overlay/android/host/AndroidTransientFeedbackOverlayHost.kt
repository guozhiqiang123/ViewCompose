package com.viewcompose.overlay.android.host

import android.view.View
import com.viewcompose.overlay.android.presenter.AndroidSnackbarOverlayPresenter
import com.viewcompose.overlay.android.presenter.AndroidToastOverlayPresenter
import com.viewcompose.widget.core.OverlayHost
import com.viewcompose.widget.core.TransientFeedbackOverlayHost

class AndroidTransientFeedbackOverlayHost(
    anchorView: View,
) : OverlayHost by TransientFeedbackOverlayHost(
    snackbarPresenter = AndroidSnackbarOverlayPresenter(anchorView),
    toastPresenter = AndroidToastOverlayPresenter(anchorView.context.applicationContext),
)
