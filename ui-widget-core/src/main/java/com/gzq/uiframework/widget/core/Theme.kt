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

data class UiThemeTokens(
    val colors: UiColors,
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
        )
    }
}

internal object ThemeContext {
    private val currentThemes = ThreadLocal<List<UiThemeTokens>>()

    fun <T> withTheme(
        tokens: UiThemeTokens,
        block: () -> T,
    ): T {
        val previous = currentThemes.get().orEmpty()
        currentThemes.set(previous + tokens)
        return try {
            block()
        } finally {
            currentThemes.set(previous)
        }
    }

    fun current(): UiThemeTokens = currentThemes.get()?.lastOrNull() ?: UiThemeDefaults.light()
}

object Theme {
    val colors: UiColors
        get() = ThemeContext.current().colors
}

fun UiTreeBuilder.UiTheme(
    tokens: UiThemeTokens? = null,
    androidContext: Context? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    val resolvedTokens = tokens
        ?: androidContext?.let(AndroidThemeBridge::fromContext)
        ?: UiThemeDefaults.light()
    ThemeContext.withTheme(resolvedTokens) {
        content()
    }
}
