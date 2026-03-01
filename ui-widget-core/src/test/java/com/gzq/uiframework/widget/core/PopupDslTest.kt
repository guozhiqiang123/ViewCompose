package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.VNode
import org.junit.Assert.assertEquals
import org.junit.Test

class PopupDslTest {
    @Test
    fun `popup submits overlay request with anchor and content when visible`() {
        val store = OverlayRequestStore()

        val tree = OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                Popup(
                    visible = true,
                    anchorId = "feedback_popup_anchor",
                    requestKey = "feedback_popup",
                    alignment = PopupAlignment.AboveEnd,
                    offsetY = 8.dp,
                ) {
                    Text(text = "Popup content")
                }
            }
        }

        assertEquals(emptyList<VNode>(), tree)
        assertEquals(
            listOf(
                OverlayRequest(
                    key = "feedback_popup",
                    type = OverlayType.Popup,
                    payload = PopupOverlaySpec(
                        anchorId = "feedback_popup_anchor",
                        alignment = PopupAlignment.AboveEnd,
                        offsetY = 8.dp,
                    ),
                    contentToken = PopupOverlayContent(
                        nodes = buildVNodeTree {
                            Text(text = "Popup content")
                        },
                    ),
                ),
            ),
            store.currentRequests(),
        )
    }

    @Test
    fun `popup is ignored when not visible`() {
        val store = OverlayRequestStore()

        val tree = OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                Popup(
                    visible = false,
                    anchorId = "feedback_popup_anchor",
                ) {
                    Text(text = "Hidden popup")
                }
            }
        }

        assertEquals(emptyList<VNode>(), tree)
        assertEquals(emptyList<OverlayRequest>(), store.currentRequests())
    }
}
