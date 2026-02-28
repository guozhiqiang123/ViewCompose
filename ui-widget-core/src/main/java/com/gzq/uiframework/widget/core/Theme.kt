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
)

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
