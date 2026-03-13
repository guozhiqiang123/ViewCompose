package com.viewcompose.widget.core

enum class TypographyTier {
    Large,
    Medium,
    Small,
}

object TextDefaults {
    fun currentStyle(): UiTextStyle = TextStyle.current

    fun titleStyle(tier: TypographyTier = TypographyTier.Medium): UiTextStyle {
        return when (tier) {
            TypographyTier.Large -> Theme.typography.titleLarge
            TypographyTier.Medium -> Theme.typography.titleMedium
            TypographyTier.Small -> Theme.typography.titleSmall
        }
    }

    fun bodyStyle(tier: TypographyTier = TypographyTier.Medium): UiTextStyle {
        return when (tier) {
            TypographyTier.Large -> Theme.typography.bodyLarge
            TypographyTier.Medium -> Theme.typography.bodyMedium
            TypographyTier.Small -> Theme.typography.bodySmall
        }
    }

    fun labelStyle(tier: TypographyTier = TypographyTier.Medium): UiTextStyle {
        return when (tier) {
            TypographyTier.Large -> Theme.typography.labelLarge
            TypographyTier.Medium -> Theme.typography.labelMedium
            TypographyTier.Small -> Theme.typography.labelSmall
        }
    }

    fun titleLargeStyle(): UiTextStyle = titleStyle(TypographyTier.Large)

    fun titleMediumStyle(): UiTextStyle = titleStyle(TypographyTier.Medium)

    fun titleSmallStyle(): UiTextStyle = titleStyle(TypographyTier.Small)

    fun bodyLargeStyle(): UiTextStyle = bodyStyle(TypographyTier.Large)

    fun bodyMediumStyle(): UiTextStyle = bodyStyle(TypographyTier.Medium)

    fun bodySmallStyle(): UiTextStyle = bodyStyle(TypographyTier.Small)

    fun labelLargeStyle(): UiTextStyle = labelStyle(TypographyTier.Large)

    fun labelMediumStyle(): UiTextStyle = labelStyle(TypographyTier.Medium)

    fun labelSmallStyle(): UiTextStyle = labelStyle(TypographyTier.Small)

    fun primaryColor(): Int = ContentColor.current

    fun secondaryColor(): Int = Theme.colors.onSurfaceVariant
}
