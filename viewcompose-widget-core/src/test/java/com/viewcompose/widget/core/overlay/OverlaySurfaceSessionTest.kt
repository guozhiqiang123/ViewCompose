package com.viewcompose.widget.core

import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.TextNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class OverlaySurfaceSessionTest {
    @Test
    fun `capture overlay surface content keeps overlay host and local snapshot`() {
        val overlayHost = RecordingOverlayHost()
        val localText = uiLocalOf { "unset" }
        lateinit var captured: OverlaySurfaceContent

        buildVNodeTree {
            ProvideOverlayHost(overlayHost) {
                ProvideLocal(localText, "captured-value") {
                    captured = captureOverlaySurfaceContent {
                        Text(UiLocals.current(localText))
                    }
                }
            }
        }

        assertSame(overlayHost, captured.overlayHost())
        val nodes = captured.buildNodes()
        assertEquals(1, nodes.size)
        assertEquals(NodeType.Text, nodes.single().type)
        val textSpec = nodes.single().spec as TextNodeProps
        assertEquals("captured-value", textSpec.text)
    }

    private class RecordingOverlayHost : OverlayHost {
        override fun commit(
            sessionId: OverlaySessionId,
            requests: List<OverlayRequest>,
        ) = Unit

        override fun clear(sessionId: OverlaySessionId) = Unit
    }
}

