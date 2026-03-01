package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.AlphaModifierElement
import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.BorderModifierElement
import com.gzq.uiframework.renderer.modifier.CornerRadiusModifierElement
import com.gzq.uiframework.renderer.modifier.MinHeightModifierElement
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.RippleColorModifierElement
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.node.NodeType
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

        assertEquals(7, tree.single().props.values[PropKeys.TEXT_COLOR])
        assertEquals(18, tree.single().props.values[PropKeys.TEXT_SIZE_SP])
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
    fun `theme override replaces only requested domain`() {
        val baseTheme = UiThemeDefaults.light()
        var primary = 0
        var bodySize = 0
        var controlColor = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = baseTheme.colors.copy(primary = 0xFF225577.toInt()),
                ) {
                    primary = Theme.colors.primary
                    bodySize = Theme.typography.body.fontSizeSp
                    controlColor = Theme.input.control
                }
            }
        }

        assertEquals(0xFF225577.toInt(), primary)
        assertEquals(baseTheme.typography.body.fontSizeSp, bodySize)
        assertEquals(baseTheme.input.control, controlColor)
    }

    @Test
    fun `nested theme override prefers innermost values`() {
        val baseTheme = UiThemeDefaults.light()
        var resolvedPrimary = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = baseTheme.colors.copy(primary = 0xFF111111.toInt()),
                ) {
                    UiThemeOverride(
                        colors = Theme.colors.copy(primary = 0xFF222222.toInt()),
                    ) {
                        resolvedPrimary = Theme.colors.primary
                    }
                }
            }
        }

        assertEquals(0xFF222222.toInt(), resolvedPrimary)
    }

    @Test
    fun `theme override scope restores parent theme after exit`() {
        val baseTheme = UiThemeDefaults.light()
        var insidePrimary = 0
        var outsidePrimary = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = baseTheme.colors.copy(primary = 0xFF335577.toInt()),
                ) {
                    insidePrimary = Theme.colors.primary
                }
                outsidePrimary = Theme.colors.primary
            }
        }

        assertEquals(0xFF335577.toInt(), insidePrimary)
        assertEquals(baseTheme.colors.primary, outsidePrimary)
    }

    @Test
    fun `builder theme override updates selected domains`() {
        val baseTheme = UiThemeDefaults.light()
        var resolvedPrimary = 0
        var resolvedCorner = 0
        var unchangedBody = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = { copy(primary = 0xFF445566.toInt()) },
                    shapes = { copy(controlCornerRadius = 77) },
                ) {
                    resolvedPrimary = Theme.colors.primary
                    resolvedCorner = Theme.shapes.controlCornerRadius
                    unchangedBody = Theme.typography.body.fontSizeSp
                }
            }
        }

        assertEquals(0xFF445566.toInt(), resolvedPrimary)
        assertEquals(77, resolvedCorner)
        assertEquals(baseTheme.typography.body.fontSizeSp, unchangedBody)
    }

    @Test
    fun `builder theme override composes with object override`() {
        val baseTheme = UiThemeDefaults.light()
        var resolvedPrimary = 0
        var resolvedPressed = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = baseTheme.colors.copy(primary = 0xFF111111.toInt()),
                ) {
                    UiThemeOverride(
                        colors = { copy(primary = 0xFF222222.toInt()) },
                        interactions = { copy(pressedOverlay = 0x33445566) },
                    ) {
                        resolvedPrimary = Theme.colors.primary
                        resolvedPressed = Theme.interactions.pressedOverlay
                    }
                }
            }
        }

        assertEquals(0xFF222222.toInt(), resolvedPrimary)
        assertEquals(0x33445566, resolvedPressed)
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
        val minHeight = elements.last { it is MinHeightModifierElement } as MinHeightModifierElement

        assertEquals(customTheme.colors.primary, background.color)
        assertEquals(customTheme.shapes.controlCornerRadius, cornerRadius.radius)
        assertEquals(customTheme.interactions.pressedOverlay, rippleColor.color)
        assertEquals(0xFFFFFFFF.toInt(), tree.single().props.values[PropKeys.TEXT_COLOR])
        assertEquals(customTheme.typography.label.fontSizeSp, tree.single().props.values[PropKeys.TEXT_SIZE_SP])
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

        assertEquals(0x00000000, background.color)
        assertEquals(Theme.colors.divider, border.color)
        assertEquals(1.dp, border.width)
        assertEquals(Theme.colors.textPrimary, tree.single().props.values[PropKeys.TEXT_COLOR])
    }

    @Test
    fun `disabled button emits enabled prop and themed disabled styles`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = UiButtonStyles(
                    primaryContainer = 101,
                    primaryContent = 102,
                    primaryDisabledContainer = 103,
                    primaryDisabledContent = 104,
                    secondaryContainer = 105,
                    secondaryContent = 106,
                    secondaryDisabledContainer = 107,
                    secondaryDisabledContent = 108,
                    tonalContainer = 109,
                    tonalContent = 110,
                    tonalDisabledContainer = 111,
                    tonalDisabledContent = 112,
                    outlinedContent = 113,
                    outlinedBorder = 114,
                    outlinedDisabledContent = 115,
                    outlinedDisabledBorder = 116,
                ),
                textField = baseTheme.components.textField,
                segmentedControl = baseTheme.components.segmentedControl,
                checkbox = baseTheme.components.checkbox,
                switchControl = baseTheme.components.switchControl,
                radioButton = baseTheme.components.radioButton,
                slider = baseTheme.components.slider,
                progressIndicator = baseTheme.components.progressIndicator,
                tabPager = baseTheme.components.tabPager,
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Button(
                    text = "Disabled",
                    enabled = false,
                )
            }
        }

        val node = tree.single()
        val elements = node.modifier.readModifierElements()
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement

        assertEquals(false, node.props.values[PropKeys.ENABLED])
        assertEquals(103, background.color)
        assertEquals(104, node.props.values[PropKeys.TEXT_COLOR])
    }

    @Test
    fun `explicit button modifier overrides theme override background`() {
        val overriddenPrimary = 0xFF123456.toInt()
        val explicitBackground = 0xFF654321.toInt()

        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                UiThemeOverride(
                    colors = Theme.colors.copy(primary = overriddenPrimary),
                ) {
                    Button(
                        text = "Override",
                        modifier = Modifier
                            .backgroundColor(explicitBackground),
                    )
                }
            }
        }

        val elements = tree.single().modifier.readModifierElements()
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement

        assertEquals(explicitBackground, background.color)
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
                progressIndicator = UiControlSizeDefaults.default().progressIndicator,
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
    fun `component style defaults reflect current theme`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            components = UiComponentStyles(
                button = UiButtonStyles(
                    primaryContainer = 101,
                    primaryContent = 102,
                    primaryDisabledContainer = 109,
                    primaryDisabledContent = 110,
                    secondaryContainer = 103,
                    secondaryContent = 104,
                    secondaryDisabledContainer = 111,
                    secondaryDisabledContent = 112,
                    tonalContainer = 105,
                    tonalContent = 106,
                    tonalDisabledContainer = 113,
                    tonalDisabledContent = 114,
                    outlinedContent = 107,
                    outlinedBorder = 108,
                    outlinedDisabledContent = 115,
                    outlinedDisabledBorder = 116,
                ),
                textField = UiTextFieldStyles(
                    filledContainer = 201,
                    filledDisabledContainer = 204,
                    filledErrorContainer = 205,
                    tonalContainer = 202,
                    tonalDisabledContainer = 206,
                    tonalErrorContainer = 207,
                    outlinedBorder = 203,
                    outlinedDisabledBorder = 208,
                    outlinedErrorBorder = 209,
                ),
                checkbox = UiCheckboxStyles(
                    label = 210,
                    labelDisabled = 211,
                    control = 212,
                    controlDisabled = 213,
                ),
                switchControl = UiSwitchStyles(
                    label = 214,
                    labelDisabled = 215,
                    control = 216,
                    controlDisabled = 217,
                ),
                radioButton = UiRadioButtonStyles(
                    label = 218,
                    labelDisabled = 219,
                    control = 220,
                    controlDisabled = 221,
                ),
                slider = UiSliderStyles(
                    control = 222,
                    controlDisabled = 223,
                ),
                progressIndicator = UiProgressIndicatorStyles(
                    linearIndicator = 224,
                    linearTrack = 225,
                    circularIndicator = 226,
                    circularTrack = 227,
                ),
                segmentedControl = UiSegmentedControlStyles(
                    background = 301,
                    backgroundDisabled = 305,
                    indicator = 306,
                    indicatorDisabled = 307,
                    text = 308,
                    textDisabled = 309,
                    selectedText = 310,
                    selectedTextDisabled = 311,
                ),
                tabPager = UiTabPagerStyles(
                    background = 312,
                    indicator = 313,
                    text = 314,
                    selectedText = 315,
                ),
            ),
        )
        var buttonTonal = 0
        var outlinedBorder = 0
        var disabledPrimary = 0
        var textFieldTonal = 0
        var textFieldError = 0
        var checkboxDisabled = 0
        var sliderDisabled = 0
        var linearProgressTrack = 0
        var segmentedIndicator = 0
        var segmentedDisabledText = 0
        var tabPagerText = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                buttonTonal = ButtonDefaults.containerColor(ButtonVariant.Tonal)
                outlinedBorder = ButtonDefaults.borderColor(ButtonVariant.Outlined)
                disabledPrimary = ButtonDefaults.containerColor(ButtonVariant.Primary, enabled = false)
                textFieldTonal = TextFieldDefaults.containerColor(TextFieldVariant.Tonal)
                textFieldError = TextFieldDefaults.borderColor(TextFieldVariant.Outlined, isError = true)
                checkboxDisabled = InputControlDefaults.checkboxControlColor(enabled = false)
                sliderDisabled = InputControlDefaults.sliderControlColor(enabled = false)
                linearProgressTrack = ProgressIndicatorDefaults.linearTrackColor()
                segmentedIndicator = SegmentedControlDefaults.indicatorColor()
                segmentedDisabledText = SegmentedControlDefaults.textColor(enabled = false)
                tabPagerText = TabPagerDefaults.unselectedTextColor()
            }
        }

        assertEquals(105, buttonTonal)
        assertEquals(108, outlinedBorder)
        assertEquals(109, disabledPrimary)
        assertEquals(202, textFieldTonal)
        assertEquals(209, textFieldError)
        assertEquals(213, checkboxDisabled)
        assertEquals(223, sliderDisabled)
        assertEquals(225, linearProgressTrack)
        assertEquals(306, segmentedIndicator)
        assertEquals(309, segmentedDisabledText)
        assertEquals(314, tabPagerText)
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
    fun `surface emits semantic node with themed defaults`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                Surface(
                    variant = SurfaceVariant.Variant,
                    enabled = false,
                ) {
                    Text("Inside")
                }
            }
        }

        val surface = tree.single()
        val elements = surface.modifier.readModifierElements()
        val background = elements.last { it is BackgroundColorModifierElement } as BackgroundColorModifierElement
        val cornerRadius = elements.last { it is CornerRadiusModifierElement } as CornerRadiusModifierElement
        val ripple = elements.last { it is RippleColorModifierElement } as RippleColorModifierElement
        val alpha = elements.last { it is AlphaModifierElement } as AlphaModifierElement

        assertEquals(NodeType.Surface, surface.type)
        assertEquals(SurfaceDefaults.variantBackgroundColor(), background.color)
        assertEquals(SurfaceDefaults.cardCornerRadius(), cornerRadius.radius)
        assertEquals(SurfaceDefaults.pressedColor(), ripple.color)
        assertEquals(SurfaceDefaults.disabledAlpha(), alpha.alpha, 0.0f)
    }

    @Test
    fun `surface provides local content color to nested text`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                Surface(
                    variant = SurfaceVariant.Variant,
                ) {
                    Text("Inside")
                }
            }
        }

        val text = tree.single().children.single()

        assertEquals(SurfaceDefaults.variantContentColor(), text.props.values[PropKeys.TEXT_COLOR])
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
                controlColor = InputControlDefaults.checkboxControlColor()
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
                progressIndicator = UiControlSizeDefaults.default().progressIndicator,
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

    @Test
    fun `theme current exposes fully overridden tokens`() {
        val baseTheme = UiThemeDefaults.light()
        val overrideShapes = baseTheme.shapes.copy(controlCornerRadius = 99)
        val overrideInteractions = baseTheme.interactions.copy(pressedOverlay = 0x12345678)
        var current: UiThemeTokens? = null

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    shapes = overrideShapes,
                    interactions = overrideInteractions,
                ) {
                    current = Theme.current
                }
            }
        }

        assertEquals(overrideShapes, current?.shapes)
        assertEquals(overrideInteractions, current?.interactions)
        assertEquals(baseTheme.colors, current?.colors)
    }

    @Test
    fun `component style override only changes targeted component domain`() {
        val baseTheme = UiThemeDefaults.light()
        var buttonPrimary = 0
        var segmentedIndicator = 0
        var disabledControl = 0
        var baseTextField = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    components = {
                        copy(
                            button = button.copy(
                                primaryContainer = 0xFF778899.toInt(),
                            ),
                            checkbox = checkbox.copy(
                                controlDisabled = 0xFF556677.toInt(),
                            ),
                            segmentedControl = segmentedControl.copy(
                                indicator = 0xFF998877.toInt(),
                            ),
                        )
                    },
                ) {
                    buttonPrimary = ButtonDefaults.containerColor(ButtonVariant.Primary)
                    segmentedIndicator = SegmentedControlDefaults.indicatorColor()
                    disabledControl = InputControlDefaults.checkboxControlColor(enabled = false)
                    baseTextField = TextFieldDefaults.containerColor(TextFieldVariant.Filled)
                }
            }
        }

        assertEquals(0xFF778899.toInt(), buttonPrimary)
        assertEquals(0xFF998877.toInt(), segmentedIndicator)
        assertEquals(0xFF556677.toInt(), disabledControl)
        assertEquals(baseTheme.components.textField.filledContainer, baseTextField)
    }

    @Test
    fun `component style override supports disabled button and text field states`() {
        val baseTheme = UiThemeDefaults.light()
        var disabledButton = 0
        var disabledField = 0
        var errorField = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    components = {
                        copy(
                            button = button.copy(
                                primaryDisabledContainer = 0xFF111122.toInt(),
                            ),
                            textField = textField.copy(
                                filledDisabledContainer = 0xFF222233.toInt(),
                                outlinedErrorBorder = 0xFF333344.toInt(),
                            ),
                        )
                    },
                ) {
                    disabledButton = ButtonDefaults.containerColor(
                        variant = ButtonVariant.Primary,
                        enabled = false,
                    )
                    disabledField = TextFieldDefaults.containerColor(
                        variant = TextFieldVariant.Filled,
                        enabled = false,
                    )
                    errorField = TextFieldDefaults.borderColor(
                        variant = TextFieldVariant.Outlined,
                        isError = true,
                    )
                }
            }
        }

        assertEquals(0xFF111122.toInt(), disabledButton)
        assertEquals(0xFF222233.toInt(), disabledField)
        assertEquals(0xFF333344.toInt(), errorField)
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.gzq.uiframework.renderer.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
