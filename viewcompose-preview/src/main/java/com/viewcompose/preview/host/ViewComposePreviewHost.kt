package com.viewcompose.preview.host

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.viewcompose.host.android.RenderSession
import com.viewcompose.host.android.renderInto
import com.viewcompose.widget.core.OverlayHost
import com.viewcompose.widget.core.OverlayHostDefaults
import com.viewcompose.widget.core.UiEnvironment
import com.viewcompose.widget.core.UiTheme
import com.viewcompose.widget.core.UiThemeDefaults
import com.viewcompose.widget.core.UiTreeBuilder

enum class PreviewThemeMode {
    Light,
    Dark,
}

@Composable
fun ViewComposePreviewHost(
    modifier: Modifier = Modifier,
    themeMode: PreviewThemeMode = PreviewThemeMode.Light,
    debug: Boolean = false,
    debugTag: String = "ViewComposePreview",
    overlayHost: OverlayHost = OverlayHostDefaults.noOp,
    content: UiTreeBuilder.() -> Unit,
) {
    val renderController = remember { PreviewRenderController() }
    val latestContent = rememberUpdatedState(content)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = { container ->
            renderController.attach(
                container = container,
                config = PreviewRenderConfig(
                    debug = debug,
                    debugTag = debugTag,
                    overlayHost = overlayHost,
                    themeMode = themeMode,
                ),
                content = {
                    UiEnvironment(androidContext = container.context) {
                        UiTheme(
                            tokens = when (themeMode) {
                                PreviewThemeMode.Light -> UiThemeDefaults.light()
                                PreviewThemeMode.Dark -> UiThemeDefaults.dark()
                            },
                        ) {
                            latestContent.value.invoke(this)
                        }
                    }
                },
            )
        },
    )
    DisposableEffect(renderController) {
        onDispose {
            renderController.dispose()
        }
    }
}

private data class PreviewRenderConfig(
    val debug: Boolean,
    val debugTag: String,
    val overlayHost: OverlayHost,
    val themeMode: PreviewThemeMode,
)

private class PreviewRenderController {
    private var attachedContainer: ViewGroup? = null
    private var config: PreviewRenderConfig? = null
    private var session: RenderSession? = null
    private var latestContent: (UiTreeBuilder.() -> Unit)? = null

    fun attach(
        container: ViewGroup,
        config: PreviewRenderConfig,
        content: UiTreeBuilder.() -> Unit,
    ) {
        latestContent = content
        val shouldRecreate = attachedContainer !== container || this.config != config || session == null
        this.config = config
        if (shouldRecreate) {
            session?.dispose()
            attachedContainer = container
            session = renderInto(
                container = container,
                debug = config.debug,
                debugTag = config.debugTag,
                overlayHost = config.overlayHost,
            ) {
                requireNotNull(latestContent).invoke(this)
            }
            return
        }
        session?.render()
    }

    fun dispose() {
        session?.dispose()
        session = null
        attachedContainer = null
        config = null
        latestContent = null
    }
}
