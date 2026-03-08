package com.viewcompose.widget.core

import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.TextFieldImeAction
import com.viewcompose.ui.node.TextFieldType
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.node.spec.TextNodeProps
import com.viewcompose.ui.node.spec.TextFieldNodeProps
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextFieldTest {
    @Test
    fun `text field emits expected props and themed defaults`() {
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
        val tree = buildVNodeTree {
            UiTheme(customTheme) {
                TextField(
                    value = "hello",
                    onValueChange = {},
                    hint = "Type here",
                    label = "Display name",
                    supportingText = "Shown in profile",
                    maxLines = 3,
                    imeAction = TextFieldImeAction.Next,
                )
            }
        }

        val root = tree.single()
        val node = findFirstTextFieldNode(tree)
        val spec = node.spec as TextFieldNodeProps

        assertEquals(NodeType.Column, root.type)
        assertEquals(NodeType.TextField, node.type)
        assertEquals("hello", spec.value)
        assertEquals("Type here", spec.placeholder)
        assertTrue(collectTextNodes(root).any { it.text == "Display name" })
        assertTrue(collectTextNodes(root).any { it.text == "Shown in profile" })
        assertEquals(true, spec.singleLine)
        assertEquals(TextFieldType.Text, spec.keyboardType)
        assertEquals(3, spec.maxLines)
        assertEquals(TextFieldImeAction.Next, spec.imeAction)
        assertEquals(customTheme.colors.textSecondary, spec.hintColor)
        assertEquals(customTheme.colors.textPrimary, spec.textColor)
        assertEquals(customTheme.typography.body.fontSizeSp, spec.textSizeSp)
        assertEquals(customTheme.colors.surface, spec.backgroundColor)
        assertEquals(customTheme.shapes.interactiveCornerRadius, spec.cornerRadius)
        assertEquals(true, spec.enabled)
        assertTrue(node.spec is TextFieldNodeProps)
    }

    @Test
    fun `password field uses password input type`() {
        val tree = buildVNodeTree {
            PasswordField(
                value = "secret",
                onValueChange = {},
                hint = "Password",
                label = "Password",
                supportingText = "At least 8 characters",
            )
        }

        val node = findFirstTextFieldNode(tree)
        val spec = node.spec as TextFieldNodeProps

        assertEquals(NodeType.TextField, node.type)
        assertEquals(TextFieldType.Password, spec.keyboardType)
        assertTrue(collectTextNodes(tree.single()).any { it.text == "Password" })
        assertTrue(collectTextNodes(tree.single()).any { it.text == "At least 8 characters" })
        assertTrue(spec.singleLine)
    }

    @Test
    fun `text area exposes read only and multiline semantics`() {
        val tree = buildVNodeTree {
            TextArea(
                value = "Line 1",
                onValueChange = {},
                label = "Bio",
                supportingText = "Visible to collaborators",
                readOnly = true,
                minLines = 4,
                maxLines = 6,
                imeAction = TextFieldImeAction.Done,
            )
        }

        val node = findFirstTextFieldNode(tree)
        val spec = node.spec as TextFieldNodeProps

        assertEquals(false, spec.singleLine)
        assertEquals(true, spec.readOnly)
        assertEquals(4, spec.minLines)
        assertEquals(6, spec.maxLines)
        assertEquals(TextFieldImeAction.Done, spec.imeAction)
        assertTrue(collectTextNodes(tree.single()).any { it.text == "Bio" })
        assertTrue(collectTextNodes(tree.single()).any { it.text == "Visible to collaborators" })
    }

    @Test
    fun `outlined text field uses border variant`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                TextField(
                    value = "hello",
                    onValueChange = {},
                    variant = TextFieldVariant.Outlined,
                )
            }
        }

        val spec = findFirstTextFieldNode(tree).spec as TextFieldNodeProps

        assertEquals(0x00000000, spec.backgroundColor)
        assertEquals(Theme.colors.primary, spec.borderColor)
        assertEquals(1.dp, spec.borderWidth)
    }

    @Test
    fun `compact text field applies themed height and typography`() {
        val tree = buildVNodeTree {
            UiTheme(UiThemeDefaults.light()) {
                TextField(
                    value = "hello",
                    onValueChange = {},
                    size = TextFieldSize.Compact,
                )
            }
        }

        val textFieldNode = findFirstTextFieldNode(tree)
        val elements = textFieldNode.modifier.readModifierElements()
        val spec = textFieldNode.spec as TextFieldNodeProps

        assertFalse(elements.any { it is com.viewcompose.ui.modifier.HeightModifierElement })
        assertEquals(TextFieldDefaults.height(TextFieldSize.Compact), spec.minHeight)
        assertEquals(Theme.typography.label.fontSizeSp, spec.textSizeSp)
    }

    @Test
    fun `disabled and error text field states use color overrides`() {
        val baseTheme = UiThemeDefaults.light()

        val disabledTree = buildVNodeTree {
            UiTheme(baseTheme) {
                ProvideTextFieldColors(
                    TextFieldColorOverride(
                        filledDisabledContainer = 202,
                        outlinedErrorBorder = 209,
                    ),
                ) {
                    TextField(
                        value = "hello",
                        onValueChange = {},
                        enabled = false,
                    )
                }
            }
        }
        val errorTree = buildVNodeTree {
            UiTheme(baseTheme) {
                ProvideTextFieldColors(
                    TextFieldColorOverride(
                        filledDisabledContainer = 202,
                        outlinedErrorBorder = 209,
                    ),
                ) {
                    TextField(
                        value = "hello",
                        onValueChange = {},
                        variant = TextFieldVariant.Outlined,
                        isError = true,
                    )
                }
            }
        }

        val disabledSpec = findFirstTextFieldNode(disabledTree).spec as TextFieldNodeProps
        val errorSpec = findFirstTextFieldNode(errorTree).spec as TextFieldNodeProps

        assertEquals(202, disabledSpec.backgroundColor)
        assertEquals(209, errorSpec.borderColor)
    }

    private fun com.viewcompose.ui.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = com.viewcompose.ui.modifier.Modifier::class.java.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }

    private fun findFirstTextFieldNode(tree: List<VNode>): VNode {
        fun visit(node: VNode): VNode? {
            if (node.type == NodeType.TextField) return node
            node.children.forEach { child ->
                val match = visit(child)
                if (match != null) return match
            }
            return null
        }
        tree.forEach { node ->
            val match = visit(node)
            if (match != null) return match
        }
        error("No TextField node found")
    }

    private fun collectTextNodes(node: VNode): List<TextNodeProps> {
        val result = mutableListOf<TextNodeProps>()
        fun visit(current: VNode) {
            val spec = current.spec
            if (spec is TextNodeProps) {
                result += spec
            }
            current.children.forEach(::visit)
        }
        visit(node)
        return result
    }
}
