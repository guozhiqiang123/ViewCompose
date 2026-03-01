package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.modifier.WidthModifierElement
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressIndicatorTest {
    @Test
    fun `linear progress indicator emits themed defaults`() {
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
                body = UiTextStyle(fontSizeSp = 16),
                label = UiTextStyle(fontSizeSp = 14),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                LinearProgressIndicator(progress = 0.42f)
            }
        }

        val node = tree.single()
        val elements = node.modifier.readModifierElements()
        val width = elements.last { it is WidthModifierElement } as WidthModifierElement
        val height = elements.last { it is HeightModifierElement } as HeightModifierElement

        assertEquals(NodeType.LinearProgressIndicator, node.type)
        assertEquals(0.42f, node.props[TypedPropKeys.ProgressFraction])
        assertEquals(customTheme.components.progressIndicator.linearIndicator, node.props[TypedPropKeys.ProgressIndicatorColor])
        assertEquals(customTheme.components.progressIndicator.linearTrack, node.props[TypedPropKeys.ProgressTrackColor])
        assertEquals(customTheme.controls.progressIndicator.linearTrackThickness, node.props[TypedPropKeys.ProgressTrackThickness])
        assertEquals(android.view.ViewGroup.LayoutParams.MATCH_PARENT, width.width)
        assertEquals(customTheme.controls.progressIndicator.linearTrackThickness, height.height)
    }

    @Test
    fun `circular progress indicator emits themed defaults`() {
        val customTheme = UiThemeTokens(
            colors = UiColors(
                background = 11,
                surface = 12,
                surfaceVariant = 13,
                primary = 14,
                accent = 15,
                divider = 16,
                textPrimary = 17,
                textSecondary = 18,
            ),
            typography = UiTypography(
                title = UiTextStyle(fontSizeSp = 28),
                body = UiTextStyle(fontSizeSp = 16),
                label = UiTextStyle(fontSizeSp = 13),
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                CircularProgressIndicator()
            }
        }

        val node = tree.single()
        val size = node.modifier.readModifierElements().last { it is SizeModifierElement } as SizeModifierElement

        assertEquals(NodeType.CircularProgressIndicator, node.type)
        assertEquals(null, node.props[TypedPropKeys.ProgressFraction])
        assertEquals(customTheme.components.progressIndicator.circularIndicator, node.props[TypedPropKeys.ProgressIndicatorColor])
        assertEquals(customTheme.components.progressIndicator.circularTrack, node.props[TypedPropKeys.ProgressTrackColor])
        assertEquals(customTheme.controls.progressIndicator.circularTrackThickness, node.props[TypedPropKeys.ProgressTrackThickness])
        assertEquals(customTheme.controls.progressIndicator.circularSize, node.props[TypedPropKeys.ProgressIndicatorSize])
        assertEquals(customTheme.controls.progressIndicator.circularSize, size.width)
        assertEquals(customTheme.controls.progressIndicator.circularSize, size.height)
    }

    @Test
    fun `progress indicator uses component style overrides`() {
        val baseTheme = UiThemeDefaults.light()
        val customTheme = UiThemeTokens(
            colors = baseTheme.colors,
            typography = baseTheme.typography,
            input = baseTheme.input,
            controls = baseTheme.controls,
            components = UiComponentStyles(
                button = baseTheme.components.button,
                textField = baseTheme.components.textField,
                segmentedControl = baseTheme.components.segmentedControl,
                checkbox = baseTheme.components.checkbox,
                switchControl = baseTheme.components.switchControl,
                radioButton = baseTheme.components.radioButton,
                slider = baseTheme.components.slider,
                progressIndicator = UiProgressIndicatorStyles(
                    linearIndicator = 701,
                    linearTrack = 702,
                    circularIndicator = 703,
                    circularTrack = 704,
                ),
                tabPager = baseTheme.components.tabPager,
            ),
        )

        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                Column {
                    LinearProgressIndicator(progress = 0.4f)
                    CircularProgressIndicator(progress = 0.6f)
                }
            }
        }

        val linearNode = tree.single().children[0]
        val circularNode = tree.single().children[1]

        assertEquals(701, linearNode.props[TypedPropKeys.ProgressIndicatorColor])
        assertEquals(702, linearNode.props[TypedPropKeys.ProgressTrackColor])
        assertEquals(703, circularNode.props[TypedPropKeys.ProgressIndicatorColor])
        assertEquals(704, circularNode.props[TypedPropKeys.ProgressTrackColor])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
