package com.viewcompose.overlay.android.presenter

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.viewcompose.widget.core.OverlayEntryId
import com.viewcompose.widget.core.SnackbarDuration
import com.viewcompose.widget.core.SnackbarOverlayPresenter
import com.viewcompose.widget.core.SnackbarOverlaySpec
import com.viewcompose.widget.core.ToastDuration
import com.viewcompose.widget.core.ToastOverlayPresenter
import com.viewcompose.widget.core.ToastOverlaySpec

class AndroidSnackbarOverlayPresenter(
    private val anchorView: View,
) : SnackbarOverlayPresenter {
    private val activeSnackbars = mutableMapOf<OverlayEntryId, Snackbar>()

    override fun show(
        entryId: OverlayEntryId,
        spec: SnackbarOverlaySpec,
    ) {
        activeSnackbars.remove(entryId)?.dismiss()
        val snackbar = Snackbar.make(
            anchorView,
            spec.message,
            spec.duration.toPlatformDuration(),
        ).apply {
            if (!spec.actionLabel.isNullOrBlank()) {
                setAction(spec.actionLabel) {
                    spec.onAction?.invoke()
                }
            }
            addCallback(
                object : Snackbar.Callback() {
                    override fun onDismissed(
                        transientBottomBar: Snackbar?,
                        event: Int,
                    ) {
                        activeSnackbars.remove(entryId)
                        spec.onDismiss?.invoke()
                    }
                },
            )
        }
        activeSnackbars[entryId] = snackbar
        snackbar.show()
    }

    override fun dismiss(entryId: OverlayEntryId) {
        activeSnackbars.remove(entryId)?.dismiss()
    }
}

class AndroidToastOverlayPresenter(
    private val appContext: Context,
) : ToastOverlayPresenter {
    private val activeToasts = mutableMapOf<OverlayEntryId, Pair<Toast, ToastOverlaySpec>>()

    override fun show(
        entryId: OverlayEntryId,
        spec: ToastOverlaySpec,
    ) {
        activeToasts.remove(entryId)?.first?.cancel()
        val toast = Toast.makeText(
            appContext,
            spec.message,
            spec.duration.toPlatformDuration(),
        )
        activeToasts[entryId] = toast to spec
        toast.show()
    }

    override fun dismiss(entryId: OverlayEntryId) {
        val active = activeToasts.remove(entryId) ?: return
        active.first.cancel()
        active.second.onDismiss?.invoke()
    }
}

private fun SnackbarDuration.toPlatformDuration(): Int {
    return when (this) {
        SnackbarDuration.Short -> Snackbar.LENGTH_SHORT
        SnackbarDuration.Long -> Snackbar.LENGTH_LONG
        SnackbarDuration.Indefinite -> Snackbar.LENGTH_INDEFINITE
    }
}

private fun ToastDuration.toPlatformDuration(): Int {
    return when (this) {
        ToastDuration.Short -> Toast.LENGTH_SHORT
        ToastDuration.Long -> Toast.LENGTH_LONG
    }
}
