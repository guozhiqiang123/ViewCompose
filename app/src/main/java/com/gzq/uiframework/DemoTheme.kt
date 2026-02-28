package com.gzq.uiframework

import android.content.Context
import android.content.res.Configuration
import com.gzq.uiframework.widget.core.UiColors
import com.gzq.uiframework.widget.core.UiControlSizing
import com.gzq.uiframework.widget.core.UiButtonSizing
import com.gzq.uiframework.widget.core.UiInputColors
import com.gzq.uiframework.widget.core.UiInteractionColors
import com.gzq.uiframework.widget.core.UiProgressIndicatorSizing
import com.gzq.uiframework.widget.core.UiSegmentedControlSizing
import com.gzq.uiframework.widget.core.UiShapes
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTextFieldSizing
import com.gzq.uiframework.widget.core.UiThemeTokens
import com.gzq.uiframework.widget.core.UiTypography
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.sp

enum class DemoThemeMode {
    System,
    Light,
    Dark,
}

object DemoThemeTokens {
    val light: UiThemeTokens = UiThemeTokens(
        colors = UiColors(
            background = 0xFFF7F2EA.toInt(),
            surface = 0xFFEFE4D2.toInt(),
            surfaceVariant = 0xFFF8EED8.toInt(),
            primary = 0xFF7B9E68.toInt(),
            accent = 0xFF9A7AAE.toInt(),
            divider = 0xFFCCBDAA.toInt(),
            textPrimary = 0xFF2F241B.toInt(),
            textSecondary = 0xFF6A5A4A.toInt(),
        ),
        typography = UiTypography(
            title = UiTextStyle(fontSizeSp = 30.sp),
            body = UiTextStyle(fontSizeSp = 16.sp),
            label = UiTextStyle(fontSizeSp = 14.sp),
        ),
        input = UiInputColors(
            fieldContainer = 0xFFFFFFFF.toInt(),
            fieldContainerDisabled = 0xFFE7DCCB.toInt(),
            fieldError = 0xFFB34B3D.toInt(),
            fieldText = 0xFF2F241B.toInt(),
            fieldTextDisabled = 0xFF8D7E6F.toInt(),
            fieldHint = 0xFF6A5A4A.toInt(),
            fieldHintDisabled = 0xFFB7A897.toInt(),
            control = 0xFF7B9E68.toInt(),
            controlDisabled = 0xFFCCBDAA.toInt(),
        ),
        shapes = UiShapes(
            cardCornerRadius = 24.dp,
            controlCornerRadius = 18.dp,
        ),
        controls = UiControlSizing(
            button = UiButtonSizing(
                compactHeight = 38.dp,
                mediumHeight = 46.dp,
                largeHeight = 54.dp,
                compactHorizontalPadding = 14.dp,
                mediumHorizontalPadding = 18.dp,
                largeHorizontalPadding = 22.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            textField = UiTextFieldSizing(
                compactHeight = 42.dp,
                mediumHeight = 50.dp,
                largeHeight = 58.dp,
                compactHorizontalPadding = 14.dp,
                mediumHorizontalPadding = 16.dp,
                largeHorizontalPadding = 18.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            segmentedControl = UiSegmentedControlSizing(
                compactHeight = 38.dp,
                mediumHeight = 44.dp,
                largeHeight = 50.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 14.dp,
                largeHorizontalPadding = 18.dp,
                compactVerticalPadding = 6.dp,
                mediumVerticalPadding = 8.dp,
                largeVerticalPadding = 10.dp,
            ),
            progressIndicator = UiProgressIndicatorSizing(
                linearTrackThickness = 6.dp,
                circularSize = 36.dp,
                circularTrackThickness = 4.dp,
            ),
        ),
        interactions = UiInteractionColors(
            pressedOverlay = 0x1A2F241B,
        ),
    )

    val dark: UiThemeTokens = UiThemeTokens(
        colors = UiColors(
            background = 0xFF1F1B18.toInt(),
            surface = 0xFF2C2621.toInt(),
            surfaceVariant = 0xFF3A332D.toInt(),
            primary = 0xFF98C27F.toInt(),
            accent = 0xFFB39AC9.toInt(),
            divider = 0xFF5B5046.toInt(),
            textPrimary = 0xFFF4EFE8.toInt(),
            textSecondary = 0xFFD0C4B6.toInt(),
        ),
        typography = UiTypography(
            title = UiTextStyle(fontSizeSp = 30.sp),
            body = UiTextStyle(fontSizeSp = 16.sp),
            label = UiTextStyle(fontSizeSp = 14.sp),
        ),
        input = UiInputColors(
            fieldContainer = 0xFF332D28.toInt(),
            fieldContainerDisabled = 0xFF2A2521.toInt(),
            fieldError = 0xFFFF8A7A.toInt(),
            fieldText = 0xFFF4EFE8.toInt(),
            fieldTextDisabled = 0xFFA59689.toInt(),
            fieldHint = 0xFFD0C4B6.toInt(),
            fieldHintDisabled = 0xFF7D7168.toInt(),
            control = 0xFF98C27F.toInt(),
            controlDisabled = 0xFF5B5046.toInt(),
        ),
        shapes = UiShapes(
            cardCornerRadius = 24.dp,
            controlCornerRadius = 18.dp,
        ),
        controls = UiControlSizing(
            button = UiButtonSizing(
                compactHeight = 38.dp,
                mediumHeight = 46.dp,
                largeHeight = 54.dp,
                compactHorizontalPadding = 14.dp,
                mediumHorizontalPadding = 18.dp,
                largeHorizontalPadding = 22.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            textField = UiTextFieldSizing(
                compactHeight = 42.dp,
                mediumHeight = 50.dp,
                largeHeight = 58.dp,
                compactHorizontalPadding = 14.dp,
                mediumHorizontalPadding = 16.dp,
                largeHorizontalPadding = 18.dp,
                compactVerticalPadding = 8.dp,
                mediumVerticalPadding = 10.dp,
                largeVerticalPadding = 12.dp,
            ),
            segmentedControl = UiSegmentedControlSizing(
                compactHeight = 38.dp,
                mediumHeight = 44.dp,
                largeHeight = 50.dp,
                compactHorizontalPadding = 12.dp,
                mediumHorizontalPadding = 14.dp,
                largeHorizontalPadding = 18.dp,
                compactVerticalPadding = 6.dp,
                mediumVerticalPadding = 8.dp,
                largeVerticalPadding = 10.dp,
            ),
            progressIndicator = UiProgressIndicatorSizing(
                linearTrackThickness = 6.dp,
                circularSize = 36.dp,
                circularTrackThickness = 4.dp,
            ),
        ),
        interactions = UiInteractionColors(
            pressedOverlay = 0x26F4EFE8,
        ),
    )

    fun resolve(
        mode: DemoThemeMode,
        context: Context,
    ): UiThemeTokens {
        return resolve(
            mode = mode,
            isSystemDark = isSystemDark(context),
        )
    }

    fun resolve(
        mode: DemoThemeMode,
        isSystemDark: Boolean,
    ): UiThemeTokens {
        return when (mode) {
            DemoThemeMode.System -> if (isSystemDark) dark else light
            DemoThemeMode.Light -> light
            DemoThemeMode.Dark -> dark
        }
    }

    fun modeLabel(
        mode: DemoThemeMode,
        context: Context,
    ): String {
        return modeLabel(
            mode = mode,
            isSystemDark = isSystemDark(context),
        )
    }

    fun modeLabel(
        mode: DemoThemeMode,
        isSystemDark: Boolean,
    ): String {
        return when (mode) {
            DemoThemeMode.System -> if (isSystemDark) "System (Dark)" else "System (Light)"
            DemoThemeMode.Light -> "Light"
            DemoThemeMode.Dark -> "Dark"
        }
    }

    fun isSystemDark(context: Context): Boolean {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
