package com.viewcompose.widget.core

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat

internal data class AndroidThemeColorSnapshot(
    val background: Int? = null,
    val surface: Int? = null,
    val surfaceVariant: Int? = null,
    val onSurface: Int? = null,
    val onSurfaceVariant: Int? = null,
    val primary: Int? = null,
    val onPrimary: Int? = null,
    val primaryContainer: Int? = null,
    val onPrimaryContainer: Int? = null,
    val secondary: Int? = null,
    val onSecondary: Int? = null,
    val secondaryContainer: Int? = null,
    val onSecondaryContainer: Int? = null,
    val error: Int? = null,
    val onError: Int? = null,
    val errorContainer: Int? = null,
    val onErrorContainer: Int? = null,
    val outline: Int? = null,
    val outlineVariant: Int? = null,
    val surfaceTint: Int? = null,
    val inverseSurface: Int? = null,
    val inverseOnSurface: Int? = null,
    val ripple: Int? = null,
)

internal data class AndroidThemeSnapshot(
    val colors: AndroidThemeColorSnapshot = AndroidThemeColorSnapshot(),
    val shapes: AndroidThemeShapeSnapshot = AndroidThemeShapeSnapshot(),
    val typography: AndroidThemeTypographySnapshot = AndroidThemeTypographySnapshot(),
    val scrimOpacity: Float? = null,
)

internal data class AndroidThemeShapeSnapshot(
    val smallCornerRadius: Int? = null,
    val mediumCornerRadius: Int? = null,
    val largeCornerRadius: Int? = null,
)

internal data class AndroidTextStyleSnapshot(
    val fontSizeSp: Int? = null,
    val fontWeight: Int? = null,
    val fontFamily: Typeface? = null,
    val letterSpacingEm: Float? = null,
    val lineHeightSp: Int? = null,
    val includeFontPadding: Boolean? = null,
)

internal data class AndroidThemeTypographySnapshot(
    val titleLarge: AndroidTextStyleSnapshot? = null,
    val titleMedium: AndroidTextStyleSnapshot? = null,
    val titleSmall: AndroidTextStyleSnapshot? = null,
    val bodyLarge: AndroidTextStyleSnapshot? = null,
    val bodyMedium: AndroidTextStyleSnapshot? = null,
    val bodySmall: AndroidTextStyleSnapshot? = null,
    val labelLarge: AndroidTextStyleSnapshot? = null,
    val labelMedium: AndroidTextStyleSnapshot? = null,
    val labelSmall: AndroidTextStyleSnapshot? = null,
)

internal object AndroidThemeSnapshotReader {
    fun read(context: Context): AndroidThemeSnapshot {
        return AndroidThemeSnapshot(
            colors = readColorSnapshot(context),
            shapes = readShapeSnapshot(context),
            typography = readTypographySnapshot(context),
            scrimOpacity = readScrimOpacity(context),
        )
    }

    private fun readColorSnapshot(context: Context): AndroidThemeColorSnapshot {
        val attrs = intArrayOf(
            android.R.attr.colorBackground,
            com.google.android.material.R.attr.colorSurface,
            com.google.android.material.R.attr.colorSurfaceVariant,
            com.google.android.material.R.attr.colorOnSurface,
            com.google.android.material.R.attr.colorOnSurfaceVariant,
            androidx.appcompat.R.attr.colorPrimary,
            com.google.android.material.R.attr.colorOnPrimary,
            com.google.android.material.R.attr.colorPrimaryContainer,
            com.google.android.material.R.attr.colorOnPrimaryContainer,
            com.google.android.material.R.attr.colorSecondary,
            com.google.android.material.R.attr.colorOnSecondary,
            com.google.android.material.R.attr.colorSecondaryContainer,
            com.google.android.material.R.attr.colorOnSecondaryContainer,
            android.R.attr.colorError,
            com.google.android.material.R.attr.colorOnError,
            com.google.android.material.R.attr.colorErrorContainer,
            com.google.android.material.R.attr.colorOnErrorContainer,
            com.google.android.material.R.attr.colorOutline,
            com.google.android.material.R.attr.colorOutlineVariant,
            androidx.appcompat.R.attr.colorAccent,
            com.google.android.material.R.attr.colorSurfaceInverse,
            com.google.android.material.R.attr.colorOnSurfaceInverse,
            android.R.attr.textColorPrimary,
            android.R.attr.textColorSecondary,
            androidx.appcompat.R.attr.colorControlHighlight,
        )
        val typedArray = context.obtainStyledAttributes(attrs)
        return try {
            val onSurface = typedArray.getColorOrNull(3) ?: typedArray.getColorOrNull(22)
            val onSurfaceVariant = typedArray.getColorOrNull(4) ?: typedArray.getColorOrNull(23)
            AndroidThemeColorSnapshot(
                background = typedArray.getColorOrNull(0),
                surface = typedArray.getColorOrNull(1),
                surfaceVariant = typedArray.getColorOrNull(2),
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
                primary = typedArray.getColorOrNull(5),
                onPrimary = typedArray.getColorOrNull(6),
                primaryContainer = typedArray.getColorOrNull(7),
                onPrimaryContainer = typedArray.getColorOrNull(8),
                secondary = typedArray.getColorOrNull(9),
                onSecondary = typedArray.getColorOrNull(10),
                secondaryContainer = typedArray.getColorOrNull(11),
                onSecondaryContainer = typedArray.getColorOrNull(12),
                error = typedArray.getColorOrNull(13),
                onError = typedArray.getColorOrNull(14),
                errorContainer = typedArray.getColorOrNull(15),
                onErrorContainer = typedArray.getColorOrNull(16),
                outline = typedArray.getColorOrNull(17),
                outlineVariant = typedArray.getColorOrNull(18),
                surfaceTint = typedArray.getColorOrNull(19),
                inverseSurface = typedArray.getColorOrNull(20),
                inverseOnSurface = typedArray.getColorOrNull(21),
                ripple = typedArray.getColorOrNull(24),
            )
        } finally {
            typedArray.recycle()
        }
    }

    private fun readScrimOpacity(context: Context): Float? {
        val typedArray = context.obtainStyledAttributes(intArrayOf(android.R.attr.backgroundDimAmount))
        return try {
            if (typedArray.hasValue(0)) typedArray.getFloat(0, 0f) else null
        } finally {
            typedArray.recycle()
        }
    }

    private fun readShapeSnapshot(context: Context): AndroidThemeShapeSnapshot {
        val attrs = intArrayOf(
            com.google.android.material.R.attr.shapeAppearanceSmallComponent,
            com.google.android.material.R.attr.shapeAppearanceMediumComponent,
            com.google.android.material.R.attr.shapeAppearanceLargeComponent,
        )
        val typedArray = context.obtainStyledAttributes(attrs)
        return try {
            AndroidThemeShapeSnapshot(
                smallCornerRadius = typedArray.getStyleRadiusOrNull(context, 0),
                mediumCornerRadius = typedArray.getStyleRadiusOrNull(context, 1),
                largeCornerRadius = typedArray.getStyleRadiusOrNull(context, 2),
            )
        } finally {
            typedArray.recycle()
        }
    }

    private fun readTypographySnapshot(context: Context): AndroidThemeTypographySnapshot {
        val attrs = intArrayOf(
            com.google.android.material.R.attr.textAppearanceTitleLarge,
            com.google.android.material.R.attr.textAppearanceTitleMedium,
            com.google.android.material.R.attr.textAppearanceTitleSmall,
            com.google.android.material.R.attr.textAppearanceBodyLarge,
            com.google.android.material.R.attr.textAppearanceBodyMedium,
            com.google.android.material.R.attr.textAppearanceBodySmall,
            com.google.android.material.R.attr.textAppearanceLabelLarge,
            com.google.android.material.R.attr.textAppearanceLabelMedium,
            com.google.android.material.R.attr.textAppearanceLabelSmall,
            android.R.attr.textAppearanceLarge,
            android.R.attr.textAppearanceMedium,
            android.R.attr.textAppearanceSmall,
        )
        val typedArray = context.obtainStyledAttributes(attrs)
        return try {
            val legacyTitle = typedArray.getTextStyleSnapshot(context, 9)
            val legacyBody = typedArray.getTextStyleSnapshot(context, 10)
            val legacyLabel = typedArray.getTextStyleSnapshot(context, 11)
            AndroidThemeTypographySnapshot(
                titleLarge = typedArray.getTextStyleSnapshot(context, 0) ?: legacyTitle,
                titleMedium = typedArray.getTextStyleSnapshot(context, 1) ?: legacyTitle,
                titleSmall = typedArray.getTextStyleSnapshot(context, 2) ?: legacyTitle,
                bodyLarge = typedArray.getTextStyleSnapshot(context, 3) ?: legacyBody,
                bodyMedium = typedArray.getTextStyleSnapshot(context, 4) ?: legacyBody,
                bodySmall = typedArray.getTextStyleSnapshot(context, 5) ?: legacyBody,
                labelLarge = typedArray.getTextStyleSnapshot(context, 6) ?: legacyLabel,
                labelMedium = typedArray.getTextStyleSnapshot(context, 7) ?: legacyLabel,
                labelSmall = typedArray.getTextStyleSnapshot(context, 8) ?: legacyLabel,
            )
        } finally {
            typedArray.recycle()
        }
    }
}

private fun TypedArray.getColorOrNull(index: Int): Int? {
    return if (hasValue(index)) getColor(index, 0) else null
}

private fun TypedArray.getStyleRadiusOrNull(context: Context, index: Int): Int? {
    if (!hasValue(index)) return null
    val styleRes = getResourceId(index, 0)
    if (styleRes == 0) return null
    val styleArray = context.obtainStyledAttributes(
        styleRes,
        intArrayOf(
            com.google.android.material.R.attr.cornerSize,
            com.google.android.material.R.attr.cornerSizeTopLeft,
            com.google.android.material.R.attr.cornerSizeTopRight,
            com.google.android.material.R.attr.cornerSizeBottomRight,
            com.google.android.material.R.attr.cornerSizeBottomLeft,
        ),
    )
    return try {
        if (styleArray.hasValue(0)) {
            return styleArray.getDimensionPixelSize(0, 0)
        }
        val corners = buildList {
            for (cornerIndex in 1..4) {
                if (styleArray.hasValue(cornerIndex)) {
                    add(styleArray.getDimensionPixelSize(cornerIndex, 0))
                }
            }
        }
        when {
            corners.isEmpty() -> null
            corners.distinct().size == 1 -> corners.first()
            else -> null
        }
    } finally {
        styleArray.recycle()
    }
}

private fun TypedArray.getTextStyleSnapshot(context: Context, index: Int): AndroidTextStyleSnapshot? {
    if (!hasValue(index)) return null
    val styleRes = getResourceId(index, 0)
    if (styleRes == 0) return null
    val styleArray = context.obtainStyledAttributes(
        styleRes,
        intArrayOf(
            android.R.attr.textSize,
            android.R.attr.textFontWeight,
            android.R.attr.fontFamily,
            androidx.appcompat.R.attr.fontFamily,
            android.R.attr.letterSpacing,
            android.R.attr.lineHeight,
            android.R.attr.includeFontPadding,
        ),
    )
    return try {
        val fontSizePx = if (styleArray.hasValue(0)) styleArray.getDimensionPixelSize(0, 0) else 0
        val lineHeightPx = if (styleArray.hasValue(5)) styleArray.getDimensionPixelSize(5, 0) else 0
        AndroidTextStyleSnapshot(
            fontSizeSp = fontSizePx.takeIf { it > 0 }?.let(context::pxToSp),
            fontWeight = if (styleArray.hasValue(1)) styleArray.getInt(1, 400) else null,
            fontFamily = resolveFontFamily(context, styleArray),
            letterSpacingEm = if (styleArray.hasValue(4)) styleArray.getFloat(4, 0f) else null,
            lineHeightSp = lineHeightPx.takeIf { it > 0 }?.let(context::pxToSp),
            includeFontPadding = if (styleArray.hasValue(6)) styleArray.getBoolean(6, false) else null,
        )
    } finally {
        styleArray.recycle()
    }
}

private fun resolveFontFamily(context: Context, styleArray: TypedArray): Typeface? {
    for (index in intArrayOf(2, 3)) {
        if (!styleArray.hasValue(index)) continue
        val resourceId = styleArray.getResourceId(index, 0)
        if (resourceId != 0) {
            runCatching { ResourcesCompat.getFont(context, resourceId) }.getOrNull()?.let { return it }
        }
        val value = styleArray.peekValue(index)
        if (value?.type == TypedValue.TYPE_STRING) {
            val familyName = value.string?.toString()
            if (!familyName.isNullOrBlank()) {
                return Typeface.create(familyName, Typeface.NORMAL)
            }
        }
    }
    return null
}

private fun Context.pxToSp(value: Int): Int {
    val density = resources.displayMetrics.density
    val fontScale = resources.configuration.fontScale.takeIf { it > 0f } ?: 1f
    return kotlin.math.round(value / (density * fontScale)).toInt()
}
