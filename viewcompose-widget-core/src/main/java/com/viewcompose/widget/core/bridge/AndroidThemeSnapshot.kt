package com.viewcompose.widget.core

import android.content.Context
import android.content.res.TypedArray

internal data class AndroidThemeColorSnapshot(
    val background: Int? = null,
    val surface: Int? = null,
    val surfaceVariant: Int? = null,
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
    val textPrimary: Int? = null,
    val textSecondary: Int? = null,
    val ripple: Int? = null,
)

internal data class AndroidThemeSnapshot(
    val colors: AndroidThemeColorSnapshot = AndroidThemeColorSnapshot(),
    val scrimOpacity: Float? = null,
)

internal object AndroidThemeSnapshotReader {
    fun read(context: Context): AndroidThemeSnapshot {
        return AndroidThemeSnapshot(
            colors = readColorSnapshot(context),
            scrimOpacity = readScrimOpacity(context),
        )
    }

    private fun readColorSnapshot(context: Context): AndroidThemeColorSnapshot {
        val attrs = intArrayOf(
            android.R.attr.colorBackground,
            com.google.android.material.R.attr.colorSurface,
            com.google.android.material.R.attr.colorSurfaceVariant,
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
            AndroidThemeColorSnapshot(
                background = typedArray.getColorOrNull(0),
                surface = typedArray.getColorOrNull(1),
                surfaceVariant = typedArray.getColorOrNull(2),
                primary = typedArray.getColorOrNull(3),
                onPrimary = typedArray.getColorOrNull(4),
                primaryContainer = typedArray.getColorOrNull(5),
                onPrimaryContainer = typedArray.getColorOrNull(6),
                secondary = typedArray.getColorOrNull(7),
                onSecondary = typedArray.getColorOrNull(8),
                secondaryContainer = typedArray.getColorOrNull(9),
                onSecondaryContainer = typedArray.getColorOrNull(10),
                error = typedArray.getColorOrNull(11),
                onError = typedArray.getColorOrNull(12),
                errorContainer = typedArray.getColorOrNull(13),
                onErrorContainer = typedArray.getColorOrNull(14),
                outline = typedArray.getColorOrNull(15),
                outlineVariant = typedArray.getColorOrNull(16),
                surfaceTint = typedArray.getColorOrNull(17),
                inverseSurface = typedArray.getColorOrNull(18),
                inverseOnSurface = typedArray.getColorOrNull(19),
                textPrimary = typedArray.getColorOrNull(20),
                textSecondary = typedArray.getColorOrNull(21),
                ripple = typedArray.getColorOrNull(22),
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
}

private fun TypedArray.getColorOrNull(index: Int): Int? {
    return if (hasValue(index)) getColor(index, 0) else null
}
