package com.gzq.uiframework.widget.core

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

internal val LocalButtonColors = LocalValue<ButtonColorOverride?> { null }

fun UiTreeBuilder.ProvideButtonColors(
    override: ButtonColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalButtonColors, override) { content() }
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

internal val LocalTextFieldColors = LocalValue<TextFieldColorOverride?> { null }

fun UiTreeBuilder.ProvideTextFieldColors(
    override: TextFieldColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalTextFieldColors, override) { content() }
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

internal val LocalSegmentedControlColors = LocalValue<SegmentedControlColorOverride?> { null }

fun UiTreeBuilder.ProvideSegmentedControlColors(
    override: SegmentedControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalSegmentedControlColors, override) { content() }
}

// --- InputControl (checkbox, switch, radio, slider) ---

data class InputControlColorOverride(
    val label: Int? = null,
    val labelDisabled: Int? = null,
    val control: Int? = null,
    val controlDisabled: Int? = null,
)

internal val LocalCheckboxColors = LocalValue<InputControlColorOverride?> { null }
internal val LocalSwitchColors = LocalValue<InputControlColorOverride?> { null }
internal val LocalRadioButtonColors = LocalValue<InputControlColorOverride?> { null }
internal val LocalSliderColors = LocalValue<InputControlColorOverride?> { null }

fun UiTreeBuilder.ProvideCheckboxColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalCheckboxColors, override) { content() }
}

fun UiTreeBuilder.ProvideSwitchColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalSwitchColors, override) { content() }
}

fun UiTreeBuilder.ProvideRadioButtonColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalRadioButtonColors, override) { content() }
}

fun UiTreeBuilder.ProvideSliderColors(
    override: InputControlColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalSliderColors, override) { content() }
}

// --- ProgressIndicator ---

data class ProgressIndicatorColorOverride(
    val linearIndicator: Int? = null,
    val linearTrack: Int? = null,
    val circularIndicator: Int? = null,
    val circularTrack: Int? = null,
)

internal val LocalProgressIndicatorColors = LocalValue<ProgressIndicatorColorOverride?> { null }

fun UiTreeBuilder.ProvideProgressIndicatorColors(
    override: ProgressIndicatorColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalProgressIndicatorColors, override) { content() }
}

// --- TabPager ---

data class TabPagerColorOverride(
    val background: Int? = null,
    val indicator: Int? = null,
    val text: Int? = null,
    val selectedText: Int? = null,
)

internal val LocalTabPagerColors = LocalValue<TabPagerColorOverride?> { null }

fun UiTreeBuilder.ProvideTabPagerColors(
    override: TabPagerColorOverride,
    content: UiTreeBuilder.() -> Unit,
) {
    LocalContext.provide(LocalTabPagerColors, override) { content() }
}
