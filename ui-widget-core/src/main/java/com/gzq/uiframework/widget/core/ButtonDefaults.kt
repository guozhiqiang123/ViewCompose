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
        enabled: Boolean = true,
    ): Int {
        return when (variant) {
            ButtonVariant.Primary -> if (enabled) {
                Theme.components.button.primaryContainer
            } else {
                Theme.components.button.primaryDisabledContainer
            }

            ButtonVariant.Secondary -> if (enabled) {
                Theme.components.button.secondaryContainer
            } else {
                Theme.components.button.secondaryDisabledContainer
            }

            ButtonVariant.Tonal -> if (enabled) {
                Theme.components.button.tonalContainer
            } else {
                Theme.components.button.tonalDisabledContainer
            }

            ButtonVariant.Outlined -> 0x00000000
        }
    }

    fun contentColor(
        variant: ButtonVariant = ButtonVariant.Primary,
        enabled: Boolean = true,
    ): Int {
        return when (variant) {
            ButtonVariant.Primary -> if (enabled) {
                Theme.components.button.primaryContent
            } else {
                Theme.components.button.primaryDisabledContent
            }

            ButtonVariant.Secondary -> if (enabled) {
                Theme.components.button.secondaryContent
            } else {
                Theme.components.button.secondaryDisabledContent
            }

            ButtonVariant.Tonal -> if (enabled) {
                Theme.components.button.tonalContent
            } else {
                Theme.components.button.tonalDisabledContent
            }

            ButtonVariant.Outlined -> if (enabled) {
                Theme.components.button.outlinedContent
            } else {
                Theme.components.button.outlinedDisabledContent
            }
        }
    }

    fun borderColor(
        variant: ButtonVariant = ButtonVariant.Primary,
        enabled: Boolean = true,
    ): Int {
        return when (variant) {
            ButtonVariant.Outlined -> if (enabled) {
                Theme.components.button.outlinedBorder
            } else {
                Theme.components.button.outlinedDisabledBorder
            }

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

    fun iconSize(
        size: ButtonSize = ButtonSize.Medium,
    ): Int {
        return when (size) {
            ButtonSize.Compact -> 16.dp
            ButtonSize.Medium -> 18.dp
            ButtonSize.Large -> 20.dp
        }
    }

    fun iconSpacing(
        size: ButtonSize = ButtonSize.Medium,
    ): Int {
        return when (size) {
            ButtonSize.Compact -> 6.dp
            ButtonSize.Medium -> 8.dp
            ButtonSize.Large -> 10.dp
        }
    }

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}
