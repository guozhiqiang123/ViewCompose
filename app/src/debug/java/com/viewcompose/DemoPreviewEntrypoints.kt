package com.viewcompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.viewcompose.preview.ViewComposePreview
import com.viewcompose.preview.ViewComposePreviewOptions
import com.viewcompose.preview.ViewComposePreviewTheme

@Preview(
    name = "Demo Preview Chapter Light",
    group = "Demo/Preview",
    widthDp = 411,
    heightDp = 891,
    showBackground = true,
)
@Composable
private fun DemoPreviewChapterLightPreview() {
    ViewComposePreview(
        options = ViewComposePreviewOptions(
            theme = ViewComposePreviewTheme.Light,
            debugTag = "DemoPreviewChapterLight",
        ),
    ) { _ ->
        PreviewPage(initialPageIndex = 0)
    }
}

@Preview(
    name = "Demo Preview Chapter Dark",
    group = "Demo/Preview",
    widthDp = 411,
    heightDp = 891,
    showBackground = true,
)
@Composable
private fun DemoPreviewChapterDarkPreview() {
    ViewComposePreview(
        options = ViewComposePreviewOptions(
            theme = ViewComposePreviewTheme.Dark,
            debugTag = "DemoPreviewChapterDark",
        ),
    ) { _ ->
        PreviewPage(initialPageIndex = 1)
    }
}


@Preview(
    name = "Demo Catalog Dark",
    group = "Demo/Preview",
    widthDp = 411,
    heightDp = 891,
    showBackground = true,
)
@Composable
private fun DemoPreviewCatalogPageDarkPreview() {
    ViewComposePreview(
        options = ViewComposePreviewOptions(
            theme = ViewComposePreviewTheme.Dark,
            debugTag = "DemoPreviewChapterDark",
        ),
    ) { root ->
        DemoCatalogPage(root)
    }
}
