package com.gzq.uiframework.widget.core

enum class SegmentedControlSize {
    Compact,
    Medium,
    Large,
}

object SegmentedControlDefaults {
    fun backgroundColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.components.segmentedControl.background
        } else {
            Theme.components.segmentedControl.backgroundDisabled
        }
    }

    fun indicatorColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.components.segmentedControl.indicator
        } else {
            Theme.components.segmentedControl.indicatorDisabled
        }
    }

    fun cornerRadius(): Int = Theme.shapes.controlCornerRadius

    fun textColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.components.segmentedControl.text
        } else {
            Theme.components.segmentedControl.textDisabled
        }
    }

    fun selectedTextColor(enabled: Boolean = true): Int {
        return if (enabled) {
            Theme.components.segmentedControl.selectedText
        } else {
            Theme.components.segmentedControl.selectedTextDisabled
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
