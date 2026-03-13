package com.viewcompose.widget.core

import com.viewcompose.ui.modifier.AlphaModifierElement
import com.viewcompose.ui.modifier.BackgroundColorModifierElement
import com.viewcompose.ui.modifier.CornerRadiusModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.backgroundColor
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.BoxNodeProps
import com.viewcompose.ui.node.spec.ButtonNodeProps
import com.viewcompose.ui.node.spec.DividerNodeProps
import com.viewcompose.ui.node.spec.TextNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
                secondary = 5,
                error = 9,
                success = 10,
                warning = 11,
                info = 12,
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

        val spec = tree.single().spec as TextNodeProps
        assertEquals(7, spec.textColor)
        assertEquals(18, spec.textSizeSp)
    }

    @Test
    fun `divider uses current theme divider color by default`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                secondary = 5,
                error = 9,
                success = 10,
                warning = 11,
                info = 12,
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

        val spec = tree.single().spec as DividerNodeProps
        assertEquals(42, spec.color)
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
    fun `theme override rebases derived domains when colors change`() {
        val baseTheme = UiThemeDefaults.light()
        var primary = 0
        var bodySize = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = baseTheme.colors.copy(primary = 0xFF225577.toInt()),
                ) {
                    primary = Theme.colors.primary
                    bodySize = Theme.typography.body.fontSizeSp
                }
            }
        }

        assertEquals(0xFF225577.toInt(), primary)
        assertEquals(baseTheme.typography.body.fontSizeSp, bodySize)
    }

    @Test
    fun `defaults derive from colors and follow color override`() {
        val baseTheme = UiThemeDefaults.light()
        var buttonContainer = 0
        var progressIndicator = 0
        var segmentedIndicator = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = baseTheme.colors.copy(primary = 0xFF225577.toInt()),
                ) {
                    buttonContainer = ButtonDefaults.containerColor(ButtonVariant.Primary)
                    progressIndicator = ProgressIndicatorDefaults.linearIndicatorColor()
                    segmentedIndicator = SegmentedControlDefaults.indicatorColor()
                }
            }
        }

        assertEquals(0xFF225577.toInt(), buttonContainer)
        assertEquals(0xFF225577.toInt(), progressIndicator)
        assertEquals(0xFF225577.toInt(), segmentedIndicator)
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
                    shapes = { copy(smallCornerRadius = 77) },
                ) {
                    resolvedPrimary = Theme.colors.primary
                    resolvedCorner = Theme.shapes.smallCornerRadius
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

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    colors = baseTheme.colors.copy(primary = 0xFF111111.toInt()),
                ) {
                    UiThemeOverride(
                        colors = { copy(primary = 0xFF222222.toInt()) },
                    ) {
                        resolvedPrimary = Theme.colors.primary
                    }
                }
            }
        }

        assertEquals(0xFF222222.toInt(), resolvedPrimary)
    }

    @Test
    fun `theme override preserves explicit shape overrides while changing colors`() {
        val baseTheme = UiThemeDefaults.light()
        var cornerRadius = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    shapes = { copy(smallCornerRadius = 99) },
                ) {
                    UiThemeOverride(
                        colors = { copy(primary = 0xFF778899.toInt()) },
                    ) {
                        cornerRadius = Theme.shapes.smallCornerRadius
                    }
                }
            }
        }

        assertEquals(99, cornerRadius)
    }

    @Test
    fun `button uses themed container and readable content colors`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 0xFF102030.toInt(),
                secondary = 5,
                error = 9,
                success = 10,
                warning = 11,
                info = 12,
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

        val spec = tree.single().spec as ButtonNodeProps
        assertEquals(customTheme.colors.primary, spec.backgroundColor)
        assertEquals(customTheme.shapes.smallCornerRadius, spec.cornerRadius)
        assertEquals(pressedOverlayColorFor(customTheme.colors.textPrimary), spec.rippleColor)
        assertEquals(0xFFFFFFFF.toInt(), spec.textColor)
        assertEquals(customTheme.typography.label.fontSizeSp, spec.textSizeSp)
        assertEquals(customTheme.controls.button.mediumHeight, ButtonDefaults.height())
        assertEquals(customTheme.controls.button.mediumHeight, spec.minHeight)
    }

    @Test
    fun `outlined button uses transparent container and semantic outline border`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                Button(
                    text = "Outline",
                    variant = ButtonVariant.Outlined,
                )
            }
        }

        val spec = tree.single().spec as ButtonNodeProps
        assertEquals(0x00000000, spec.backgroundColor)
        assertEquals(Theme.colors.outline, spec.borderColor)
        assertEquals(1.dp, spec.borderWidth)
        assertEquals(Theme.colors.onSurface, spec.textColor)
    }

    @Test
    fun `disabled button emits enabled prop and themed disabled styles`() {
        val baseTheme = UiThemeDefaults.light()

        val tree = buildVNodeTree {
            UiTheme(baseTheme) {
                Button(
                    text = "Disabled",
                    enabled = false,
                )
            }
        }

        val spec = tree.single().spec as ButtonNodeProps

        assertEquals(false, spec.enabled)
        assertEquals(baseTheme.colors.divider, spec.backgroundColor)
        assertEquals(baseTheme.colors.textSecondary, spec.textColor)
    }

    @Test
    fun `button color override changes targeted colors`() {
        val baseTheme = UiThemeDefaults.light()

        val tree = buildVNodeTree {
            UiTheme(baseTheme) {
                ProvideButtonColors(
                    ButtonColorOverride(
                        primaryContainer = 0xFF556677.toInt(),
                        primaryDisabledContainer = 103,
                        primaryDisabledContent = 104,
                    ),
                ) {
                    Button(text = "Custom", enabled = false)
                }
            }
        }

        val spec = tree.single().spec as ButtonNodeProps

        assertEquals(103, spec.backgroundColor)
        assertEquals(104, spec.textColor)
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
    fun `component defaults derive from theme colors`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 0xFF112233.toInt(),
                secondary = 0xFF445566.toInt(),
                error = 0xFFB3261E.toInt(),
                success = 0xFF2E7D32.toInt(),
                warning = 0xFFF57C00.toInt(),
                info = 0xFF1565C0.toInt(),
                divider = 0xFF778899.toInt(),
                textPrimary = 0xFFAABBCC.toInt(),
                textSecondary = 0xFFDDEEFF.toInt(),
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 30),
                body = UiTextStyle(fontSizeSp = 18),
                label = UiTextStyle(fontSizeSp = 12),
            ),
        )
        var buttonTonal = 0
        var outlinedBorder = 0
        var disabledPrimary = 0
        var linearProgressTrack = 0
        var segmentedIndicator = 0

        buildVNodeTree {
            UiTheme(customTheme) {
                buttonTonal = ButtonDefaults.containerColor(ButtonVariant.Tonal)
                outlinedBorder = ButtonDefaults.borderColor(ButtonVariant.Outlined)
                disabledPrimary = ButtonDefaults.containerColor(ButtonVariant.Primary, enabled = false)
                linearProgressTrack = ProgressIndicatorDefaults.linearTrackColor()
                segmentedIndicator = SegmentedControlDefaults.indicatorColor()
            }
        }

        assertEquals(customTheme.colors.secondaryContainer, buttonTonal)
        assertEquals(customTheme.colors.divider, outlinedBorder)
        assertEquals(customTheme.colors.divider, disabledPrimary)
        assertEquals(customTheme.colors.divider, linearProgressTrack)
        assertEquals(customTheme.colors.primary, segmentedIndicator)
    }

    @Test
    fun `text defaults reflect current theme`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 2,
                surfaceVariant = 3,
                primary = 4,
                secondary = 5,
                error = 9,
                success = 10,
                warning = 11,
                info = 12,
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
                secondary = 5,
                error = 9,
                success = 10,
                warning = 11,
                info = 12,
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
        val alpha = elements.last { it is AlphaModifierElement } as AlphaModifierElement
        val spec = surface.spec as BoxNodeProps

        assertEquals(NodeType.Surface, surface.type)
        assertEquals(SurfaceDefaults.variantBackgroundColor(), background.color)
        assertEquals(SurfaceDefaults.cardCornerRadius(), cornerRadius.topStart)
        assertEquals(SurfaceDefaults.cardCornerRadius(), cornerRadius.bottomStart)
        assertEquals(SurfaceDefaults.pressedColor(), spec.rippleColor)
        assertEquals(SurfaceDefaults.disabledAlpha(), alpha.alpha)
        assertTrue(surface.spec is BoxNodeProps)
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
        val spec = text.spec as TextNodeProps

        assertEquals(SurfaceDefaults.variantContentColor(), spec.textColor)
    }

    @Test
    fun `input defaults reflect current theme`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 1,
                surface = 22,
                surfaceVariant = 33,
                primary = 44,
                secondary = 55,
                error = 9,
                success = 10,
                warning = 11,
                info = 12,
                divider = 66,
                textPrimary = 77,
                textSecondary = 88,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 31),
                body = UiTextStyle(fontSizeSp = 19),
                label = UiTextStyle(fontSizeSp = 13),
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

        assertEquals(customTheme.colors.surface, container)
        assertEquals(customTheme.colors.surfaceVariant, disabledContainer)
        assertEquals(customTheme.colors.onErrorContainer, errorColor)
        assertEquals(customTheme.colors.primary, controlColor)
        assertEquals(customTheme.shapes.cardCornerRadius, cardCornerRadius)
        assertEquals(pressedOverlayColorFor(customTheme.colors.textPrimary), pressedColor)
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
        val overrideShapes = baseTheme.shapes.copy(smallCornerRadius = 99)
        var current: UiThemeTokens? = null

        buildVNodeTree {
            UiTheme(baseTheme) {
                UiThemeOverride(
                    shapes = overrideShapes,
                ) {
                    current = Theme.current
                }
            }
        }

        assertEquals(overrideShapes, current?.shapes)
        assertEquals(baseTheme.colors, current?.colors)
    }

    @Test
    fun `color override via ProvideButtonColors changes targeted defaults`() {
        val baseTheme = UiThemeDefaults.light()
        var buttonPrimary = 0
        var segmentedIndicator = 0
        var disabledControl = 0
        var baseTextField = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                ProvideButtonColors(
                    ButtonColorOverride(
                        primaryContainer = 0xFF778899.toInt(),
                    ),
                ) {
                    buttonPrimary = ButtonDefaults.containerColor(ButtonVariant.Primary)
                }
                ProvideSegmentedControlColors(
                    SegmentedControlColorOverride(
                        indicator = 0xFF998877.toInt(),
                    ),
                ) {
                    segmentedIndicator = SegmentedControlDefaults.indicatorColor()
                }
                ProvideCheckboxColors(
                    InputControlColorOverride(
                        controlDisabled = 0xFF556677.toInt(),
                    ),
                ) {
                    disabledControl = InputControlDefaults.checkboxControlColor(enabled = false)
                }
                baseTextField = TextFieldDefaults.containerColor(TextFieldVariant.Filled)
            }
        }

        assertEquals(0xFF778899.toInt(), buttonPrimary)
        assertEquals(0xFF998877.toInt(), segmentedIndicator)
        assertEquals(0xFF556677.toInt(), disabledControl)
        assertEquals(baseTheme.colors.surface, baseTextField)
    }

    @Test
    fun `color override supports disabled button and text field states`() {
        val baseTheme = UiThemeDefaults.light()
        var disabledButton = 0
        var disabledField = 0
        var errorField = 0

        buildVNodeTree {
            UiTheme(baseTheme) {
                ProvideButtonColors(
                    ButtonColorOverride(
                        primaryDisabledContainer = 0xFF111122.toInt(),
                    ),
                ) {
                    disabledButton = ButtonDefaults.containerColor(
                        variant = ButtonVariant.Primary,
                        enabled = false,
                    )
                }
                ProvideTextFieldColors(
                    TextFieldColorOverride(
                        filledDisabledContainer = 0xFF222233.toInt(),
                        outlinedErrorBorder = 0xFF333344.toInt(),
                    ),
                ) {
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

    private fun com.viewcompose.ui.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.viewcompose.ui.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
