package com.gzq.uiframework.widget.core

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

    fun cardCornerRadius(): Int = Theme.shapes.cardCornerRadius

    fun contentColor(
        variant: SurfaceVariant = SurfaceVariant.Default,
    ): Int {
        return when (variant) {
            SurfaceVariant.Default -> Theme.colors.textPrimary
            SurfaceVariant.Variant -> Theme.colors.textSecondary
        }
    }

    fun variantContentColor(): Int = TextDefaults.secondaryColor()

    fun pressedColor(): Int = Theme.interactions.pressedOverlay

    fun disabledAlpha(): Float = 0.72f
}
