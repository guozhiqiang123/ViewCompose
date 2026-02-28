package com.gzq.uiframework.widget.core

enum class ButtonVariant {
    Primary,
    Secondary,
    Tonal,
    Outlined,
}

enum class ButtonSize {
    Compact,
    Medium,
    Large,
}

object ButtonDefaults {
    fun containerColor(
        variant: ButtonVariant = ButtonVariant.Primary,
    ): Int {
        return when (variant) {
            ButtonVariant.Primary -> Theme.colors.primary
            ButtonVariant.Secondary -> Theme.colors.accent
            ButtonVariant.Tonal -> Theme.colors.surfaceVariant
            ButtonVariant.Outlined -> 0x00000000
        }
    }

    fun contentColor(
        variant: ButtonVariant = ButtonVariant.Primary,
    ): Int {
        return when (variant) {
            ButtonVariant.Tonal,
            ButtonVariant.Outlined,
            -> Theme.colors.textPrimary

            else -> contentColorFor(containerColor(variant))
        }
    }

    fun borderColor(
        variant: ButtonVariant = ButtonVariant.Primary,
    ): Int {
        return when (variant) {
            ButtonVariant.Outlined -> Theme.colors.divider
            else -> 0x00000000
        }
    }

    fun borderWidth(
        variant: ButtonVariant = ButtonVariant.Primary,
    ): Int {
        return when (variant) {
            ButtonVariant.Outlined -> 1.dp
            else -> 0
        }
    }

    fun cornerRadius(): Int = Theme.shapes.controlCornerRadius

    fun height(
        size: ButtonSize = ButtonSize.Medium,
    ): Int {
        return when (size) {
            ButtonSize.Compact -> Theme.controls.button.compactHeight
            ButtonSize.Medium -> Theme.controls.button.mediumHeight
            ButtonSize.Large -> Theme.controls.button.largeHeight
        }
    }

    fun horizontalPadding(
        size: ButtonSize = ButtonSize.Medium,
    ): Int {
        return when (size) {
            ButtonSize.Compact -> Theme.controls.button.compactHorizontalPadding
            ButtonSize.Medium -> Theme.controls.button.mediumHorizontalPadding
            ButtonSize.Large -> Theme.controls.button.largeHorizontalPadding
        }
    }

    fun verticalPadding(
        size: ButtonSize = ButtonSize.Medium,
    ): Int {
        return when (size) {
            ButtonSize.Compact -> Theme.controls.button.compactVerticalPadding
            ButtonSize.Medium -> Theme.controls.button.mediumVerticalPadding
            ButtonSize.Large -> Theme.controls.button.largeVerticalPadding
        }
    }

    fun textStyle(
        size: ButtonSize = ButtonSize.Medium,
    ): UiTextStyle {
        return when (size) {
            ButtonSize.Compact,
            ButtonSize.Medium,
            -> Theme.typography.label

            ButtonSize.Large -> Theme.typography.body
        }
    }

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}

internal fun contentColorFor(backgroundColor: Int): Int {
    val red = backgroundColor shr 16 and 0xFF
    val green = backgroundColor shr 8 and 0xFF
    val blue = backgroundColor and 0xFF
    val luma = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luma >= 186) {
        0xFF000000.toInt()
    } else {
        0xFFFFFFFF.toInt()
    }
}
