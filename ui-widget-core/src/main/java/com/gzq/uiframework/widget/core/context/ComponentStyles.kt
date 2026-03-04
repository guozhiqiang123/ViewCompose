package com.gzq.uiframework.widget.core

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
