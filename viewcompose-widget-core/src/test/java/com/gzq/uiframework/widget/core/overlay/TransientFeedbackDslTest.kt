package com.gzq.uiframework.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class TransientFeedbackDslTest {
    @Test
    fun `snackbar submits overlay request when visible`() {
        val store = OverlayRequestStore()

        OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                Snackbar(
                    visible = true,
                    message = "Saved",
                    actionLabel = "Undo",
                    requestKey = "save_snackbar",
                )
            }
        }

        assertEquals(
            listOf(
                OverlayRequest(
                    key = "save_snackbar",
                    type = OverlayType.Snackbar,
                    payload = SnackbarOverlaySpec(
                        message = "Saved",
                        actionLabel = "Undo",
                    ),
                ),
            ),
            store.currentRequests(),
        )
    }

    @Test
    fun `toast submits overlay request when visible`() {
        val store = OverlayRequestStore()

        OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                Toast(
                    visible = true,
                    message = "Copied",
                    duration = ToastDuration.Long,
                    requestKey = "copy_toast",
                )
            }
        }

        assertEquals(
            listOf(
                OverlayRequest(
                    key = "copy_toast",
                    type = OverlayType.Toast,
                    payload = ToastOverlaySpec(
                        message = "Copied",
                        duration = ToastDuration.Long,
                    ),
                ),
            ),
            store.currentRequests(),
        )
    }

    @Test
    fun `transient feedback is ignored when not visible`() {
        val store = OverlayRequestStore()

        OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                Snackbar(
                    visible = false,
                    message = "Saved",
                )
                Toast(
                    visible = false,
                    message = "Copied",
                )
            }
        }

        assertEquals(emptyList<OverlayRequest>(), store.currentRequests())
    }
}
