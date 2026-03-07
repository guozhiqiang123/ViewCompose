package com.viewcompose.widget.core

data class UiColors(
    val background: Int,
    val surface: Int,
    val surfaceVariant: Int,
    val primary: Int,
    val secondary: Int,
    val error: Int,
    val success: Int,
    val warning: Int,
    val info: Int,
    val divider: Int,
    val textPrimary: Int,
    val textSecondary: Int,
    val ripple: Int = pressedOverlayColorFor(textPrimary),
) {
    @Deprecated(
        message = "Use secondary instead.",
        replaceWith = ReplaceWith("secondary"),
    )
    val accent: Int
        get() = secondary
}

data class UiShapes(
    val cardCornerRadius: Int,
    val controlCornerRadius: Int,
)

data class UiTextStyle(
    val fontSizeSp: Int,
    val fontWeight: Int? = null,
    val fontFamily: android.graphics.Typeface? = null,
    val letterSpacingEm: Float? = null,
    val lineHeightSp: Int? = null,
    val includeFontPadding: Boolean = false,
    val textDecoration: com.viewcompose.renderer.node.TextDecoration? = null,
)

data class UiTypography(
    val title: UiTextStyle,
    val body: UiTextStyle,
    val label: UiTextStyle,
    val titleLarge: UiTextStyle = title,
    val titleMedium: UiTextStyle = title,
    val titleSmall: UiTextStyle = title,
    val bodyLarge: UiTextStyle = body,
    val bodyMedium: UiTextStyle = body,
    val bodySmall: UiTextStyle = body,
    val labelLarge: UiTextStyle = label,
    val labelMedium: UiTextStyle = label,
    val labelSmall: UiTextStyle = label,
)

data class UiThemeTokens(
    val colors: UiColors,
    val typography: UiTypography,
    val shapes: UiShapes = UiShapeDefaults.default(),
    val controls: UiControlSizing = UiControlSizeDefaults.default(),
    val overlays: UiOverlays = UiOverlayDefaults.default(),
)

data class UiOverlays(
    val scrimOpacity: Float,
)

internal fun pressedOverlayColorFor(textPrimary: Int): Int {
    val base = textPrimary and 0x00FFFFFF
    return 0x22000000 or base
}

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
