package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.props
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ImageNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.ProgressIndicatorNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.SliderNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeBindingDifferTest {
    @Test
    fun `ignores child changes when node self is unchanged`() {
        val previous = textNode(
            children = listOf(
                VNode(
                    type = NodeType.Text,
                    spec = TextNodeProps(
                        text = "child-1",
                        maxLines = 1,
                        overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                        textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
                    ),
                ),
            ),
        )
        val next = textNode(
            children = listOf(
                VNode(
                    type = NodeType.Text,
                    spec = TextNodeProps(
                        text = "child-2",
                        maxLines = 1,
                        overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                        textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
                    ),
                ),
            ),
        )

        assertSame(NodeBindingPlan.Skip, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `rebinds when node spec changes`() {
        val previous = textNode(text = "before")
        val next = textNode(text = "after")

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is TextNodePatch)
    }

    @Test
    fun `rebinds when modifier changes`() {
        val previous = textNode()
        val next = textNode(modifier = Modifier.padding(8))

        assertSame(NodeBindingPlan.Rebind, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `rebinds when props change for nodes without spec`() {
        val previous = VNode(
            type = NodeType.Spacer,
            props = Props.Empty,
            modifier = Modifier,
        )
        val next = VNode(
            type = NodeType.Spacer,
            props = Props(mapOf("width" to 10)),
            modifier = Modifier,
        )

        assertSame(NodeBindingPlan.Rebind, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `patches button semantic updates when style is unchanged`() {
        val previous = buttonNode(text = "Continue")
        val next = buttonNode(text = "Continue now")

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ButtonNodePatch)
    }

    @Test
    fun `rebinds button when style props change`() {
        val previous = buttonNode(textColor = 0xFF000000.toInt())
        val next = buttonNode(textColor = 0xFFFF0000.toInt())

        assertSame(NodeBindingPlan.Rebind, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `patches text field semantic updates when style is unchanged`() {
        val previous = textFieldNode(value = "before")
        val next = textFieldNode(value = "after")

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is TextFieldNodePatch)
    }

    @Test
    fun `rebinds text field when style props change`() {
        val previous = textFieldNode(textColor = 0xFF000000.toInt())
        val next = textFieldNode(textColor = 0xFFFF0000.toInt())

        assertSame(NodeBindingPlan.Rebind, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `patches tab pager semantic updates`() {
        val previous = tabPagerNode(selectedTabIndex = 0)
        val next = tabPagerNode(selectedTabIndex = 1)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is TabPagerNodePatch)
    }

    @Test
    fun `patches segmented control semantic updates`() {
        val previous = segmentedControlNode(selectedIndex = 0)
        val next = segmentedControlNode(selectedIndex = 1)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is SegmentedControlNodePatch)
    }

    @Test
    fun `patches lazy column semantic updates`() {
        val previous = lazyColumnNode(spacing = 8)
        val next = lazyColumnNode(spacing = 16)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is LazyColumnNodePatch)
    }

    @Test
    fun `patches toggle semantic updates`() {
        val previous = toggleNode(checked = false)
        val next = toggleNode(checked = true)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ToggleNodePatch)
    }

    @Test
    fun `patches slider semantic updates`() {
        val previous = sliderNode(value = 10)
        val next = sliderNode(value = 50)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is SliderNodePatch)
    }

    @Test
    fun `patches progress indicator semantic updates`() {
        val previous = progressNode(progress = 0.3f)
        val next = progressNode(progress = 0.7f)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ProgressIndicatorNodePatch)
    }

    @Test
    fun `patches divider semantic updates`() {
        val previous = dividerNode(color = 0xFFCCCCCC.toInt())
        val next = dividerNode(color = 0xFF000000.toInt())

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is DividerNodePatch)
    }

    @Test
    fun `patches row semantic updates`() {
        val previous = rowNode(spacing = 8)
        val next = rowNode(spacing = 16)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is RowNodePatch)
    }

    @Test
    fun `patches column semantic updates`() {
        val previous = columnNode(spacing = 8)
        val next = columnNode(spacing = 16)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ColumnNodePatch)
    }

    @Test
    fun `patches box semantic updates`() {
        val previous = boxNode(contentAlignment = com.gzq.uiframework.renderer.layout.BoxAlignment.TopStart)
        val next = boxNode(contentAlignment = com.gzq.uiframework.renderer.layout.BoxAlignment.Center)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is BoxNodePatch)
    }

    @Test
    fun `patches image semantic updates`() {
        val previous = imageNode(tint = 0xFF000000.toInt())
        val next = imageNode(tint = 0xFFFF0000.toInt())

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ImageNodePatch)
    }

    @Test
    fun `patches icon button semantic updates`() {
        val previous = iconButtonNode(enabled = true)
        val next = iconButtonNode(enabled = false)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is IconButtonNodePatch)
    }

    private fun textNode(
        text: String = "value",
        modifier: Modifier = Modifier,
        children: List<VNode> = emptyList(),
    ): VNode {
        return VNode(
            type = NodeType.Text,
            props = Props.Empty,
            spec = TextNodeProps(
                text = text,
                maxLines = 1,
                overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
            ),
            modifier = modifier,
            children = children,
        )
    }

    private fun buttonNode(
        text: String = "Continue",
        textColor: Int = 0xFF000000.toInt(),
    ): VNode {
        return VNode(
            type = NodeType.Button,
            props = props {
                set(TypedPropKeys.TextColor, textColor)
            },
            spec = ButtonNodeProps(
                text = text,
                enabled = true,
                iconSpacing = 8,
                leadingIcon = null,
                trailingIcon = null,
                iconTint = textColor,
                iconSize = 18,
                onClick = null,
            ),
            modifier = Modifier,
        )
    }

    private fun textFieldNode(
        value: String = "hello",
        textColor: Int = 0xFF000000.toInt(),
    ): VNode {
        return VNode(
            type = NodeType.TextField,
            props = props {
                set(TypedPropKeys.TextColor, textColor)
            },
            spec = TextFieldNodeProps(
                value = value,
                label = "Label",
                labelColor = 0xFF666666.toInt(),
                labelTextSizeSp = 12,
                supportingText = "Help",
                supportingTextColor = 0xFF777777.toInt(),
                supportingTextSizeSp = 12,
                placeholder = "Hint",
                enabled = true,
                singleLine = true,
                minLines = 1,
                maxLines = 1,
                keyboardType = com.gzq.uiframework.renderer.node.TextFieldType.Text,
                imeAction = com.gzq.uiframework.renderer.node.TextFieldImeAction.Done,
                hintColor = 0xFF888888.toInt(),
                readOnly = false,
                onValueChange = null,
            ),
            modifier = Modifier,
        )
    }

    private fun tabPagerNode(
        selectedTabIndex: Int = 0,
    ): VNode {
        return VNode(
            type = NodeType.TabPager,
            props = Props.Empty,
            spec = TabPagerNodeProps(
                pages = emptyList(),
                selectedTabIndex = selectedTabIndex,
                onTabSelected = null,
                backgroundColor = 1,
                indicatorColor = 2,
                cornerRadius = 3,
                indicatorHeight = 4,
                tabPaddingHorizontal = 5,
                tabPaddingVertical = 6,
                selectedTextColor = 7,
                unselectedTextColor = 8,
                rippleColor = 9,
            ),
            modifier = Modifier,
        )
    }

    private fun segmentedControlNode(
        selectedIndex: Int = 0,
    ): VNode {
        return VNode(
            type = NodeType.SegmentedControl,
            props = Props.Empty,
            spec = SegmentedControlNodeProps(
                items = emptyList(),
                selectedIndex = selectedIndex,
                onSelectionChange = null,
                enabled = true,
                backgroundColor = 1,
                indicatorColor = 2,
                cornerRadius = 3,
                textColor = 4,
                selectedTextColor = 5,
                rippleColor = 6,
                textSizeSp = 14,
                horizontalPadding = 8,
                verticalPadding = 6,
            ),
            modifier = Modifier,
        )
    }

    private fun lazyColumnNode(
        spacing: Int = 8,
    ): VNode {
        return VNode(
            type = NodeType.LazyColumn,
            props = Props.Empty,
            spec = LazyColumnNodeProps(
                contentPadding = 12,
                spacing = spacing,
                items = emptyList(),
            ),
            modifier = Modifier,
        )
    }

    private fun toggleNode(
        checked: Boolean = false,
    ): VNode {
        return VNode(
            type = NodeType.Checkbox,
            props = Props.Empty,
            spec = ToggleNodeProps(
                text = "Toggle",
                enabled = true,
                checked = checked,
                controlColor = 0xFF000000.toInt(),
                onCheckedChange = null,
            ),
            modifier = Modifier,
        )
    }

    private fun sliderNode(
        value: Int = 50,
    ): VNode {
        return VNode(
            type = NodeType.Slider,
            props = Props.Empty,
            spec = SliderNodeProps(
                min = 0,
                max = 100,
                value = value,
                enabled = true,
                tintColor = 0xFF000000.toInt(),
                onValueChange = null,
            ),
            modifier = Modifier,
        )
    }

    private fun progressNode(
        progress: Float? = 0.5f,
    ): VNode {
        return VNode(
            type = NodeType.LinearProgressIndicator,
            props = Props.Empty,
            spec = ProgressIndicatorNodeProps(
                enabled = true,
                progress = progress,
                indicatorColor = 0xFF000000.toInt(),
                trackColor = 0x33000000,
                trackThickness = 4,
                indicatorSize = 32,
            ),
            modifier = Modifier,
        )
    }

    private fun dividerNode(
        color: Int = 0xFFCCCCCC.toInt(),
    ): VNode {
        return VNode(
            type = NodeType.Divider,
            props = Props.Empty,
            spec = DividerNodeProps(
                color = color,
                thickness = 1,
            ),
            modifier = Modifier,
        )
    }

    private fun rowNode(
        spacing: Int = 8,
    ): VNode {
        return VNode(
            type = NodeType.Row,
            props = Props.Empty,
            spec = RowNodeProps(
                spacing = spacing,
                arrangement = com.gzq.uiframework.renderer.layout.MainAxisArrangement.Start,
                verticalAlignment = com.gzq.uiframework.renderer.layout.VerticalAlignment.Top,
            ),
            modifier = Modifier,
        )
    }

    private fun columnNode(
        spacing: Int = 8,
    ): VNode {
        return VNode(
            type = NodeType.Column,
            props = Props.Empty,
            spec = ColumnNodeProps(
                spacing = spacing,
                arrangement = com.gzq.uiframework.renderer.layout.MainAxisArrangement.Start,
                horizontalAlignment = com.gzq.uiframework.renderer.layout.HorizontalAlignment.Start,
            ),
            modifier = Modifier,
        )
    }

    private fun boxNode(
        contentAlignment: com.gzq.uiframework.renderer.layout.BoxAlignment = com.gzq.uiframework.renderer.layout.BoxAlignment.TopStart,
    ): VNode {
        return VNode(
            type = NodeType.Box,
            props = Props.Empty,
            spec = BoxNodeProps(
                contentAlignment = contentAlignment,
            ),
            modifier = Modifier,
        )
    }

    private fun imageNode(
        tint: Int? = null,
    ): VNode {
        return VNode(
            type = NodeType.Image,
            props = Props.Empty,
            spec = ImageNodeProps(
                contentDescription = null,
                contentScale = com.gzq.uiframework.renderer.node.ImageContentScale.Fit,
                tint = tint,
                source = null,
                placeholder = null,
                error = null,
                fallback = null,
                remoteImageLoader = null,
            ),
            modifier = Modifier,
        )
    }

    private fun iconButtonNode(
        enabled: Boolean = true,
    ): VNode {
        return VNode(
            type = NodeType.IconButton,
            props = Props.Empty,
            spec = IconButtonNodeProps(
                contentDescription = null,
                contentScale = com.gzq.uiframework.renderer.node.ImageContentScale.Fit,
                tint = null,
                source = null,
                placeholder = null,
                error = null,
                fallback = null,
                remoteImageLoader = null,
                enabled = enabled,
            ),
            modifier = Modifier,
        )
    }
}
