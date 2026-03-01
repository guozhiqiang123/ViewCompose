package com.gzq.uiframework.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class TransientFeedbackOverlayHostTest {
    @Test
    fun `shows snackbar request on first commit`() {
        val snackbarPresenter = RecordingSnackbarPresenter()
        val toastPresenter = RecordingToastPresenter()
        val host = TransientFeedbackOverlayHost(snackbarPresenter, toastPresenter)

        host.commit(
            sessionId = OverlaySessionId("session-1"),
            requests = listOf(
                OverlayRequest(
                    key = "snackbar",
                    type = OverlayType.Snackbar,
                    payload = SnackbarOverlaySpec(message = "Saved"),
                ),
            ),
        )

        assertEquals(listOf("show:session-1:snackbar:Saved"), snackbarPresenter.events)
        assertEquals(emptyList<String>(), toastPresenter.events)
    }

    @Test
    fun `does not re-show equal transient feedback request`() {
        val snackbarPresenter = RecordingSnackbarPresenter()
        val toastPresenter = RecordingToastPresenter()
        val host = TransientFeedbackOverlayHost(snackbarPresenter, toastPresenter)
        val request = OverlayRequest(
            key = "toast",
            type = OverlayType.Toast,
            payload = ToastOverlaySpec(message = "Copied"),
        )

        host.commit(OverlaySessionId("session-1"), listOf(request))
        host.commit(OverlaySessionId("session-1"), listOf(request))

        assertEquals(listOf("show:session-1:toast:Copied"), toastPresenter.events)
    }

    @Test
    fun `dismisses removed request on next commit`() {
        val snackbarPresenter = RecordingSnackbarPresenter()
        val toastPresenter = RecordingToastPresenter()
        val host = TransientFeedbackOverlayHost(snackbarPresenter, toastPresenter)
        val sessionId = OverlaySessionId("session-1")

        host.commit(
            sessionId = sessionId,
            requests = listOf(
                OverlayRequest(
                    key = "snackbar",
                    type = OverlayType.Snackbar,
                    payload = SnackbarOverlaySpec(message = "Saved"),
                ),
            ),
        )
        host.commit(sessionId = sessionId, requests = emptyList())

        assertEquals(
            listOf(
                "show:session-1:snackbar:Saved",
                "dismiss:session-1:snackbar",
            ),
            snackbarPresenter.events,
        )
    }

    @Test
    fun `clear only dismisses matching session requests`() {
        val snackbarPresenter = RecordingSnackbarPresenter()
        val toastPresenter = RecordingToastPresenter()
        val host = TransientFeedbackOverlayHost(snackbarPresenter, toastPresenter)

        host.commit(
            sessionId = OverlaySessionId("session-1"),
            requests = listOf(
                OverlayRequest(
                    key = "toast",
                    type = OverlayType.Toast,
                    payload = ToastOverlaySpec(message = "Copied"),
                ),
            ),
        )
        host.commit(
            sessionId = OverlaySessionId("session-2"),
            requests = listOf(
                OverlayRequest(
                    key = "toast",
                    type = OverlayType.Toast,
                    payload = ToastOverlaySpec(message = "Pinned"),
                ),
            ),
        )

        host.clear(OverlaySessionId("session-1"))

        assertEquals(
            listOf(
                "show:session-1:toast:Copied",
                "show:session-2:toast:Pinned",
                "dismiss:session-1:toast",
            ),
            toastPresenter.events,
        )
    }

    private class RecordingSnackbarPresenter : SnackbarOverlayPresenter {
        val events = mutableListOf<String>()

        override fun show(
            entryId: OverlayEntryId,
            spec: SnackbarOverlaySpec,
        ) {
            events += "show:${entryId.sessionId.value}:${entryId.requestKey}:${spec.message}"
        }

        override fun dismiss(entryId: OverlayEntryId) {
            events += "dismiss:${entryId.sessionId.value}:${entryId.requestKey}"
        }
    }

    private class RecordingToastPresenter : ToastOverlayPresenter {
        val events = mutableListOf<String>()

        override fun show(
            entryId: OverlayEntryId,
            spec: ToastOverlaySpec,
        ) {
            events += "show:${entryId.sessionId.value}:${entryId.requestKey}:${spec.message}"
        }

        override fun dismiss(entryId: OverlayEntryId) {
            events += "dismiss:${entryId.sessionId.value}:${entryId.requestKey}"
        }
    }
}
