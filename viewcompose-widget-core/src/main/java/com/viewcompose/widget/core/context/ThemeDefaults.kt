package com.viewcompose.widget.core

object UiShapeDefaults {
    fun default(): UiShapes {
        return UiShapes(
            cardCornerRadius = 20.dp,
            interactiveCornerRadius = 14.dp,
        )
    }
}

object UiControlSizeDefaults {
    fun default(): UiControlSizing {
        return UiControlSizing(
            button = UiButtonSizing(
                compactHeight = 36.dp,
                mediumHeight = 44.dp,
                largeHeight = 52.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 16.dp,
                largeHorizontalPadding = 20.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            textField = UiTextFieldSizing(
                compactHeight = 40.dp,
                mediumHeight = 48.dp,
                largeHeight = 56.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 14.dp,
                largeHorizontalPadding = 16.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            segmentedControl = UiSegmentedControlSizing(
                compactHeight = 36.dp,
                mediumHeight = 42.dp,
                largeHeight = 48.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 14.dp,
                largeHorizontalPadding = 18.dp,
                compactVerticalPadding = 6.dp,
                mediumVerticalPadding = 8.dp,
                largeVerticalPadding = 10.dp,
            ),
            progressIndicator = UiProgressIndicatorSizing(
                linearTrackThickness = 6.dp,
                circularSize = 32.dp,
                circularTrackThickness = 4.dp,
            ),
        )
    }
}

object UiOverlayDefaults {
    fun default(): UiOverlays {
        return UiOverlays(
            scrimOpacity = 0.32f,
        )
    }
}

object UiThemeDefaults {
    fun light(): UiThemeTokens {
        return UiThemeTokens(
            colors = UiColors(
                background = 0xFFF4F1EA.toInt(),
                surface = 0xFFE6D9C6.toInt(),
                surfaceVariant = 0xFFFDECC8.toInt(),
                primary = 0xFFBFD8A6.toInt(),
                secondary = 0xFFD6C6F0.toInt(),
                error = 0xFFB3261E.toInt(),
                success = 0xFF2E7D32.toInt(),
                warning = 0xFFF57C00.toInt(),
                info = 0xFF1565C0.toInt(),
                divider = 0xFFD8CCBA.toInt(),
                textPrimary = 0xFF2F241B.toInt(),
                textSecondary = 0xFF6A5A4A.toInt(),
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 24.sp),
                body = UiTextStyle(fontSizeSp = 16.sp),
                label = UiTextStyle(fontSizeSp = 14.sp),
            ),
        )
    }

    fun dark(): UiThemeTokens {
        return UiThemeTokens(
            colors = UiColors(
                background = 0xFF1F1B18.toInt(),
                surface = 0xFF2C2621.toInt(),
                surfaceVariant = 0xFF3A332D.toInt(),
                primary = 0xFF7EA16D.toInt(),
                secondary = 0xFF8B7AA8.toInt(),
                error = 0xFFF2B8B5.toInt(),
                success = 0xFF81C784.toInt(),
                warning = 0xFFFBC02D.toInt(),
                info = 0xFF64B5F6.toInt(),
                divider = 0xFF51473E.toInt(),
                textPrimary = 0xFFF4EFE8.toInt(),
                textSecondary = 0xFFD0C4B6.toInt(),
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 24.sp),
                body = UiTextStyle(fontSizeSp = 16.sp),
                label = UiTextStyle(fontSizeSp = 14.sp),
            ),
        )
    }
}
