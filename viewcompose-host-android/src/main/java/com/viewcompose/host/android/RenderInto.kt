package com.viewcompose.host.android

import android.view.ViewGroup
import com.viewcompose.renderer.view.tree.RenderStats
import com.viewcompose.renderer.view.tree.RenderTreeResult
import com.viewcompose.host.android.runtime.ensureAndroidCoreRenderEngineInstalled
import com.viewcompose.widget.core.OverlayHost
import com.viewcompose.widget.core.OverlayHostDefaults
import com.viewcompose.widget.core.UiTreeBuilder

class RenderSession internal constructor(
    private val delegate: com.viewcompose.widget.core.RenderSession,
) {
    fun render() {
        delegate.render()
    }

    fun dispose() {
        delegate.dispose()
    }
}

fun renderInto(
    container: ViewGroup,
    debug: Boolean = false,
    debugTag: String = "ViewCompose",
    overlayHost: OverlayHost = OverlayHostDefaults.noOp,
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.() -> Unit,
): RenderSession {
    ensureAndroidCoreRenderEngineInstalled()
    val session = com.viewcompose.widget.core.RenderSession(
        container = container,
        content = content,
        debug = debug,
        debugTag = debugTag,
        overlayHost = overlayHost,
        onRenderStats = { stats ->
            (stats as? RenderStats)?.let { onRenderStats?.invoke(it) }
        },
        onRenderResult = { result ->
            (result as? RenderTreeResult)?.let { onRenderResult?.invoke(it) }
        },
    )
    session.render()
    return RenderSession(session)
}
