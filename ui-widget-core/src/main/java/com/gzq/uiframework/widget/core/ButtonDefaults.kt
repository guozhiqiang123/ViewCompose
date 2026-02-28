package com.gzq.uiframework.widget.core

enum class ButtonVariant {
    Primary,
    Secondary,
    Tonal,
    Outlined,
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
