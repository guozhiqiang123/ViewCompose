package com.gzq.uiframework.widget.core

import android.content.Context

data class UiColors(
    val background: Int,
    val surface: Int,
    val surfaceVariant: Int,
    val primary: Int,
    val accent: Int,
    val divider: Int,
    val textPrimary: Int,
    val textSecondary: Int,
)

data class UiInputColors(
    val fieldContainer: Int,
    val fieldContainerDisabled: Int,
    val fieldError: Int,
    val fieldText: Int,
    val fieldTextDisabled: Int,
    val fieldHint: Int,
    val fieldHintDisabled: Int,
    val control: Int,
    val controlDisabled: Int,
)

data class UiShapes(
    val cardCornerRadius: Int,
    val controlCornerRadius: Int,
)

data class UiButtonSizing(
    val compactHeight: Int,
    val mediumHeight: Int,
    val largeHeight: Int,
    val compactHorizontalPadding: Int,
    val mediumHorizontalPadding: Int,
    val largeHorizontalPadding: Int,
    val compactVerticalPadding: Int,
    val mediumVerticalPadding: Int,
    val largeVerticalPadding: Int,
)

data class UiTextFieldSizing(
    val compactHeight: Int,
    val mediumHeight: Int,
    val largeHeight: Int,
    val compactHorizontalPadding: Int,
    val mediumHorizontalPadding: Int,
    val largeHorizontalPadding: Int,
    val compactVerticalPadding: Int,
    val mediumVerticalPadding: Int,
    val largeVerticalPadding: Int,
)

data class UiSegmentedControlSizing(
    val compactHeight: Int,
    val mediumHeight: Int,
    val largeHeight: Int,
    val compactHorizontalPadding: Int,
    val mediumHorizontalPadding: Int,
    val largeHorizontalPadding: Int,
    val compactVerticalPadding: Int,
    val mediumVerticalPadding: Int,
    val largeVerticalPadding: Int,
)

data class UiProgressIndicatorSizing(
    val linearTrackThickness: Int,
    val circularSize: Int,
    val circularTrackThickness: Int,
)

data class UiControlSizing(
    val button: UiButtonSizing,
    val textField: UiTextFieldSizing,
    val segmentedControl: UiSegmentedControlSizing,
    val progressIndicator: UiProgressIndicatorSizing,
)

data class UiButtonStyles(
    val primaryContainer: Int,
    val primaryContent: Int,
    val primaryDisabledContainer: Int,
    val primaryDisabledContent: Int,
    val secondaryContainer: Int,
    val secondaryContent: Int,
    val secondaryDisabledContainer: Int,
    val secondaryDisabledContent: Int,
    val tonalContainer: Int,
    val tonalContent: Int,
    val tonalDisabledContainer: Int,
    val tonalDisabledContent: Int,
    val outlinedContent: Int,
    val outlinedBorder: Int,
    val outlinedDisabledContent: Int,
    val outlinedDisabledBorder: Int,
)

data class UiTextFieldStyles(
    val filledContainer: Int,
    val filledDisabledContainer: Int,
    val filledErrorContainer: Int,
    val tonalContainer: Int,
    val tonalDisabledContainer: Int,
    val tonalErrorContainer: Int,
    val outlinedBorder: Int,
    val outlinedDisabledBorder: Int,
    val outlinedErrorBorder: Int,
)

data class UiSegmentedControlStyles(
    val background: Int,
    val backgroundDisabled: Int,
    val indicator: Int,
    val indicatorDisabled: Int,
    val text: Int,
    val textDisabled: Int,
    val selectedText: Int,
    val selectedTextDisabled: Int,
)

data class UiCheckboxStyles(
    val label: Int,
    val labelDisabled: Int,
    val control: Int,
    val controlDisabled: Int,
)

data class UiSwitchStyles(
    val label: Int,
    val labelDisabled: Int,
    val control: Int,
    val controlDisabled: Int,
)

data class UiRadioButtonStyles(
    val label: Int,
    val labelDisabled: Int,
    val control: Int,
    val controlDisabled: Int,
)

data class UiSliderStyles(
    val control: Int,
    val controlDisabled: Int,
)

data class UiProgressIndicatorStyles(
    val linearIndicator: Int,
    val linearTrack: Int,
    val circularIndicator: Int,
    val circularTrack: Int,
)

data class UiTabPagerStyles(
    val background: Int,
    val indicator: Int,
    val text: Int,
    val selectedText: Int,
)

data class UiComponentStyles(
    val button: UiButtonStyles,
    val textField: UiTextFieldStyles,
    val segmentedControl: UiSegmentedControlStyles,
    val checkbox: UiCheckboxStyles,
    val switchControl: UiSwitchStyles,
    val radioButton: UiRadioButtonStyles,
    val slider: UiSliderStyles,
    val progressIndicator: UiProgressIndicatorStyles,
    val tabPager: UiTabPagerStyles,
)

data class UiInteractionColors(
    val pressedOverlay: Int,
)

data class UiTextStyle(
    val fontSizeSp: Int,
)

data class UiTypography(
    val title: UiTextStyle,
    val body: UiTextStyle,
    val label: UiTextStyle,
)

data class UiThemeTokens(
    val colors: UiColors,
    val typography: UiTypography,
    val input: UiInputColors = UiInputDefaults.fromColors(colors),
    val shapes: UiShapes = UiShapeDefaults.default(),
    val controls: UiControlSizing = UiControlSizeDefaults.default(),
    val components: UiComponentStyles = UiComponentStyleDefaults.fromTheme(colors, input),
    val interactions: UiInteractionColors = UiInteractionDefaults.fromColors(colors),
)

internal fun contentColorFor(backgroundColor: Int): Int {
    val red = backgroundColor shr 16 and 0xFF
    val green = backgroundColor shr 8 and 0xFF
    val blue = backgroundColor and 0xFF
    val luma = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luma >= 186) {
        0xFF000000.toInt()
    } else {
        0xFFFFFFFF.toInt()
    }
}

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

private fun rebaseInputColors(
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

private fun rebaseComponentStyles(
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

private fun rebaseInteractionColors(
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

private fun <T> rebaseValue(
    current: T,
    oldDefault: T,
    newDefault: T,
): T {
    return if (current == oldDefault) newDefault else current
}

object UiInputDefaults {
    fun fromColors(colors: UiColors): UiInputColors {
        return UiInputColors(
            fieldContainer = colors.surface,
            fieldContainerDisabled = colors.surfaceVariant,
            fieldError = 0xFFB3261E.toInt(),
            fieldText = colors.textPrimary,
            fieldTextDisabled = colors.textSecondary,
            fieldHint = colors.textSecondary,
            fieldHintDisabled = colors.divider,
            control = colors.primary,
            controlDisabled = colors.divider,
        )
    }
}

object UiShapeDefaults {
    fun default(): UiShapes {
        return UiShapes(
            cardCornerRadius = 20.dp,
            controlCornerRadius = 14.dp,
        )
    }
}

object UiControlSizeDefaults {
    fun default(): UiControlSizing {
        return UiControlSizing(
            button = UiButtonSizing(
                compactHeight = 36.dp,
                mediumHeight = 44.dp,
                largeHeight = 52.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 16.dp,
                largeHorizontalPadding = 20.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            textField = UiTextFieldSizing(
                compactHeight = 40.dp,
                mediumHeight = 48.dp,
                largeHeight = 56.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 14.dp,
                largeHorizontalPadding = 16.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            segmentedControl = UiSegmentedControlSizing(
                compactHeight = 36.dp,
                mediumHeight = 42.dp,
                largeHeight = 48.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 14.dp,
                largeHorizontalPadding = 18.dp,
                compactVerticalPadding = 6.dp,
                mediumVerticalPadding = 8.dp,
                largeVerticalPadding = 10.dp,
            ),
            progressIndicator = UiProgressIndicatorSizing(
                linearTrackThickness = 6.dp,
                circularSize = 32.dp,
                circularTrackThickness = 4.dp,
            ),
        )
    }
}

object UiComponentStyleDefaults {
    fun fromTheme(
        colors: UiColors,
        input: UiInputColors,
    ): UiComponentStyles {
        return UiComponentStyles(
            button = UiButtonStyles(
                primaryContainer = colors.primary,
                primaryContent = contentColorFor(colors.primary),
                primaryDisabledContainer = colors.divider,
                primaryDisabledContent = colors.textSecondary,
                secondaryContainer = colors.accent,
                secondaryContent = contentColorFor(colors.accent),
                secondaryDisabledContainer = colors.divider,
                secondaryDisabledContent = colors.textSecondary,
                tonalContainer = colors.surfaceVariant,
                tonalContent = colors.textPrimary,
                tonalDisabledContainer = input.fieldContainerDisabled,
                tonalDisabledContent = colors.textSecondary,
                outlinedContent = colors.textPrimary,
                outlinedBorder = colors.divider,
                outlinedDisabledContent = colors.textSecondary,
                outlinedDisabledBorder = input.controlDisabled,
            ),
            textField = UiTextFieldStyles(
                filledContainer = input.fieldContainer,
                filledDisabledContainer = input.fieldContainerDisabled,
                filledErrorContainer = input.fieldError,
                tonalContainer = colors.surfaceVariant,
                tonalDisabledContainer = input.fieldContainerDisabled,
                tonalErrorContainer = input.fieldError,
                outlinedBorder = input.control,
                outlinedDisabledBorder = input.controlDisabled,
                outlinedErrorBorder = input.fieldError,
            ),
            segmentedControl = UiSegmentedControlStyles(
                background = colors.surfaceVariant,
                backgroundDisabled = colors.surface,
                indicator = colors.primary,
                indicatorDisabled = input.controlDisabled,
                text = colors.textSecondary,
                textDisabled = colors.textSecondary,
                selectedText = contentColorFor(colors.primary),
                selectedTextDisabled = colors.textSecondary,
            ),
            checkbox = UiCheckboxStyles(
                label = input.fieldText,
                labelDisabled = input.fieldTextDisabled,
                control = input.control,
                controlDisabled = input.controlDisabled,
            ),
            switchControl = UiSwitchStyles(
                label = input.fieldText,
                labelDisabled = input.fieldTextDisabled,
                control = input.control,
                controlDisabled = input.controlDisabled,
            ),
            radioButton = UiRadioButtonStyles(
                label = input.fieldText,
                labelDisabled = input.fieldTextDisabled,
                control = input.control,
                controlDisabled = input.controlDisabled,
            ),
            slider = UiSliderStyles(
                control = input.control,
                controlDisabled = input.controlDisabled,
            ),
            progressIndicator = UiProgressIndicatorStyles(
                linearIndicator = colors.primary,
                linearTrack = colors.divider,
                circularIndicator = colors.primary,
                circularTrack = colors.divider,
            ),
            tabPager = UiTabPagerStyles(
                background = colors.surfaceVariant,
                indicator = colors.primary,
                text = colors.textSecondary,
                selectedText = colors.textPrimary,
            ),
        )
    }
}

object UiInteractionDefaults {
    fun fromColors(colors: UiColors): UiInteractionColors {
        val base = colors.textPrimary and 0x00FFFFFF
        return UiInteractionColors(
            pressedOverlay = 0x22000000 or base,
        )
    }
}

object UiThemeDefaults {
    fun light(): UiThemeTokens {
        return UiThemeTokens(
            colors = UiColors(
                background = 0xFFF4F1EA.toInt(),
                surface = 0xFFE6D9C6.toInt(),
                surfaceVariant = 0xFFFDECC8.toInt(),
                primary = 0xFFBFD8A6.toInt(),
                accent = 0xFFD6C6F0.toInt(),
                divider = 0xFFD8CCBA.toInt(),
                textPrimary = 0xFF2F241B.toInt(),
                textSecondary = 0xFF6A5A4A.toInt(),
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 24.sp),
                body = UiTextStyle(fontSizeSp = 16.sp),
                label = UiTextStyle(fontSizeSp = 14.sp),
            ),
        )
    }

    fun dark(): UiThemeTokens {
        return UiThemeTokens(
            colors = UiColors(
                background = 0xFF1F1B18.toInt(),
                surface = 0xFF2C2621.toInt(),
                surfaceVariant = 0xFF3A332D.toInt(),
                primary = 0xFF7EA16D.toInt(),
                accent = 0xFF8B7AA8.toInt(),
                divider = 0xFF51473E.toInt(),
                textPrimary = 0xFFF4EFE8.toInt(),
                textSecondary = 0xFFD0C4B6.toInt(),
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 24.sp),
                body = UiTextStyle(fontSizeSp = 16.sp),
                label = UiTextStyle(fontSizeSp = 14.sp),
            ),
        )
    }
}

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
