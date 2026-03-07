package com.gzq.uiframework.widget.core

fun UiThemeTokens.override(
    colors: UiColors? = null,
    typography: UiTypography? = null,
    shapes: UiShapes? = null,
    controls: UiControlSizing? = null,
): UiThemeTokens {
    return copy(
        colors = colors ?: this.colors,
        typography = typography ?: this.typography,
        shapes = shapes ?: this.shapes,
        controls = controls ?: this.controls,
    )
}
