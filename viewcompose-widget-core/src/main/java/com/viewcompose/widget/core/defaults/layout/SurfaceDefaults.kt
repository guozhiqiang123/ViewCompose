package com.viewcompose.widget.core

enum class SurfaceVariant {
    Default,
    Variant,
}

object SurfaceDefaults {
    fun backgroundColor(
        variant: SurfaceVariant = SurfaceVariant.Default,
    ): Int {
        return when (variant) {
            SurfaceVariant.Default -> Theme.colors.surface
            SurfaceVariant.Variant -> Theme.colors.surfaceVariant
        }
    }

    fun variantBackgroundColor(): Int = Theme.colors.surfaceVariant

    fun cardCornerRadius(): Int = Theme.shapes.mediumCornerRadius

    fun contentColor(
        variant: SurfaceVariant = SurfaceVariant.Default,
    ): Int {
        return when (variant) {
            SurfaceVariant.Default -> Theme.colors.onSurface
            SurfaceVariant.Variant -> Theme.colors.onSurfaceVariant
        }
    }

    fun variantContentColor(): Int = TextDefaults.secondaryColor()

    fun pressedColor(): Int = Theme.colors.ripple

    fun disabledAlpha(): Float = 0.72f
}
