package com.gzq.uiframework.widget.core

import android.content.Context
import android.content.res.TypedArray

object AndroidThemeBridge {
    fun fromContext(context: Context): UiThemeTokens {
        return ThemeTokenMapper.fromThemeColors { attr ->
            context.resolveThemeColor(attr)
        }
    }
}

internal object ThemeTokenMapper {
    fun fromThemeColors(
        readColor: (Int) -> Int?,
    ): UiThemeTokens {
        val fallback = UiThemeDefaults.light().colors
        return UiThemeTokens(
            colors = UiColors(
                background = readColor(android.R.attr.colorBackground) ?: fallback.background,
                surface = readColor(com.google.android.material.R.attr.colorSurface) ?: fallback.surface,
                surfaceVariant = readColor(com.google.android.material.R.attr.colorSurfaceVariant)
                    ?: fallback.surfaceVariant,
                primary = readColor(androidx.appcompat.R.attr.colorPrimary) ?: fallback.primary,
                accent = readColor(com.google.android.material.R.attr.colorSecondary) ?: fallback.accent,
                divider = readColor(com.google.android.material.R.attr.colorOutline) ?: fallback.divider,
                textPrimary = readColor(android.R.attr.textColorPrimary) ?: fallback.textPrimary,
                textSecondary = readColor(android.R.attr.textColorSecondary) ?: fallback.textSecondary,
            ),
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
