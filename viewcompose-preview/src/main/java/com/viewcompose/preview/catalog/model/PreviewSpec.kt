package com.viewcompose.preview.catalog.model

import com.viewcompose.widget.core.UiTreeBuilder

internal enum class PreviewDomain(
    val title: String,
) {
    Content("Content"),
    Input("Input"),
    Container("Container"),
    Collection("Collection"),
    Navigation("Navigation"),
    Feedback("Feedback"),
    Modifier("Modifier"),
    Animation("Animation"),
    Gesture("Gesture"),
    Graphics("Graphics"),
}

internal data class PreviewSpec(
    val id: String,
    val title: String,
    val domain: PreviewDomain,
    val content: UiTreeBuilder.() -> Unit,
)

internal data class PreviewSpecRef(
    val id: String,
)
