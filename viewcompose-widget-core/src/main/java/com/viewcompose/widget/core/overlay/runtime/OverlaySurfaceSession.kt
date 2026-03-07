package com.viewcompose.widget.core

import android.view.ViewGroup
import com.viewcompose.renderer.node.VNode

class OverlaySurfaceContent internal constructor(
    private val localSnapshot: LocalSnapshot,
    private val overlayHost: OverlayHost,
    private val content: UiTreeBuilder.() -> Unit,
) {
    internal fun renderInto(builder: UiTreeBuilder) {
        LocalContext.withSnapshot(localSnapshot) {
            with(builder) {
                content()
            }
        }
    }

    internal fun buildNodes(): List<VNode> {
        var nodes: List<VNode> = emptyList()
        LocalContext.withSnapshot(localSnapshot) {
            nodes = buildVNodeTree(content)
        }
        return nodes
    }

    internal fun overlayHost(): OverlayHost = overlayHost
}

class OverlaySurfaceSession internal constructor(
    container: ViewGroup,
    initialContent: OverlaySurfaceContent,
) {
    private var currentContent = initialContent
    private val overlayHostDelegate = MutableOverlayHost(initialContent.overlayHost())
    private val renderSession = RenderSession(
        container = container,
        content = {
            currentContent.renderInto(this)
        },
        overlayHost = overlayHostDelegate,
    )

    init {
        renderSession.render()
    }

    fun update(content: OverlaySurfaceContent) {
        currentContent = content
        overlayHostDelegate.delegate = content.overlayHost()
        renderSession.render()
    }

    fun dispose() {
        renderSession.dispose()
    }
}

internal fun captureOverlaySurfaceContent(
    content: UiTreeBuilder.() -> Unit,
): OverlaySurfaceContent {
    return OverlaySurfaceContent(
        localSnapshot = LocalContext.snapshot(),
        overlayHost = OverlayHostContext.current,
        content = content,
    )
}

fun createOverlaySurfaceSession(
    container: ViewGroup,
    content: OverlaySurfaceContent,
): OverlaySurfaceSession {
    return OverlaySurfaceSession(
        container = container,
        initialContent = content,
    )
}

private class MutableOverlayHost(
    var delegate: OverlayHost,
) : OverlayHost {
    override fun commit(
        sessionId: OverlaySessionId,
        requests: List<OverlayRequest>,
    ) {
        delegate.commit(sessionId, requests)
    }

    override fun clear(sessionId: OverlaySessionId) {
        delegate.clear(sessionId)
    }
}
