package com.gzq.uiframework.widget.core

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
