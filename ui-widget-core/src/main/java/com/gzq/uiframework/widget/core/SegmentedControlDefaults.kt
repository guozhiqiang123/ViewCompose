package com.gzq.uiframework.widget.core

enum class SegmentedControlSize {
    Compact,
    Medium,
    Large,
}

object SegmentedControlDefaults {
    fun backgroundColor(): Int = Theme.colors.surfaceVariant

    fun indicatorColor(): Int = Theme.colors.primary

    fun cornerRadius(): Int = Theme.shapes.controlCornerRadius

    fun textColor(): Int = Theme.colors.textSecondary

    fun selectedTextColor(): Int = contentColorFor(indicatorColor())

    fun rippleColor(): Int = Theme.interactions.pressedOverlay

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
