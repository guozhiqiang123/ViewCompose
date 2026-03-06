package com.gzq.uiframework.widget.core

enum class ButtonVariant {
    Primary,
    Secondary,
    Tonal,
    Outlined,
    Text,
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
        val override = LocalContext.current(LocalButtonColors)
        return when (variant) {
            ButtonVariant.Primary -> if (enabled) {
                override?.primaryContainer ?: Theme.colors.primary
            } else {
                override?.primaryDisabledContainer ?: Theme.colors.divider
            }

            ButtonVariant.Secondary -> if (enabled) {
                override?.secondaryContainer ?: Theme.colors.accent
            } else {
                override?.secondaryDisabledContainer ?: Theme.colors.divider
            }

            ButtonVariant.Tonal -> if (enabled) {
                override?.tonalContainer ?: Theme.colors.surfaceVariant
            } else {
                override?.tonalDisabledContainer ?: Theme.colors.surfaceVariant
            }

            ButtonVariant.Outlined -> 0x00000000

            ButtonVariant.Text -> 0x00000000
        }
    }

    fun contentColor(
        variant: ButtonVariant = ButtonVariant.Primary,
        enabled: Boolean = true,
    ): Int {
        val override = LocalContext.current(LocalButtonColors)
        return when (variant) {
            ButtonVariant.Primary -> if (enabled) {
                override?.primaryContent ?: contentColorFor(Theme.colors.primary)
            } else {
                override?.primaryDisabledContent ?: Theme.colors.textSecondary
            }

            ButtonVariant.Secondary -> if (enabled) {
                override?.secondaryContent ?: contentColorFor(Theme.colors.accent)
            } else {
                override?.secondaryDisabledContent ?: Theme.colors.textSecondary
            }

            ButtonVariant.Tonal -> if (enabled) {
                override?.tonalContent ?: Theme.colors.textPrimary
            } else {
                override?.tonalDisabledContent ?: Theme.colors.textSecondary
            }

            ButtonVariant.Outlined -> if (enabled) {
                override?.outlinedContent ?: Theme.colors.textPrimary
            } else {
                override?.outlinedDisabledContent ?: Theme.colors.textSecondary
            }

            ButtonVariant.Text -> if (enabled) {
                Theme.colors.primary
            } else {
                Theme.colors.textSecondary
            }
        }
    }

    fun borderColor(
        variant: ButtonVariant = ButtonVariant.Primary,
        enabled: Boolean = true,
    ): Int {
        val override = LocalContext.current(LocalButtonColors)
        return when (variant) {
            ButtonVariant.Outlined -> if (enabled) {
                override?.outlinedBorder ?: Theme.colors.divider
            } else {
                override?.outlinedDisabledBorder ?: Theme.colors.divider
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

    fun pressedColor(): Int = Theme.colors.ripple
}
