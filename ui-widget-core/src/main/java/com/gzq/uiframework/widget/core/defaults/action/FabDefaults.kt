package com.gzq.uiframework.widget.core

enum class FabSize {
    Small,
    Medium,
    Large,
}

object FabDefaults {
    fun containerColor(): Int = Theme.colors.primary

    fun contentColor(): Int = contentColorFor(Theme.colors.primary)

    fun size(size: FabSize = FabSize.Medium): Int {
        return when (size) {
            FabSize.Small -> 40.dp
            FabSize.Medium -> 56.dp
            FabSize.Large -> 96.dp
        }
    }

    fun iconSize(size: FabSize = FabSize.Medium): Int {
        return when (size) {
            FabSize.Small -> 20.dp
            FabSize.Medium -> 24.dp
            FabSize.Large -> 36.dp
        }
    }

    fun cornerRadius(size: FabSize = FabSize.Medium): Int {
        return when (size) {
            FabSize.Small -> 12.dp
            FabSize.Medium -> 16.dp
            FabSize.Large -> 28.dp
        }
    }

    fun elevation(): Int = 6.dp

    fun extendedHeight(): Int = 56.dp

    fun extendedCornerRadius(): Int = 16.dp

    fun extendedHorizontalPadding(): Int = 16.dp

    fun extendedIconSpacing(): Int = 8.dp

    fun extendedTextStyle(): UiTextStyle = Theme.typography.label

    fun pressedColor(): Int = Theme.colors.ripple
}
