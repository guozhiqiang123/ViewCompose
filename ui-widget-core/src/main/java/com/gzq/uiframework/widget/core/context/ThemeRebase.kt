package com.gzq.uiframework.widget.core

fun UiThemeTokens.override(
    colors: UiColors? = null,
    typography: UiTypography? = null,
    input: UiInputColors? = null,
    shapes: UiShapes? = null,
    controls: UiControlSizing? = null,
    components: UiComponentStyles? = null,
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

    val oldComponentDefaults = UiComponentStyleDefaults.fromTheme(this.colors, this.input)
    val newComponentDefaults = UiComponentStyleDefaults.fromTheme(resolvedColors, resolvedInput)
    val resolvedComponents = components ?: when {
        resolvedColors != this.colors || resolvedInput != this.input -> rebaseComponentStyles(
            current = this.components,
            oldDefaults = oldComponentDefaults,
            newDefaults = newComponentDefaults,
        )

        else -> this.components
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
        components = resolvedComponents,
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

internal fun rebaseComponentStyles(
    current: UiComponentStyles,
    oldDefaults: UiComponentStyles,
    newDefaults: UiComponentStyles,
): UiComponentStyles {
    return UiComponentStyles(
        button = UiButtonStyles(
            primaryContainer = rebaseValue(
                current.button.primaryContainer,
                oldDefaults.button.primaryContainer,
                newDefaults.button.primaryContainer,
            ),
            primaryContent = rebaseValue(
                current.button.primaryContent,
                oldDefaults.button.primaryContent,
                newDefaults.button.primaryContent,
            ),
            primaryDisabledContainer = rebaseValue(
                current.button.primaryDisabledContainer,
                oldDefaults.button.primaryDisabledContainer,
                newDefaults.button.primaryDisabledContainer,
            ),
            primaryDisabledContent = rebaseValue(
                current.button.primaryDisabledContent,
                oldDefaults.button.primaryDisabledContent,
                newDefaults.button.primaryDisabledContent,
            ),
            secondaryContainer = rebaseValue(
                current.button.secondaryContainer,
                oldDefaults.button.secondaryContainer,
                newDefaults.button.secondaryContainer,
            ),
            secondaryContent = rebaseValue(
                current.button.secondaryContent,
                oldDefaults.button.secondaryContent,
                newDefaults.button.secondaryContent,
            ),
            secondaryDisabledContainer = rebaseValue(
                current.button.secondaryDisabledContainer,
                oldDefaults.button.secondaryDisabledContainer,
                newDefaults.button.secondaryDisabledContainer,
            ),
            secondaryDisabledContent = rebaseValue(
                current.button.secondaryDisabledContent,
                oldDefaults.button.secondaryDisabledContent,
                newDefaults.button.secondaryDisabledContent,
            ),
            tonalContainer = rebaseValue(
                current.button.tonalContainer,
                oldDefaults.button.tonalContainer,
                newDefaults.button.tonalContainer,
            ),
            tonalContent = rebaseValue(
                current.button.tonalContent,
                oldDefaults.button.tonalContent,
                newDefaults.button.tonalContent,
            ),
            tonalDisabledContainer = rebaseValue(
                current.button.tonalDisabledContainer,
                oldDefaults.button.tonalDisabledContainer,
                newDefaults.button.tonalDisabledContainer,
            ),
            tonalDisabledContent = rebaseValue(
                current.button.tonalDisabledContent,
                oldDefaults.button.tonalDisabledContent,
                newDefaults.button.tonalDisabledContent,
            ),
            outlinedContent = rebaseValue(
                current.button.outlinedContent,
                oldDefaults.button.outlinedContent,
                newDefaults.button.outlinedContent,
            ),
            outlinedBorder = rebaseValue(
                current.button.outlinedBorder,
                oldDefaults.button.outlinedBorder,
                newDefaults.button.outlinedBorder,
            ),
            outlinedDisabledContent = rebaseValue(
                current.button.outlinedDisabledContent,
                oldDefaults.button.outlinedDisabledContent,
                newDefaults.button.outlinedDisabledContent,
            ),
            outlinedDisabledBorder = rebaseValue(
                current.button.outlinedDisabledBorder,
                oldDefaults.button.outlinedDisabledBorder,
                newDefaults.button.outlinedDisabledBorder,
            ),
        ),
        textField = UiTextFieldStyles(
            filledContainer = rebaseValue(
                current.textField.filledContainer,
                oldDefaults.textField.filledContainer,
                newDefaults.textField.filledContainer,
            ),
            filledDisabledContainer = rebaseValue(
                current.textField.filledDisabledContainer,
                oldDefaults.textField.filledDisabledContainer,
                newDefaults.textField.filledDisabledContainer,
            ),
            filledErrorContainer = rebaseValue(
                current.textField.filledErrorContainer,
                oldDefaults.textField.filledErrorContainer,
                newDefaults.textField.filledErrorContainer,
            ),
            tonalContainer = rebaseValue(
                current.textField.tonalContainer,
                oldDefaults.textField.tonalContainer,
                newDefaults.textField.tonalContainer,
            ),
            tonalDisabledContainer = rebaseValue(
                current.textField.tonalDisabledContainer,
                oldDefaults.textField.tonalDisabledContainer,
                newDefaults.textField.tonalDisabledContainer,
            ),
            tonalErrorContainer = rebaseValue(
                current.textField.tonalErrorContainer,
                oldDefaults.textField.tonalErrorContainer,
                newDefaults.textField.tonalErrorContainer,
            ),
            outlinedBorder = rebaseValue(
                current.textField.outlinedBorder,
                oldDefaults.textField.outlinedBorder,
                newDefaults.textField.outlinedBorder,
            ),
            outlinedDisabledBorder = rebaseValue(
                current.textField.outlinedDisabledBorder,
                oldDefaults.textField.outlinedDisabledBorder,
                newDefaults.textField.outlinedDisabledBorder,
            ),
            outlinedErrorBorder = rebaseValue(
                current.textField.outlinedErrorBorder,
                oldDefaults.textField.outlinedErrorBorder,
                newDefaults.textField.outlinedErrorBorder,
            ),
        ),
        segmentedControl = UiSegmentedControlStyles(
            background = rebaseValue(
                current.segmentedControl.background,
                oldDefaults.segmentedControl.background,
                newDefaults.segmentedControl.background,
            ),
            backgroundDisabled = rebaseValue(
                current.segmentedControl.backgroundDisabled,
                oldDefaults.segmentedControl.backgroundDisabled,
                newDefaults.segmentedControl.backgroundDisabled,
            ),
            indicator = rebaseValue(
                current.segmentedControl.indicator,
                oldDefaults.segmentedControl.indicator,
                newDefaults.segmentedControl.indicator,
            ),
            indicatorDisabled = rebaseValue(
                current.segmentedControl.indicatorDisabled,
                oldDefaults.segmentedControl.indicatorDisabled,
                newDefaults.segmentedControl.indicatorDisabled,
            ),
            text = rebaseValue(
                current.segmentedControl.text,
                oldDefaults.segmentedControl.text,
                newDefaults.segmentedControl.text,
            ),
            textDisabled = rebaseValue(
                current.segmentedControl.textDisabled,
                oldDefaults.segmentedControl.textDisabled,
                newDefaults.segmentedControl.textDisabled,
            ),
            selectedText = rebaseValue(
                current.segmentedControl.selectedText,
                oldDefaults.segmentedControl.selectedText,
                newDefaults.segmentedControl.selectedText,
            ),
            selectedTextDisabled = rebaseValue(
                current.segmentedControl.selectedTextDisabled,
                oldDefaults.segmentedControl.selectedTextDisabled,
                newDefaults.segmentedControl.selectedTextDisabled,
            ),
        ),
        checkbox = UiCheckboxStyles(
            label = rebaseValue(current.checkbox.label, oldDefaults.checkbox.label, newDefaults.checkbox.label),
            labelDisabled = rebaseValue(
                current.checkbox.labelDisabled,
                oldDefaults.checkbox.labelDisabled,
                newDefaults.checkbox.labelDisabled,
            ),
            control = rebaseValue(current.checkbox.control, oldDefaults.checkbox.control, newDefaults.checkbox.control),
            controlDisabled = rebaseValue(
                current.checkbox.controlDisabled,
                oldDefaults.checkbox.controlDisabled,
                newDefaults.checkbox.controlDisabled,
            ),
        ),
        switchControl = UiSwitchStyles(
            label = rebaseValue(
                current.switchControl.label,
                oldDefaults.switchControl.label,
                newDefaults.switchControl.label,
            ),
            labelDisabled = rebaseValue(
                current.switchControl.labelDisabled,
                oldDefaults.switchControl.labelDisabled,
                newDefaults.switchControl.labelDisabled,
            ),
            control = rebaseValue(
                current.switchControl.control,
                oldDefaults.switchControl.control,
                newDefaults.switchControl.control,
            ),
            controlDisabled = rebaseValue(
                current.switchControl.controlDisabled,
                oldDefaults.switchControl.controlDisabled,
                newDefaults.switchControl.controlDisabled,
            ),
        ),
        radioButton = UiRadioButtonStyles(
            label = rebaseValue(
                current.radioButton.label,
                oldDefaults.radioButton.label,
                newDefaults.radioButton.label,
            ),
            labelDisabled = rebaseValue(
                current.radioButton.labelDisabled,
                oldDefaults.radioButton.labelDisabled,
                newDefaults.radioButton.labelDisabled,
            ),
            control = rebaseValue(
                current.radioButton.control,
                oldDefaults.radioButton.control,
                newDefaults.radioButton.control,
            ),
            controlDisabled = rebaseValue(
                current.radioButton.controlDisabled,
                oldDefaults.radioButton.controlDisabled,
                newDefaults.radioButton.controlDisabled,
            ),
        ),
        slider = UiSliderStyles(
            control = rebaseValue(current.slider.control, oldDefaults.slider.control, newDefaults.slider.control),
            controlDisabled = rebaseValue(
                current.slider.controlDisabled,
                oldDefaults.slider.controlDisabled,
                newDefaults.slider.controlDisabled,
            ),
        ),
        progressIndicator = UiProgressIndicatorStyles(
            linearIndicator = rebaseValue(
                current.progressIndicator.linearIndicator,
                oldDefaults.progressIndicator.linearIndicator,
                newDefaults.progressIndicator.linearIndicator,
            ),
            linearTrack = rebaseValue(
                current.progressIndicator.linearTrack,
                oldDefaults.progressIndicator.linearTrack,
                newDefaults.progressIndicator.linearTrack,
            ),
            circularIndicator = rebaseValue(
                current.progressIndicator.circularIndicator,
                oldDefaults.progressIndicator.circularIndicator,
                newDefaults.progressIndicator.circularIndicator,
            ),
            circularTrack = rebaseValue(
                current.progressIndicator.circularTrack,
                oldDefaults.progressIndicator.circularTrack,
                newDefaults.progressIndicator.circularTrack,
            ),
        ),
        tabPager = UiTabPagerStyles(
            background = rebaseValue(current.tabPager.background, oldDefaults.tabPager.background, newDefaults.tabPager.background),
            indicator = rebaseValue(current.tabPager.indicator, oldDefaults.tabPager.indicator, newDefaults.tabPager.indicator),
            text = rebaseValue(current.tabPager.text, oldDefaults.tabPager.text, newDefaults.tabPager.text),
            selectedText = rebaseValue(
                current.tabPager.selectedText,
                oldDefaults.tabPager.selectedText,
                newDefaults.tabPager.selectedText,
            ),
        ),
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
