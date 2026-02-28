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
            ButtonVariant.Primary -> Theme.components.button.primaryContainer
            ButtonVariant.Secondary -> Theme.components.button.secondaryContainer
            ButtonVariant.Tonal -> Theme.components.button.tonalContainer
            ButtonVariant.Outlined -> 0x00000000
        }
    }

    fun contentColor(
        variant: ButtonVariant = ButtonVariant.Primary,
    ): Int {
        return when (variant) {
            ButtonVariant.Primary -> Theme.components.button.primaryContent
            ButtonVariant.Secondary -> Theme.components.button.secondaryContent
            ButtonVariant.Tonal -> Theme.components.button.tonalContent
            ButtonVariant.Outlined -> Theme.components.button.outlinedContent
        }
    }

    fun borderColor(
        variant: ButtonVariant = ButtonVariant.Primary,
    ): Int {
        return when (variant) {
            ButtonVariant.Outlined -> Theme.components.button.outlinedBorder
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
