package com.viewcompose.widget.core

import org.junit.Assert.assertSame
import org.junit.Test

class OverlayHostTest {
    @Test
    fun `overlay host uses no-op host outside provider`() {
        val host = OverlayHostContext.current

        host.commit(
            sessionId = OverlaySessionId("test"),
            requests = listOf(
                OverlayRequest(
                    key = "snackbar",
                    type = OverlayType.Snackbar,
                ),
            ),
        )
        host.clear(OverlaySessionId("test"))

        assertSame(OverlayHostDefaults.noOp, host)
    }

    @Test
    fun `overlay host provider exposes nested values`() {
        val outerHost = RecordingOverlayHost()
        val innerHost = RecordingOverlayHost()
        var outerResolved: OverlayHost? = null
        var innerResolved: OverlayHost? = null
        var restoredResolved: OverlayHost? = null

        buildVNodeTree {
            ProvideOverlayHost(outerHost) {
                outerResolved = OverlayHostContext.current
                ProvideOverlayHost(innerHost) {
                    innerResolved = OverlayHostContext.current
                }
                restoredResolved = OverlayHostContext.current
            }
        }

        assertSame(outerHost, outerResolved)
        assertSame(innerHost, innerResolved)
        assertSame(outerHost, restoredResolved)
        assertSame(OverlayHostDefaults.noOp, OverlayHostContext.current)
    }

    private class RecordingOverlayHost : OverlayHost {
        override fun commit(
            sessionId: OverlaySessionId,
            requests: List<OverlayRequest>,
        ) = Unit

        override fun clear(sessionId: OverlaySessionId) = Unit
    }
}
