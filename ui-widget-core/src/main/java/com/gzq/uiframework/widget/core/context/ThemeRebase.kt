package com.gzq.uiframework.widget.core

fun UiThemeTokens.override(
    colors: UiColors? = null,
    typography: UiTypography? = null,
    input: UiInputColors? = null,
    shapes: UiShapes? = null,
    controls: UiControlSizing? = null,
    interactions: UiInteractionColors? = null,
): UiThemeTokens {
    val resolvedColors = colors ?: this.colors
    val resolvedTypography = typography ?: this.typography
    val resolvedShapes = shapes ?: this.shapes
    val resolvedControls = controls ?: this.controls

    val oldInputDefaults = UiInputDefaults.fromColors(this.colors)
    val newInputDefaults = UiInputDefaults.fromColors(resolvedColors)
    val resolvedInput = input ?: when {
        resolvedColors != this.colors -> rebaseInputColors(
            current = this.input,
            oldDefaults = oldInputDefaults,
            newDefaults = newInputDefaults,
        )

        else -> this.input
    }

    val oldInteractionDefaults = UiInteractionDefaults.fromColors(this.colors)
    val newInteractionDefaults = UiInteractionDefaults.fromColors(resolvedColors)
    val resolvedInteractions = interactions ?: when {
        resolvedColors != this.colors -> rebaseInteractionColors(
            current = this.interactions,
            oldDefaults = oldInteractionDefaults,
            newDefaults = newInteractionDefaults,
        )

        else -> this.interactions
    }

    return copy(
        colors = resolvedColors,
        typography = resolvedTypography,
        input = resolvedInput,
        shapes = resolvedShapes,
        controls = resolvedControls,
        interactions = resolvedInteractions,
    )
}

internal fun rebaseInputColors(
    current: UiInputColors,
    oldDefaults: UiInputColors,
    newDefaults: UiInputColors,
): UiInputColors {
    return UiInputColors(
        fieldContainer = rebaseValue(current.fieldContainer, oldDefaults.fieldContainer, newDefaults.fieldContainer),
        fieldContainerDisabled = rebaseValue(
            current.fieldContainerDisabled,
            oldDefaults.fieldContainerDisabled,
            newDefaults.fieldContainerDisabled,
        ),
        fieldError = rebaseValue(current.fieldError, oldDefaults.fieldError, newDefaults.fieldError),
        fieldText = rebaseValue(current.fieldText, oldDefaults.fieldText, newDefaults.fieldText),
        fieldTextDisabled = rebaseValue(current.fieldTextDisabled, oldDefaults.fieldTextDisabled, newDefaults.fieldTextDisabled),
        fieldHint = rebaseValue(current.fieldHint, oldDefaults.fieldHint, newDefaults.fieldHint),
        fieldHintDisabled = rebaseValue(current.fieldHintDisabled, oldDefaults.fieldHintDisabled, newDefaults.fieldHintDisabled),
        control = rebaseValue(current.control, oldDefaults.control, newDefaults.control),
        controlDisabled = rebaseValue(current.controlDisabled, oldDefaults.controlDisabled, newDefaults.controlDisabled),
    )
}

internal fun rebaseInteractionColors(
    current: UiInteractionColors,
    oldDefaults: UiInteractionColors,
    newDefaults: UiInteractionColors,
): UiInteractionColors {
    return UiInteractionColors(
        pressedOverlay = rebaseValue(
            current.pressedOverlay,
            oldDefaults.pressedOverlay,
            newDefaults.pressedOverlay,
        ),
    )
}

internal fun <T> rebaseValue(
    current: T,
    oldDefault: T,
    newDefault: T,
): T {
    return if (current == oldDefault) newDefault else current
}
