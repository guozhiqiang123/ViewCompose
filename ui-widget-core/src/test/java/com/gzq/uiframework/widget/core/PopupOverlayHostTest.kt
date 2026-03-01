package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.node.TypedPropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class PopupOverlayHostTest {
    @Test
    fun `shows popup request on first commit`() {
        val presenter = RecordingPopupPresenter()
        val host = PopupOverlayHost(presenter)

        host.commit(
            sessionId = OverlaySessionId("session-1"),
            requests = listOf(
                popupRequest(
                    key = "menu",
                    message = "Menu popup",
                ),
            ),
        )

        assertEquals(
            listOf("show:session-1:menu:Menu popup"),
            presenter.events,
        )
    }

    @Test
    fun `updates popup when content changes`() {
        val presenter = RecordingPopupPresenter()
        val host = PopupOverlayHost(presenter)
        val sessionId = OverlaySessionId("session-1")

        host.commit(
            sessionId = sessionId,
            requests = listOf(
                popupRequest(
                    key = "menu",
                    message = "Menu popup",
                ),
            ),
        )
        host.commit(
            sessionId = sessionId,
            requests = listOf(
                popupRequest(
                    key = "menu",
                    message = "Updated popup",
                ),
            ),
        )

        assertEquals(
            listOf(
                "show:session-1:menu:Menu popup",
                "update:session-1:menu:Updated popup",
            ),
            presenter.events,
        )
    }

    @Test
    fun `dismisses popup removed on next commit`() {
        val presenter = RecordingPopupPresenter()
        val host = PopupOverlayHost(presenter)
        val sessionId = OverlaySessionId("session-1")

        host.commit(
            sessionId = sessionId,
            requests = listOf(
                popupRequest(
                    key = "menu",
                    message = "Menu popup",
                ),
            ),
        )
        host.commit(
            sessionId = sessionId,
            requests = emptyList(),
        )

        assertEquals(
            listOf(
                "show:session-1:menu:Menu popup",
                "dismiss:session-1:menu",
            ),
            presenter.events,
        )
    }

    private fun popupRequest(
        key: String,
        message: String,
    ): OverlayRequest {
        return OverlayRequest(
            key = key,
            type = OverlayType.Popup,
            payload = PopupOverlaySpec(anchorId = "popup_anchor"),
            contentToken = PopupOverlayContent(
                nodes = buildVNodeTree {
                    Text(text = message)
                },
            ),
        )
    }

    private class RecordingPopupPresenter : PopupOverlayPresenter {
        val events = mutableListOf<String>()

        override fun show(
            entryId: OverlayEntryId,
            spec: PopupOverlaySpec,
            content: PopupOverlayContent,
        ): PopupOverlayHandle {
            val textNode = content.nodes.single()
            val message = textNode.props[TypedPropKeys.Text] as? String ?: ""
            events += "show:${entryId.sessionId.value}:${entryId.requestKey}:$message"
            return RecordingPopupHandle(
                entryId = entryId,
                events = events,
            )
        }
    }

    private class RecordingPopupHandle(
        private val entryId: OverlayEntryId,
        private val events: MutableList<String>,
    ) : PopupOverlayHandle {
        override fun update(
            spec: PopupOverlaySpec,
            content: PopupOverlayContent,
        ) {
            val textNode = content.nodes.single()
            val message = textNode.props[TypedPropKeys.Text] as? String ?: ""
            events += "update:${entryId.sessionId.value}:${entryId.requestKey}:$message"
        }

        override fun dismiss() {
            events += "dismiss:${entryId.sessionId.value}:${entryId.requestKey}"
        }
    }
}
