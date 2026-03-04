package com.gzq.uiframework.widget.core

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
