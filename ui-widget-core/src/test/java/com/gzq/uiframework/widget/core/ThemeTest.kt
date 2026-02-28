package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.BorderModifierElement
import com.gzq.uiframework.renderer.modifier.CornerRadiusModifierElement
import com.gzq.uiframework.renderer.modifier.MinHeightModifierElement
import com.gzq.uiframework.renderer.modifier.RippleColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.node.PropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {
    @Test
    fun `text uses current theme text color by default`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                accent = 5,
                divider = 6,
                textPrimary = 7,
                textSecondary = 8,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 12),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Text("Hello")
            }
        }

        val textColor = tree.single()
            .modifier
            .readModifierElements()
            .last { it is TextColorModifierElement } as TextColorModifierElement
        assertEquals(7, textColor.color)
        val textSize = tree.single()
            .modifier
            .readModifierElements()
            .last { it is TextSizeModifierElement } as TextSizeModifierElement
        assertEquals(18, textSize.sizeSp)
    }

    @Test
    fun `divider uses current theme divider color by default`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                accent = 5,
                divider = 42,
                textPrimary = 7,
                textSecondary = 8,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 12),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Divider()
            }
        }

        assertEquals(42, tree.single().props.values[PropKeys.DIVIDER_COLOR])
    }

    @Test
    fun `nested theme overrides parent colors`() {
        val outerTheme = UiThemeDefaults.light()
        val innerTheme = UiThemeDefaults.dark()
        var outerColor = 0
        var innerColor = 0
        var outerSize = 0
        var innerSize = 0

        buildVNodeTree {
            UiTheme(outerTheme) {
                outerColor = Theme.colors.textPrimary
                outerSize = Theme.typography.body.fontSizeSp
                UiTheme(innerTheme) {
                    innerColor = Theme.colors.textPrimary
                    innerSize = Theme.typography.body.fontSizeSp
                }
            }
        }

        assertEquals(outerTheme.colors.textPrimary, outerColor)
        assertEquals(innerTheme.colors.textPrimary, innerColor)
        assertEquals(outerTheme.typography.body.fontSizeSp, outerSize)
        assertEquals(innerTheme.typography.body.fontSizeSp, innerSize)
    }

    @Test
    fun `button uses themed container and readable content colors`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 0xFF102030.toInt(),
                accent = 5,
                divider = 6,
                textPrimary = 7,
                textSecondary = 8,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 12),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Button("Tap")
            }
        }

        val elements = tree.single().modifier.readModifierElements()
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement
        val cornerRadius = elements.last { it is CornerRadiusModifierElement } as CornerRadiusModifierElement
        val rippleColor = elements.last { it is RippleColorModifierElement } as RippleColorModifierElement
        val textColor = elements.last { it is TextColorModifierElement } as TextColorModifierElement
        val textSize = elements.last { it is TextSizeModifierElement } as TextSizeModifierElement
        val minHeight = elements.last { it is MinHeightModifierElement } as MinHeightModifierElement

        assertEquals(customTheme.colors.primary, background.color)
        assertEquals(customTheme.shapes.controlCornerRadius, cornerRadius.radius)
        assertEquals(customTheme.interactions.pressedOverlay, rippleColor.color)
        assertEquals(0xFFFFFFFF.toInt(), textColor.color)
        assertEquals(customTheme.typography.label.fontSizeSp, textSize.sizeSp)
        assertEquals(customTheme.controls.button.mediumHeight, ButtonDefaults.height())
        assertEquals(customTheme.controls.button.mediumHeight, minHeight.minHeight)
    }

    @Test
    fun `outlined button uses transparent container and divider border`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                Button(
                    text = "Outline",
                    variant = ButtonVariant.Outlined,
                )
            }
        }

        val elements = tree.single().modifier.readModifierElements()
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement
        val border = elements.last { it is BorderModifierElement } as BorderModifierElement
        val textColor = elements.last { it is TextColorModifierElement } as TextColorModifierElement

        assertEquals(0x00000000, background.color)
        assertEquals(Theme.colors.divider, border.color)
        assertEquals(1.dp, border.width)
        assertEquals(Theme.colors.textPrimary, textColor.color)
    }

    @Test
    fun `button sizing defaults reflect current theme`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            controls = UiControlSizing(
                button = UiButtonSizing(
                    compactHeight = 31,
                    mediumHeight = 41,
                    largeHeight = 51,
                    compactHorizontalPadding = 11,
                    mediumHorizontalPadding = 21,
                    largeHorizontalPadding = 31,
                    compactVerticalPadding = 6,
                    mediumVerticalPadding = 8,
                    largeVerticalPadding = 10,
                ),
                textField = UiControlSizeDefaults.default().textField,
                segmentedControl = UiControlSizeDefaults.default().segmentedControl,
            ),
        )
        var compactHeight = 0
        var largeHorizontalPadding = 0
        var largeStyle = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                compactHeight = ButtonDefaults.height(ButtonSize.Compact)
                largeHorizontalPadding = ButtonDefaults.horizontalPadding(ButtonSize.Large)
                largeStyle = ButtonDefaults.textStyle(ButtonSize.Large).fontSizeSp
            }
        }

        assertEquals(31, compactHeight)
        assertEquals(31, largeHorizontalPadding)
        assertEquals(customTheme.typography.body.fontSizeSp, largeStyle)
    }

    @Test
    fun `text defaults reflect current theme`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                accent = 5,
                divider = 6,
                textPrimary = 70,
                textSecondary = 80,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 31),
                body = UiTextStyle(fontSizeSp = 19),
                label = UiTextStyle(fontSizeSp = 13),
            ),
        )
        var primaryColor = 0
        var secondaryColor = 0
        var titleSize = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                primaryColor = TextDefaults.primaryColor()
                secondaryColor = TextDefaults.secondaryColor()
                titleSize = TextDefaults.titleStyle().fontSizeSp
            }
        }

        assertEquals(customTheme.colors.textPrimary, primaryColor)
        assertEquals(customTheme.colors.textSecondary, secondaryColor)
        assertEquals(customTheme.typography.title.fontSizeSp, titleSize)
    }

    @Test
    fun `surface and divider defaults reflect current theme`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 22,
                surfaceVariant = 33,
                primary = 4,
                accent = 5,
                divider = 66,
                textPrimary = 70,
                textSecondary = 80,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 31),
                body = UiTextStyle(fontSizeSp = 19),
                label = UiTextStyle(fontSizeSp = 13),
            ),
        )
        var surface = 0
        var surfaceVariant = 0
        var divider = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                surface = SurfaceDefaults.backgroundColor()
                surfaceVariant = SurfaceDefaults.variantBackgroundColor()
                divider = DividerDefaults.color()
            }
        }

        assertEquals(customTheme.colors.surface, surface)
        assertEquals(customTheme.colors.surfaceVariant, surfaceVariant)
        assertEquals(customTheme.colors.divider, divider)
    }

    @Test
    fun `input defaults reflect current theme`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 22,
                surfaceVariant = 33,
                primary = 44,
                accent = 55,
                divider = 66,
                textPrimary = 77,
                textSecondary = 88,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 31),
                body = UiTextStyle(fontSizeSp = 19),
                label = UiTextStyle(fontSizeSp = 13),
            ),
            input = UiInputColors(
                fieldContainer = 101,
                fieldContainerDisabled = 102,
                fieldError = 103,
                fieldText = 104,
                fieldTextDisabled = 105,
                fieldHint = 106,
                fieldHintDisabled = 107,
                control = 108,
                controlDisabled = 109,
            ),
        )
        var container = 0
        var disabledContainer = 0
        var errorColor = 0
        var controlColor = 0
        var cardCornerRadius = 0
        var pressedColor = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                container = TextFieldDefaults.containerColor()
                disabledContainer = TextFieldDefaults.containerColor(enabled = false)
                errorColor = TextFieldDefaults.hintColor(isError = true)
                controlColor = InputControlDefaults.controlColor()
                cardCornerRadius = SurfaceDefaults.cardCornerRadius()
                pressedColor = SurfaceDefaults.pressedColor()
            }
        }

        assertEquals(101, container)
        assertEquals(102, disabledContainer)
        assertEquals(103, errorColor)
        assertEquals(108, controlColor)
        assertEquals(customTheme.shapes.cardCornerRadius, cardCornerRadius)
        assertEquals(customTheme.interactions.pressedOverlay, pressedColor)
    }

    @Test
    fun `text field sizing defaults reflect current theme`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            controls = UiControlSizing(
                button = UiControlSizeDefaults.default().button,
                textField = UiTextFieldSizing(
                    compactHeight = 35,
                    mediumHeight = 45,
                    largeHeight = 55,
                    compactHorizontalPadding = 12,
                    mediumHorizontalPadding = 16,
                    largeHorizontalPadding = 20,
                    compactVerticalPadding = 5,
                    mediumVerticalPadding = 7,
                    largeVerticalPadding = 9,
                ),
                segmentedControl = UiControlSizeDefaults.default().segmentedControl,
            ),
        )
        var compactHeight = 0
        var mediumVerticalPadding = 0
        var compactStyle = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                compactHeight = TextFieldDefaults.height(TextFieldSize.Compact)
                mediumVerticalPadding = TextFieldDefaults.verticalPadding(TextFieldSize.Medium)
                compactStyle = TextFieldDefaults.textStyle(TextFieldSize.Compact).fontSizeSp
            }
        }

        assertEquals(35, compactHeight)
        assertEquals(7, mediumVerticalPadding)
        assertEquals(customTheme.typography.label.fontSizeSp, compactStyle)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
