package com.gzq.uiframework.widget.core

object SurfaceDefaults {
    fun backgroundColor(): Int = Theme.colors.surface

    fun variantBackgroundColor(): Int = Theme.colors.surfaceVariant

    fun cardCornerRadius(): Int = Theme.shapes.cardCornerRadius

    fun contentColor(): Int = TextDefaults.primaryColor()

    fun variantContentColor(): Int = TextDefaults.secondaryColor()

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
