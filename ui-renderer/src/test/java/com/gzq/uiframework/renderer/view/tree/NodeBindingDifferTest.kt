package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowRowNodeProps
import com.gzq.uiframework.renderer.node.spec.HorizontalPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ImageNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyVerticalGridNodeProps
import com.gzq.uiframework.renderer.node.spec.ProgressIndicatorNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.SliderNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps
import com.gzq.uiframework.renderer.node.spec.VerticalPagerNodeProps
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NodeBindingDifferTest {
    @Test
    fun `returns skip self only when child tree changes`() {
        val previous = textNode(
            children = listOf(
                VNode(
                    type = NodeType.Text,
                    spec = TextNodeProps(
                        text = "child-1",
                        maxLines = 1,
                        overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                        textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
                        textColor = 0xFF000000.toInt(),
                        textSizeSp = 14,
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
                        textColor = 0xFF000000.toInt(),
                        textSizeSp = 14,
                    ),
                ),
            ),
        )

        assertSame(NodeBindingPlan.SkipSelfOnly, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `returns subtree skip when self and children are unchanged`() {
        val previous = textNode(text = "stable")
        val next = textNode(text = "stable")

        assertSame(NodeBindingPlan.SkipSubtree, NodeBindingDiffer.plan(previous, next))
    }

    @Test
    fun `patches when node spec changes`() {
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
    fun `patches button when text changes`() {
        val previous = buttonNode(text = "Continue")
        val next = buttonNode(text = "Continue now")

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ButtonNodePatch)
    }

    @Test
    fun `patches button when style changes instead of rebinding`() {
        val previous = buttonNode(textColor = 0xFF000000.toInt())
        val next = buttonNode(textColor = 0xFFFF0000.toInt())

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is ButtonNodePatch)
    }

    @Test
    fun `patches text field semantic updates`() {
        val previous = textFieldNode(value = "before")
        val next = textFieldNode(value = "after")

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is TextFieldNodePatch)
    }

    @Test
    fun `patches text field when style changes instead of rebinding`() {
        val previous = textFieldNode(textColor = 0xFF000000.toInt())
        val next = textFieldNode(textColor = 0xFFFF0000.toInt())

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is TextFieldNodePatch)
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
    fun `patches lazy vertical grid semantic updates`() {
        val previous = lazyVerticalGridNode(spanCount = 2)
        val next = lazyVerticalGridNode(spanCount = 3)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is LazyVerticalGridNodePatch)
    }

    @Test
    fun `patches horizontal pager semantic updates`() {
        val previous = horizontalPagerNode(currentPage = 0)
        val next = horizontalPagerNode(currentPage = 1)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is HorizontalPagerNodePatch)
    }

    @Test
    fun `patches vertical pager semantic updates`() {
        val previous = verticalPagerNode(currentPage = 0)
        val next = verticalPagerNode(currentPage = 1)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is VerticalPagerNodePatch)
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

    @Test
    fun `patches flow row semantic updates`() {
        val previous = flowRowNode(horizontalSpacing = 8)
        val next = flowRowNode(horizontalSpacing = 16)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is FlowRowNodePatch)
    }

    @Test
    fun `patches flow column semantic updates`() {
        val previous = flowColumnNode(verticalSpacing = 8)
        val next = flowColumnNode(verticalSpacing = 16)

        val plan = NodeBindingDiffer.plan(previous, next)

        assertTrue(plan is NodeBindingPlan.Patch)
        assertTrue((plan as NodeBindingPlan.Patch).patch is FlowColumnNodePatch)
    }

    private fun textNode(
        text: String = "value",
        modifier: Modifier = Modifier,
        children: List<VNode> = emptyList(),
    ): VNode {
        return VNode(
            type = NodeType.Text,
            spec = TextNodeProps(
                text = text,
                maxLines = 1,
                overflow = com.gzq.uiframework.renderer.node.TextOverflow.Clip,
                textAlign = com.gzq.uiframework.renderer.node.TextAlign.Start,
                textColor = 0xFF000000.toInt(),
                textSizeSp = 14,
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
            spec = ButtonNodeProps(
                text = text,
                enabled = true,
                onClick = null,
                textColor = textColor,
                textSizeSp = 14,
                backgroundColor = 0xFF0000FF.toInt(),
                borderWidth = 0,
                borderColor = 0,
                cornerRadius = 8,
                rippleColor = 0x33000000,
                minHeight = 48,
                paddingHorizontal = 16,
                paddingVertical = 8,
                leadingIcon = null,
                trailingIcon = null,
                iconTint = textColor,
                iconSize = 18,
                iconSpacing = 8,
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
                textColor = textColor,
                textSizeSp = 16,
                backgroundColor = 0xFFEEEEEE.toInt(),
                borderWidth = 0,
                borderColor = 0,
                cornerRadius = 8,
                rippleColor = 0x33000000,
                minHeight = 56,
                paddingHorizontal = 16,
                paddingVertical = 12,
            ),
            modifier = Modifier,
        )
    }

    private fun segmentedControlNode(
        selectedIndex: Int = 0,
    ): VNode {
        return VNode(
            type = NodeType.SegmentedControl,
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
                paddingHorizontal = 8,
                paddingVertical = 6,
            ),
            modifier = Modifier,
        )
    }

    private fun lazyColumnNode(
        spacing: Int = 8,
    ): VNode {
        return VNode(
            type = NodeType.LazyColumn,
            spec = LazyColumnNodeProps(
                contentPadding = 12,
                spacing = spacing,
                items = emptyList(),
            ),
            modifier = Modifier,
        )
    }

    private fun lazyVerticalGridNode(
        spanCount: Int = 2,
    ): VNode {
        return VNode(
            type = NodeType.LazyVerticalGrid,
            spec = LazyVerticalGridNodeProps(
                spanCount = spanCount,
                contentPadding = 8,
                horizontalSpacing = 8,
                verticalSpacing = 8,
                items = listOf(
                    lazyItem("grid-1"),
                    lazyItem("grid-2"),
                ),
                state = null,
            ),
            modifier = Modifier,
        )
    }

    private fun horizontalPagerNode(
        currentPage: Int = 0,
    ): VNode {
        return VNode(
            type = NodeType.HorizontalPager,
            spec = HorizontalPagerNodeProps(
                pages = listOf(
                    lazyItem("page-1"),
                    lazyItem("page-2"),
                ),
                currentPage = currentPage,
                onPageChanged = null,
                offscreenPageLimit = 1,
                pagerState = null,
                userScrollEnabled = true,
            ),
            modifier = Modifier,
        )
    }

    private fun verticalPagerNode(
        currentPage: Int = 0,
    ): VNode {
        return VNode(
            type = NodeType.VerticalPager,
            spec = VerticalPagerNodeProps(
                pages = listOf(
                    lazyItem("v-page-1"),
                    lazyItem("v-page-2"),
                ),
                currentPage = currentPage,
                onPageChanged = null,
                offscreenPageLimit = 1,
                pagerState = null,
                userScrollEnabled = true,
            ),
            modifier = Modifier,
        )
    }

    private fun lazyItem(
        key: String,
    ): LazyListItem {
        return LazyListItem(
            key = key,
            contentToken = key,
            sessionFactory = LazyListItemSessionFactory {
                object : LazyListItemSession {
                    override fun render() = Unit

                    override fun dispose() = Unit
                }
            },
        )
    }

    private fun toggleNode(
        checked: Boolean = false,
    ): VNode {
        return VNode(
            type = NodeType.Checkbox,
            spec = ToggleNodeProps(
                text = "Toggle",
                enabled = true,
                checked = checked,
                controlColor = 0xFF000000.toInt(),
                onCheckedChange = null,
                textColor = 0xFF000000.toInt(),
                textSizeSp = 14,
                rippleColor = 0x33000000,
            ),
            modifier = Modifier,
        )
    }

    private fun sliderNode(
        value: Int = 50,
    ): VNode {
        return VNode(
            type = NodeType.Slider,
            spec = SliderNodeProps(
                min = 0,
                max = 100,
                value = value,
                enabled = true,
                thumbColor = 0xFF000000.toInt(),
                trackColor = 0xFF000000.toInt(),
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
                backgroundColor = 0xFF0000FF.toInt(),
                borderWidth = 0,
                borderColor = 0,
                cornerRadius = 8,
                rippleColor = 0x33000000,
                contentPadding = 8,
            ),
            modifier = Modifier,
        )
    }

    private fun flowRowNode(
        horizontalSpacing: Int = 8,
    ): VNode {
        return VNode(
            type = NodeType.FlowRow,
            spec = FlowRowNodeProps(
                horizontalSpacing = horizontalSpacing,
                verticalSpacing = 4,
                maxItemsInEachRow = Int.MAX_VALUE,
            ),
            modifier = Modifier,
        )
    }

    private fun flowColumnNode(
        verticalSpacing: Int = 8,
    ): VNode {
        return VNode(
            type = NodeType.FlowColumn,
            spec = FlowColumnNodeProps(
                horizontalSpacing = 4,
                verticalSpacing = verticalSpacing,
                maxItemsInEachColumn = Int.MAX_VALUE,
            ),
            modifier = Modifier,
        )
    }
}
