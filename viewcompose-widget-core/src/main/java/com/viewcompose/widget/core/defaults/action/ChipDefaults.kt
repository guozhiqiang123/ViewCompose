package com.viewcompose.widget.core

enum class ChipVariant {
    Assist,
    Filter,
    Input,
    Suggestion,
}

object ChipDefaults {
    fun containerColor(
        variant: ChipVariant = ChipVariant.Assist,
        selected: Boolean = false,
        enabled: Boolean = true,
    ): Int {
        return when {
            !enabled -> Theme.colors.surface
            selected && variant == ChipVariant.Filter -> Theme.colors.surfaceVariant
            else -> 0x00000000 // transparent
        }
    }

    fun contentColor(
        variant: ChipVariant = ChipVariant.Assist,
        selected: Boolean = false,
        enabled: Boolean = true,
    ): Int {
        return when {
            !enabled -> Theme.colors.textSecondary
            selected -> Theme.colors.primary
            else -> Theme.colors.textPrimary
        }
    }

    fun borderColor(
        variant: ChipVariant = ChipVariant.Assist,
        selected: Boolean = false,
        enabled: Boolean = true,
    ): Int {
        return when {
            selected && variant == ChipVariant.Filter -> 0x00000000
            else -> Theme.colors.divider
        }
    }

    fun borderWidth(
        variant: ChipVariant = ChipVariant.Assist,
        selected: Boolean = false,
    ): Int {
        return when {
            selected && variant == ChipVariant.Filter -> 0
            else -> 1.dp
        }
    }

    fun cornerRadius(): Int = 8.dp

    fun height(): Int = Theme.controls.chip.height

    fun horizontalPadding(): Int = Theme.controls.chip.horizontalPadding

    fun leadingIconPadding(): Int = Theme.controls.chip.leadingIconPadding

    fun iconSize(): Int = Theme.controls.chip.iconSize

    fun trailingIconSize(): Int = Theme.controls.chip.trailingIconSize

    fun iconSpacing(): Int = Theme.controls.chip.iconSpacing

    fun textStyle(): UiTextStyle = Theme.typography.label

    fun pressedColor(): Int = Theme.colors.ripple
}
