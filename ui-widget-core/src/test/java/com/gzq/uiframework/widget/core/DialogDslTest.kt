package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.VNode
import org.junit.Assert.assertEquals
import org.junit.Test

class DialogDslTest {
    @Test
    fun `dialog submits overlay request with content when visible`() {
        val store = OverlayRequestStore()

        val tree = OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                Dialog(
                    visible = true,
                    requestKey = "settings_dialog",
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    position = DialogPosition.Bottom,
                    scrimOpacity = 0.48f,
                ) {
                    Text(text = "Dialog body")
                }
            }
        }

        assertEquals(emptyList<VNode>(), tree)
        assertEquals(
            listOf(
                OverlayRequest(
                    key = "settings_dialog",
                    type = OverlayType.Dialog,
                    payload = DialogOverlaySpec(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false,
                        position = DialogPosition.Bottom,
                        scrimOpacity = 0.48f,
                    ),
                    contentToken = DialogOverlayContent(
                        nodes = buildVNodeTree {
                            Text(text = "Dialog body")
                        },
                    ),
                ),
            ),
            store.currentRequests(),
        )
    }

    @Test
    fun `dialog is ignored when not visible`() {
        val store = OverlayRequestStore()

        val tree = OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                Dialog(visible = false) {
                    Text(text = "Hidden dialog")
                }
            }
        }

        assertEquals(emptyList<VNode>(), tree)
        assertEquals(emptyList<OverlayRequest>(), store.currentRequests())
    }
}
