package com.gzq.uiframework

import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Chip
import com.gzq.uiframework.widget.core.ChipVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.ExtendedFloatingActionButton
import com.gzq.uiframework.widget.core.FabSize
import com.gzq.uiframework.widget.core.FloatingActionButton
import com.gzq.uiframework.widget.core.FlowRow
import com.gzq.uiframework.widget.core.Icon
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.ShowcaseChip() {
    val selectedState = remember { mutableStateOf(false) }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "变体对比", subtitle = "Assist / Filter / Input / Suggestion") {
            FlowRow(
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp,
            ) {
                ChipVariant.entries.forEach { variant ->
                    Chip(
                        label = variant.name,
                        variant = variant,
                        onClick = {},
                    )
                }
            }
        }

        DemoSection(title = "选中态", subtitle = "Filter Chip selected 切换") {
            Chip(
                label = if (selectedState.value) "已选中" else "未选中",
                variant = ChipVariant.Filter,
                selected = selectedState.value,
                onClick = { selectedState.value = !selectedState.value },
            )
        }

        DemoSection(title = "带图标", subtitle = "leadingIcon + onTrailingIconClick") {
            Chip(
                label = "带前置图标",
                leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                onClick = {},
            )
            Chip(
                label = "可删除",
                onTrailingIconClick = {},
                onClick = {},
                modifier = Modifier.margin(top = 8.dp),
            )
            Chip(
                label = "完整 Chip",
                leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                onTrailingIconClick = {},
                onClick = {},
                modifier = Modifier.margin(top = 8.dp),
            )
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Row(spacing = 8.dp) {
                Chip(label = "Enabled", onClick = {})
                Chip(label = "Disabled", enabled = false, onClick = {})
            }
        }
    }
}

internal fun UiTreeBuilder.ShowcaseFab() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "尺寸对比", subtitle = "Small / Medium / Large") {
            Row(
                spacing = 16.dp,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                FabSize.entries.forEach { size ->
                    FloatingActionButton(
                        onClick = {},
                        size = size,
                    ) {
                        Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                    }
                }
            }
            Row(
                spacing = 16.dp,
                modifier = Modifier.margin(top = 4.dp),
            ) {
                FabSize.entries.forEach { size ->
                    Text(
                        text = size.name,
                        style = UiTextStyle(fontSizeSp = 12.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }

        DemoSection(title = "ExtendedFAB", subtitle = "文字 + 图标的扩展样式") {
            ExtendedFloatingActionButton(
                text = "新建",
                icon = ImageSource.Resource(R.drawable.demo_media_icon),
                onClick = {},
            )
            ExtendedFloatingActionButton(
                text = "仅文字",
                onClick = {},
                modifier = Modifier.margin(top = 12.dp),
            )
        }

        DemoSection(title = "自定义颜色", subtitle = "containerColor / contentColor") {
            FloatingActionButton(
                onClick = {},
                containerColor = Theme.colors.accent,
                contentColor = Theme.colors.surface,
            ) {
                Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
            }
        }
    }
}
