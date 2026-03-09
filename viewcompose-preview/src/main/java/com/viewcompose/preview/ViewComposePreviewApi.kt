package com.viewcompose.preview

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.viewcompose.preview.host.PreviewThemeMode
import com.viewcompose.preview.host.ViewComposePreviewHost
import com.viewcompose.widget.core.UiTreeBuilder

enum class ViewComposePreviewTheme {
    Light,
    Dark,
}

data class ViewComposePreviewOptions(
    val theme: ViewComposePreviewTheme = ViewComposePreviewTheme.Light,
    val debug: Boolean = false,
    val debugTag: String = "ViewComposePreview",
)

@Composable
fun ViewComposePreview(
    modifier: Modifier = Modifier,
    options: ViewComposePreviewOptions = ViewComposePreviewOptions(),
    content: UiTreeBuilder.() -> Unit,
) {
    ViewComposePreviewHost(
        modifier = modifier,
        themeMode = options.theme.toHostThemeMode(),
        debug = options.debug,
        debugTag = options.debugTag,
        content = { _ ->
            content.invoke(this)
        },
    )
}

@Composable
fun ViewComposePreviewWithRoot(
    modifier: Modifier = Modifier,
    options: ViewComposePreviewOptions = ViewComposePreviewOptions(),
    content: UiTreeBuilder.(ViewGroup) -> Unit,
) {
    ViewComposePreviewHost(
        modifier = modifier,
        themeMode = options.theme.toHostThemeMode(),
        debug = options.debug,
        debugTag = options.debugTag,
        content = content,
    )
}

private fun ViewComposePreviewTheme.toHostThemeMode(): PreviewThemeMode {
    return when (this) {
        ViewComposePreviewTheme.Light -> PreviewThemeMode.Light
        ViewComposePreviewTheme.Dark -> PreviewThemeMode.Dark
    }
}
