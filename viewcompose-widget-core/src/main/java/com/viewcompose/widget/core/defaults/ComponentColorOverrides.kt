package com.viewcompose.widget.core

// --- Button ---

data class ButtonColorOverride(
    val primaryContainer: Int? = null,
    val primaryContent: Int? = null,
    val primaryDisabledContainer: Int? = null,
    val primaryDisabledContent: Int? = null,
    val secondaryContainer: Int? = null,
    val secondaryContent: Int? = null,
    val secondaryDisabledContainer: Int? = null,
    val secondaryDisabledContent: Int? = null,
    val tonalContainer: Int? = null,
    val tonalContent: Int? = null,
    val tonalDisabledContainer: Int? = null,
    val tonalDisabledContent: Int? = null,
    val outlinedContent: Int? = null,
    val outlinedBorder: Int? = null,
    val outlinedDisabledContent: Int? = null,
    val outlinedDisabledBorder: Int? = null,
)

internal val LocalButtonColors = uiLocalOf<ButtonColorOverride?> { null }

fun UiTreeBuilder.ProvideButtonColors(
    override: ButtonColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalButtonColors, override) { content() }
}

// --- TextField ---

data class TextFieldColorOverride(
    val filledContainer: Int? = null,
    val filledDisabledContainer: Int? = null,
    val filledErrorContainer: Int? = null,
    val tonalContainer: Int? = null,
    val tonalDisabledContainer: Int? = null,
    val tonalErrorContainer: Int? = null,
    val outlinedBorder: Int? = null,
    val outlinedDisabledBorder: Int? = null,
    val outlinedErrorBorder: Int? = null,
)

internal val LocalTextFieldColors = uiLocalOf<TextFieldColorOverride?> { null }

fun UiTreeBuilder.ProvideTextFieldColors(
    override: TextFieldColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalTextFieldColors, override) { content() }
}

// --- SegmentedControl ---

data class SegmentedControlColorOverride(
    val background: Int? = null,
    val backgroundDisabled: Int? = null,
    val indicator: Int? = null,
    val indicatorDisabled: Int? = null,
    val text: Int? = null,
    val textDisabled: Int? = null,
    val selectedText: Int? = null,
    val selectedTextDisabled: Int? = null,
)

internal val LocalSegmentedControlColors = uiLocalOf<SegmentedControlColorOverride?> { null }

fun UiTreeBuilder.ProvideSegmentedControlColors(
    override: SegmentedControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalSegmentedControlColors, override) { content() }
}

// --- InputControl (checkbox, switch, radio, slider) ---

data class InputControlColorOverride(
    val label: Int? = null,
    val labelDisabled: Int? = null,
    val control: Int? = null,
    val controlDisabled: Int? = null,
)

internal val LocalCheckboxColors = uiLocalOf<InputControlColorOverride?> { null }
internal val LocalSwitchColors = uiLocalOf<InputControlColorOverride?> { null }
internal val LocalRadioButtonColors = uiLocalOf<InputControlColorOverride?> { null }
internal val LocalSliderColors = uiLocalOf<InputControlColorOverride?> { null }

fun UiTreeBuilder.ProvideCheckboxColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalCheckboxColors, override) { content() }
}

fun UiTreeBuilder.ProvideSwitchColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalSwitchColors, override) { content() }
}

fun UiTreeBuilder.ProvideRadioButtonColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalRadioButtonColors, override) { content() }
}

fun UiTreeBuilder.ProvideSliderColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalSliderColors, override) { content() }
}

// --- ProgressIndicator ---

data class ProgressIndicatorColorOverride(
    val linearIndicator: Int? = null,
    val linearTrack: Int? = null,
    val circularIndicator: Int? = null,
    val circularTrack: Int? = null,
)

internal val LocalProgressIndicatorColors = uiLocalOf<ProgressIndicatorColorOverride?> { null }

fun UiTreeBuilder.ProvideProgressIndicatorColors(
    override: ProgressIndicatorColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(LocalProgressIndicatorColors, override) { content() }
}
