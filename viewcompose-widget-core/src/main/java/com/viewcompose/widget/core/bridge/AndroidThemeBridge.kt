package com.viewcompose.widget.core

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray

object AndroidThemeBridge {
    fun fromContext(context: Context): UiThemeTokens {
        val isDark = isNightMode(context)
        val snapshot = AndroidThemeSnapshotReader.read(context)
        return ThemeTokenMapper.fromSnapshot(
            snapshot = snapshot,
            isDarkMode = isDark,
        )
    }

    private fun isNightMode(context: Context): Boolean {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }
}

internal object ThemeTokenMapper {
    fun fromSnapshot(
        snapshot: AndroidThemeSnapshot,
        isDarkMode: Boolean = false,
    ): UiThemeTokens {
        val fallback = if (isDarkMode) UiThemeDefaults.dark() else UiThemeDefaults.light()
        val baseTokens = fromThemeColors(
            readColor = { attr ->
                when (attr) {
                    android.R.attr.colorBackground -> snapshot.colors.background
                    com.google.android.material.R.attr.colorSurface -> snapshot.colors.surface
                    com.google.android.material.R.attr.colorSurfaceVariant -> snapshot.colors.surfaceVariant
                    com.google.android.material.R.attr.colorOnSurface -> snapshot.colors.onSurface
                    com.google.android.material.R.attr.colorOnSurfaceVariant -> snapshot.colors.onSurfaceVariant
                    androidx.appcompat.R.attr.colorPrimary -> snapshot.colors.primary
                    com.google.android.material.R.attr.colorOnPrimary -> snapshot.colors.onPrimary
                    com.google.android.material.R.attr.colorPrimaryContainer -> snapshot.colors.primaryContainer
                    com.google.android.material.R.attr.colorOnPrimaryContainer -> snapshot.colors.onPrimaryContainer
                    com.google.android.material.R.attr.colorSecondary -> snapshot.colors.secondary
                    com.google.android.material.R.attr.colorOnSecondary -> snapshot.colors.onSecondary
                    com.google.android.material.R.attr.colorSecondaryContainer -> snapshot.colors.secondaryContainer
                    com.google.android.material.R.attr.colorOnSecondaryContainer -> snapshot.colors.onSecondaryContainer
                    android.R.attr.colorError -> snapshot.colors.error
                    com.google.android.material.R.attr.colorOnError -> snapshot.colors.onError
                    com.google.android.material.R.attr.colorErrorContainer -> snapshot.colors.errorContainer
                    com.google.android.material.R.attr.colorOnErrorContainer -> snapshot.colors.onErrorContainer
                    com.google.android.material.R.attr.colorOutline -> snapshot.colors.outline
                    com.google.android.material.R.attr.colorOutlineVariant -> snapshot.colors.outlineVariant
                    androidx.appcompat.R.attr.colorAccent -> snapshot.colors.surfaceTint
                    com.google.android.material.R.attr.colorSurfaceInverse -> snapshot.colors.inverseSurface
                    com.google.android.material.R.attr.colorOnSurfaceInverse -> snapshot.colors.inverseOnSurface
                    android.R.attr.textColorPrimary -> snapshot.colors.textPrimary
                    android.R.attr.textColorSecondary -> snapshot.colors.textSecondary
                    else -> null
                }
            },
            readRippleColor = { snapshot.colors.ripple },
            readScrimOpacity = { snapshot.scrimOpacity },
            isDarkMode = isDarkMode,
        )
        return baseTokens.copy(
            typography = UiTypography(
                title = resolveTextStyle(snapshot.typography.titleLarge, fallback.typography.title),
                body = resolveTextStyle(snapshot.typography.bodyLarge, fallback.typography.body),
                label = resolveTextStyle(snapshot.typography.labelLarge, fallback.typography.label),
                titleLarge = resolveTextStyle(snapshot.typography.titleLarge, fallback.typography.titleLarge),
                titleMedium = resolveTextStyle(snapshot.typography.titleMedium, fallback.typography.titleMedium),
                titleSmall = resolveTextStyle(snapshot.typography.titleSmall, fallback.typography.titleSmall),
                bodyLarge = resolveTextStyle(snapshot.typography.bodyLarge, fallback.typography.bodyLarge),
                bodyMedium = resolveTextStyle(snapshot.typography.bodyMedium, fallback.typography.bodyMedium),
                bodySmall = resolveTextStyle(snapshot.typography.bodySmall, fallback.typography.bodySmall),
                labelLarge = resolveTextStyle(snapshot.typography.labelLarge, fallback.typography.labelLarge),
                labelMedium = resolveTextStyle(snapshot.typography.labelMedium, fallback.typography.labelMedium),
                labelSmall = resolveTextStyle(snapshot.typography.labelSmall, fallback.typography.labelSmall),
            ),
            shapes = UiShapes(
                cardCornerRadius = snapshot.shapes.mediumCornerRadius ?: fallback.shapes.cardCornerRadius,
                interactiveCornerRadius = snapshot.shapes.smallCornerRadius
                    ?: fallback.shapes.interactiveCornerRadius,
                smallCornerRadius = snapshot.shapes.smallCornerRadius ?: fallback.shapes.smallCornerRadius,
                mediumCornerRadius = snapshot.shapes.mediumCornerRadius ?: fallback.shapes.mediumCornerRadius,
                largeCornerRadius = snapshot.shapes.largeCornerRadius ?: fallback.shapes.largeCornerRadius,
            ),
        )
    }

    fun fromThemeColors(
        readColor: (Int) -> Int?,
        readTextSizeSp: (Int) -> Int? = { null },
        readRippleColor: () -> Int? = { null },
        readScrimOpacity: () -> Float? = { null },
        isDarkMode: Boolean = false,
    ): UiThemeTokens {
        val fallback = if (isDarkMode) UiThemeDefaults.dark() else UiThemeDefaults.light()
        return UiThemeTokens(
            colors = UiColors(
                background = readColor(android.R.attr.colorBackground) ?: fallback.colors.background,
                surface = readColor(com.google.android.material.R.attr.colorSurface) ?: fallback.colors.surface,
                surfaceVariant = readColor(com.google.android.material.R.attr.colorSurfaceVariant)
                    ?: fallback.colors.surfaceVariant,
                textPrimary = readColor(android.R.attr.textColorPrimary) ?: fallback.colors.textPrimary,
                textSecondary = readColor(android.R.attr.textColorSecondary) ?: fallback.colors.textSecondary,
                onSurface = readColor(com.google.android.material.R.attr.colorOnSurface)
                    ?: fallback.colors.onSurface,
                onSurfaceVariant = readColor(com.google.android.material.R.attr.colorOnSurfaceVariant)
                    ?: fallback.colors.onSurfaceVariant,
                primary = readColor(androidx.appcompat.R.attr.colorPrimary) ?: fallback.colors.primary,
                onPrimary = readColor(com.google.android.material.R.attr.colorOnPrimary)
                    ?: fallback.colors.onPrimary,
                primaryContainer = readColor(com.google.android.material.R.attr.colorPrimaryContainer)
                    ?: fallback.colors.primaryContainer,
                onPrimaryContainer = readColor(com.google.android.material.R.attr.colorOnPrimaryContainer)
                    ?: fallback.colors.onPrimaryContainer,
                secondary = readColor(com.google.android.material.R.attr.colorSecondary)
                    ?: fallback.colors.secondary,
                onSecondary = readColor(com.google.android.material.R.attr.colorOnSecondary)
                    ?: fallback.colors.onSecondary,
                secondaryContainer = readColor(com.google.android.material.R.attr.colorSecondaryContainer)
                    ?: fallback.colors.secondaryContainer,
                onSecondaryContainer = readColor(com.google.android.material.R.attr.colorOnSecondaryContainer)
                    ?: fallback.colors.onSecondaryContainer,
                error = readColor(android.R.attr.colorError) ?: fallback.colors.error,
                onError = readColor(com.google.android.material.R.attr.colorOnError) ?: fallback.colors.onError,
                errorContainer = readColor(com.google.android.material.R.attr.colorErrorContainer)
                    ?: fallback.colors.errorContainer,
                onErrorContainer = readColor(com.google.android.material.R.attr.colorOnErrorContainer)
                    ?: fallback.colors.onErrorContainer,
                success = fallback.colors.success,
                warning = fallback.colors.warning,
                info = fallback.colors.info,
                divider = readColor(com.google.android.material.R.attr.colorOutline) ?: fallback.colors.divider,
                outline = readColor(com.google.android.material.R.attr.colorOutline) ?: fallback.colors.outline,
                outlineVariant = readColor(com.google.android.material.R.attr.colorOutlineVariant)
                    ?: fallback.colors.outlineVariant,
                surfaceTint = readColor(androidx.appcompat.R.attr.colorAccent)
                    ?: fallback.colors.surfaceTint,
                inverseSurface = readColor(com.google.android.material.R.attr.colorSurfaceInverse)
                    ?: fallback.colors.inverseSurface,
                inverseOnSurface = readColor(com.google.android.material.R.attr.colorOnSurfaceInverse)
                    ?: fallback.colors.inverseOnSurface,
                ripple = readRippleColor() ?: fallback.colors.ripple,
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
            shapes = fallback.shapes,
            controls = fallback.controls,
            overlays = UiOverlays(
                scrimOpacity = readScrimOpacity() ?: fallback.overlays.scrimOpacity,
            ),
        )
    }
}

private fun Context.resolveTextAppearanceTextSizeSp(
    textAppearanceAttr: Int,
): Int? {
    val ta: TypedArray = obtainStyledAttributes(intArrayOf(textAppearanceAttr))
    val resId = try {
        if (ta.hasValue(0)) ta.getResourceId(0, 0) else 0
    } finally {
        ta.recycle()
    }
    if (resId == 0) return null

    val attrs: TypedArray = obtainStyledAttributes(resId, intArrayOf(android.R.attr.textSize))
    val px = try {
        if (attrs.hasValue(0)) attrs.getDimensionPixelSize(0, 0) else -1
    } finally {
        attrs.recycle()
    }
    if (px <= 0) return null

    val density = resources.displayMetrics.density
    val fontScale = resources.configuration.fontScale.takeIf { it > 0f } ?: 1f
    return kotlin.math.round(px / (density * fontScale)).toInt()
}

private fun resolveTextStyle(
    snapshot: AndroidTextStyleSnapshot?,
    fallback: UiTextStyle,
): UiTextStyle {
    return UiTextStyle(
        fontSizeSp = snapshot?.fontSizeSp ?: fallback.fontSizeSp,
        fontWeight = snapshot?.fontWeight ?: fallback.fontWeight,
        fontFamily = snapshot?.fontFamily ?: fallback.fontFamily,
        letterSpacingEm = snapshot?.letterSpacingEm ?: fallback.letterSpacingEm,
        lineHeightSp = snapshot?.lineHeightSp ?: fallback.lineHeightSp,
        includeFontPadding = snapshot?.includeFontPadding ?: fallback.includeFontPadding,
        textDecoration = fallback.textDecoration,
    )
}
