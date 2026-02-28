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

data class UiControlSizing(
    val button: UiButtonSizing,
    val textField: UiTextFieldSizing,
    val segmentedControl: UiSegmentedControlSizing,
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
    val interactions: UiInteractionColors = UiInteractionDefaults.fromColors(colors),
)

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
    val colors: UiColors
        get() = LocalContext.current(LocalTheme).colors

    val typography: UiTypography
        get() = LocalContext.current(LocalTheme).typography

    val input: UiInputColors
        get() = LocalContext.current(LocalTheme).input

    val shapes: UiShapes
        get() = LocalContext.current(LocalTheme).shapes

    val controls: UiControlSizing
        get() = LocalContext.current(LocalTheme).controls

    val interactions: UiInteractionColors
        get() = LocalContext.current(LocalTheme).interactions
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
