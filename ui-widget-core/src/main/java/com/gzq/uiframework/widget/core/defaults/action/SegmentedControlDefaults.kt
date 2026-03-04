package com.gzq.uiframework.widget.core

enum class SegmentedControlSize {
    Compact,
    Medium,
    Large,
}

object SegmentedControlDefaults {
    fun backgroundColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSegmentedControlColors)
        return if (enabled) {
            override?.background ?: Theme.colors.surfaceVariant
        } else {
            override?.backgroundDisabled ?: Theme.colors.surface
        }
    }

    fun indicatorColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSegmentedControlColors)
        return if (enabled) {
            override?.indicator ?: Theme.colors.primary
        } else {
            override?.indicatorDisabled ?: Theme.colors.divider
        }
    }

    fun cornerRadius(): Int = Theme.shapes.controlCornerRadius

    fun textColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSegmentedControlColors)
        return if (enabled) {
            override?.text ?: Theme.colors.textSecondary
        } else {
            override?.textDisabled ?: Theme.colors.textSecondary
        }
    }

    fun selectedTextColor(enabled: Boolean = true): Int {
        val override = LocalContext.current(LocalSegmentedControlColors)
        return if (enabled) {
            override?.selectedText ?: contentColorFor(Theme.colors.primary)
        } else {
            override?.selectedTextDisabled ?: Theme.colors.textSecondary
        }
    }

    fun rippleColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.interactions.pressedOverlay
        } else {
            0x00000000
        }
    }

    fun textStyle(
        size: SegmentedControlSize = SegmentedControlSize.Medium,
    ): UiTextStyle {
        return when (size) {
            SegmentedControlSize.Compact,
            SegmentedControlSize.Medium,
            -> Theme.typography.label

            SegmentedControlSize.Large -> Theme.typography.body
        }
    }

    fun height(
        size: SegmentedControlSize = SegmentedControlSize.Medium,
    ): Int {
        return when (size) {
            SegmentedControlSize.Compact -> Theme.controls.segmentedControl.compactHeight
            SegmentedControlSize.Medium -> Theme.controls.segmentedControl.mediumHeight
            SegmentedControlSize.Large -> Theme.controls.segmentedControl.largeHeight
        }
    }

    fun horizontalPadding(
        size: SegmentedControlSize = SegmentedControlSize.Medium,
    ): Int {
        return when (size) {
            SegmentedControlSize.Compact -> Theme.controls.segmentedControl.compactHorizontalPadding
            SegmentedControlSize.Medium -> Theme.controls.segmentedControl.mediumHorizontalPadding
            SegmentedControlSize.Large -> Theme.controls.segmentedControl.largeHorizontalPadding
        }
    }

    fun verticalPadding(
        size: SegmentedControlSize = SegmentedControlSize.Medium,
    ): Int {
        return when (size) {
            SegmentedControlSize.Compact -> Theme.controls.segmentedControl.compactVerticalPadding
            SegmentedControlSize.Medium -> Theme.controls.segmentedControl.mediumVerticalPadding
            SegmentedControlSize.Large -> Theme.controls.segmentedControl.largeVerticalPadding
        }
    }
}
