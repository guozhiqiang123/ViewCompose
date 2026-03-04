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

    val shapes: UiShapes
        get() = current.shapes

    val controls: UiControlSizing
        get() = current.controls
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
    shapes: UiShapes? = null,
    controls: UiControlSizing? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(
        local = LocalTheme,
        value = Theme.current.override(
            colors = colors,
            typography = typography,
            shapes = shapes,
            controls = controls,
        ),
    ) {
        content()
    }
}

fun UiTreeBuilder.UiThemeOverride(
    colors: (UiColors.() -> UiColors)? = null,
    typography: (UiTypography.() -> UiTypography)? = null,
    shapes: (UiShapes.() -> UiShapes)? = null,
    controls: (UiControlSizing.() -> UiControlSizing)? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    UiThemeOverride(
        colors = colors?.invoke(Theme.colors),
        typography = typography?.invoke(Theme.typography),
        shapes = shapes?.invoke(Theme.shapes),
        controls = controls?.invoke(Theme.controls),
        content = content,
    )
}
