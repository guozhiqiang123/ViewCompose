package com.viewcompose.widget.core

import android.view.ViewGroup
import com.viewcompose.ui.node.LazyListItemSession
import com.viewcompose.ui.node.RenderContainerHandle
import com.viewcompose.ui.node.nativeContainer

internal class WidgetLazyListItemSession(
    container: RenderContainerHandle,
    localSnapshot: LocalSnapshot,
    content: UiTreeBuilder.() -> Unit,
) : LazyListItemSession {
    private val hostContainer = container.nativeContainer as? ViewGroup
        ?: error("WidgetLazyListItemSession requires an Android ViewGroup container.")
    private var capturedLocals = localSnapshot
    private var renderContent = content
    private var diagnosticsListener = resolveDiagnosticsListener(localSnapshot)
    private val session = RenderSession(
        container = hostContainer,
        content = {
            LocalContext.withSnapshot(capturedLocals) {
                renderContent()
            }
        },
        onRenderResult = { result ->
            diagnosticsListener?.invoke(result)
        },
    )

    override fun render() {
        // Lazy item session bind path must keep immediate render semantics.
        session.render()
    }

    override fun dispose() {
        session.dispose()
    }

    fun updateContent(
        localSnapshot: LocalSnapshot,
        content: UiTreeBuilder.() -> Unit,
    ) {
        capturedLocals = localSnapshot
        renderContent = content
        diagnosticsListener = resolveDiagnosticsListener(localSnapshot)
    }

    private fun resolveDiagnosticsListener(
        snapshot: LocalSnapshot,
    ): ((RenderTreeResult) -> Unit)? {
        @Suppress("UNCHECKED_CAST")
        return snapshot.values[LocalRenderResultListener.holder] as? ((RenderTreeResult) -> Unit)
    }
}
