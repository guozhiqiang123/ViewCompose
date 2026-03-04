package com.gzq.uiframework.widget.core

import android.content.Context

private val LocalTheme = LocalValue(UiThemeDefaults::light)

object Theme {
    val current: UiThemeTokens
        get() = LocalContext.current(LocalTheme)

    val colors: UiColors
        get() = current.colors

    val typography: UiTypography
        get() = current.typography

    val input: UiInputColors
        get() = current.input

    val shapes: UiShapes
        get() = current.shapes

    val controls: UiControlSizing
        get() = current.controls

    val components: UiComponentStyles
        get() = current.components

    val interactions: UiInteractionColors
        get() = current.interactions
}

fun UiTreeBuilder.UiTheme(
    tokens: UiThemeTokens? = null,
    androidContext: Context? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    val resolvedTokens = tokens
        ?: androidContext?.let(AndroidThemeBridge::fromContext)
        ?: UiThemeDefaults.light()
    LocalContext.provide(LocalTheme, resolvedTokens) {
        content()
    }
}

fun UiTreeBuilder.UiThemeOverride(
    colors: UiColors? = null,
    typography: UiTypography? = null,
    input: UiInputColors? = null,
    shapes: UiShapes? = null,
    controls: UiControlSizing? = null,
    components: UiComponentStyles? = null,
    interactions: UiInteractionColors? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(
        local = LocalTheme,
        value = Theme.current.override(
            colors = colors,
            typography = typography,
            input = input,
            shapes = shapes,
            controls = controls,
            components = components,
            interactions = interactions,
        ),
    ) {
        content()
    }
}

fun UiTreeBuilder.UiThemeOverride(
    colors: (UiColors.() -> UiColors)? = null,
    typography: (UiTypography.() -> UiTypography)? = null,
    input: (UiInputColors.() -> UiInputColors)? = null,
    shapes: (UiShapes.() -> UiShapes)? = null,
    controls: (UiControlSizing.() -> UiControlSizing)? = null,
    components: (UiComponentStyles.() -> UiComponentStyles)? = null,
    interactions: (UiInteractionColors.() -> UiInteractionColors)? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    UiThemeOverride(
        colors = colors?.invoke(Theme.colors),
        typography = typography?.invoke(Theme.typography),
        input = input?.invoke(Theme.input),
        shapes = shapes?.invoke(Theme.shapes),
        controls = controls?.invoke(Theme.controls),
        components = components?.invoke(Theme.components),
        interactions = interactions?.invoke(Theme.interactions),
        content = content,
    )
}
