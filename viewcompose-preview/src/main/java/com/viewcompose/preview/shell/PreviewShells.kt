package com.viewcompose.preview.shell

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.viewcompose.preview.host.PreviewThemeMode
import com.viewcompose.preview.host.ViewComposePreviewHost
import com.viewcompose.ui.modifier.Modifier as UiModifier
import com.viewcompose.ui.modifier.fillMaxSize
import com.viewcompose.ui.modifier.padding
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.Text
import com.viewcompose.widget.core.UiTextStyle
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.dp
import com.viewcompose.widget.core.sp

@Composable
internal fun ViewComposePreviewSurface(
    themeMode: PreviewThemeMode,
    content: UiTreeBuilder.() -> Unit,
) {
    ViewComposePreviewHost(
        themeMode = themeMode,
        modifier = androidx.compose.ui.Modifier,
        content = content,
    )
}

private fun UiTreeBuilder.previewBootstrapContent() {
    Column(
        spacing = 8.dp,
        modifier = UiModifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "ViewCompose Preview",
            style = UiTextStyle(fontSizeSp = 20.sp),
        )
        Text(
            text = "Compose Preview bridge is active.",
            style = UiTextStyle(fontSizeSp = 14.sp),
        )
    }
}

@Preview(
    name = "ViewCompose Phone Light",
    group = "ViewCompose/Bridge",
    widthDp = 411,
    heightDp = 891,
    showBackground = true,
    backgroundColor = 0xFFF4F1EA,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun ViewComposePhoneLightPreview() {
    ViewComposePreviewSurface(themeMode = PreviewThemeMode.Light) {
        previewBootstrapContent()
    }
}

@Preview(
    name = "ViewCompose Phone Dark",
    group = "ViewCompose/Bridge",
    widthDp = 411,
    heightDp = 891,
    showBackground = true,
    backgroundColor = 0xFF1F1B18,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ViewComposePhoneDarkPreview() {
    ViewComposePreviewSurface(themeMode = PreviewThemeMode.Dark) {
        previewBootstrapContent()
    }
}

@Preview(
    name = "ViewCompose Tablet Light",
    group = "ViewCompose/Bridge",
    widthDp = 840,
    heightDp = 1280,
    showBackground = true,
    backgroundColor = 0xFFF4F1EA,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun ViewComposeTabletLightPreview() {
    ViewComposePreviewSurface(themeMode = PreviewThemeMode.Light) {
        previewBootstrapContent()
    }
}

@Preview(
    name = "ViewCompose Tablet Dark",
    group = "ViewCompose/Bridge",
    widthDp = 840,
    heightDp = 1280,
    showBackground = true,
    backgroundColor = 0xFF1F1B18,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ViewComposeTabletDarkPreview() {
    ViewComposePreviewSurface(themeMode = PreviewThemeMode.Dark) {
        previewBootstrapContent()
    }
}
