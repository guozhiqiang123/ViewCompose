package com.gzq.uiframework.widget.core

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray

object AndroidThemeBridge {
    fun fromContext(context: Context): UiThemeTokens {
        val isDark = isNightMode(context)
        return ThemeTokenMapper.fromThemeColors(
            readColor = { attr -> context.resolveThemeColor(attr) },
            readTextSizeSp = { attr -> context.resolveTextAppearanceTextSizeSp(attr) },
            isDarkMode = isDark,
        )
    }

    private fun isNightMode(context: Context): Boolean {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }
}

internal object ThemeTokenMapper {
    fun fromThemeColors(
        readColor: (Int) -> Int?,
        readTextSizeSp: (Int) -> Int? = { null },
        isDarkMode: Boolean = false,
    ): UiThemeTokens {
        val fallback = if (isDarkMode) UiThemeDefaults.dark() else UiThemeDefaults.light()
        return UiThemeTokens(
            colors = UiColors(
                background = readColor(android.R.attr.colorBackground) ?: fallback.colors.background,
                surface = readColor(com.google.android.material.R.attr.colorSurface) ?: fallback.colors.surface,
                surfaceVariant = readColor(com.google.android.material.R.attr.colorSurfaceVariant)
                    ?: fallback.colors.surfaceVariant,
                primary = readColor(androidx.appcompat.R.attr.colorPrimary) ?: fallback.colors.primary,
                accent = readColor(com.google.android.material.R.attr.colorSecondary) ?: fallback.colors.accent,
                divider = readColor(com.google.android.material.R.attr.colorOutline) ?: fallback.colors.divider,
                textPrimary = readColor(android.R.attr.textColorPrimary) ?: fallback.colors.textPrimary,
                textSecondary = readColor(android.R.attr.textColorSecondary) ?: fallback.colors.textSecondary,
            ),
            typography = UiTypography(
                title = UiTextStyle(
                    fontSizeSp = readTextSizeSp(android.R.attr.textAppearanceLarge)
                        ?: fallback.typography.title.fontSizeSp,
                ),
                body = UiTextStyle(
                    fontSizeSp = readTextSizeSp(android.R.attr.textAppearanceMedium)
                        ?: fallback.typography.body.fontSizeSp,
                ),
                label = UiTextStyle(
                    fontSizeSp = readTextSizeSp(android.R.attr.textAppearanceSmall)
                        ?: fallback.typography.label.fontSizeSp,
                ),
            ),
            shapes = UiShapeDefaults.default(),
        )
    }
}

private fun Context.resolveThemeColor(
    attr: Int,
): Int? {
    val typedArray: TypedArray = obtainStyledAttributes(intArrayOf(attr))
    return typedArray.use { array ->
        if (!array.hasValue(0)) {
            null
        } else {
            array.getColor(0, 0)
        }
    }
}

private fun Context.resolveTextAppearanceTextSizeSp(
    textAppearanceAttr: Int,
): Int? {
    val ta: TypedArray = obtainStyledAttributes(intArrayOf(textAppearanceAttr))
    val resId = if (ta.hasValue(0)) ta.getResourceId(0, 0) else 0
    ta.recycle()
    if (resId == 0) return null

    val attrs: TypedArray = obtainStyledAttributes(resId, intArrayOf(android.R.attr.textSize))
    val px = if (attrs.hasValue(0)) attrs.getDimensionPixelSize(0, 0) else -1
    attrs.recycle()
    if (px <= 0) return null

    return (px / resources.displayMetrics.scaledDensity).toInt()
}
