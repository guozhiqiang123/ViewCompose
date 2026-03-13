package com.viewcompose.widget.core

enum class FabSize {
    Small,
    Medium,
    Large,
}

object FabDefaults {
    fun containerColor(): Int = Theme.colors.primaryContainer

    fun contentColor(): Int = Theme.colors.onPrimaryContainer

    fun size(size: FabSize = FabSize.Medium): Int {
        return when (size) {
            FabSize.Small -> Theme.controls.fab.smallSize
            FabSize.Medium -> Theme.controls.fab.mediumSize
            FabSize.Large -> Theme.controls.fab.largeSize
        }
    }

    fun iconSize(size: FabSize = FabSize.Medium): Int {
        return when (size) {
            FabSize.Small -> Theme.controls.fab.smallIconSize
            FabSize.Medium -> Theme.controls.fab.mediumIconSize
            FabSize.Large -> Theme.controls.fab.largeIconSize
        }
    }

    fun cornerRadius(size: FabSize = FabSize.Medium): Int {
        return when (size) {
            FabSize.Small -> 12.dp
            FabSize.Medium -> 16.dp
            FabSize.Large -> 28.dp
        }
    }

    fun elevation(): Int = Theme.controls.fab.elevation

    fun extendedHeight(): Int = Theme.controls.fab.extendedHeight

    fun extendedCornerRadius(): Int = Theme.shapes.largeCornerRadius

    fun extendedHorizontalPadding(): Int = Theme.controls.fab.extendedHorizontalPadding

    fun extendedIconSpacing(): Int = Theme.controls.fab.extendedIconSpacing

    fun extendedTextStyle(): UiTextStyle = TextDefaults.labelLargeStyle()

    fun pressedColor(): Int = Theme.colors.ripple
}
