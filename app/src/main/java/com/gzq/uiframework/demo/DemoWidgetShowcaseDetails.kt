package com.gzq.uiframework

import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Badge
import com.gzq.uiframework.widget.core.BadgedBox
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonSize
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Card
import com.gzq.uiframework.widget.core.CardVariant
import com.gzq.uiframework.widget.core.Checkbox
import com.gzq.uiframework.widget.core.Chip
import com.gzq.uiframework.widget.core.ChipVariant
import com.gzq.uiframework.widget.core.CircularProgressIndicator
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.ElevatedCard
import com.gzq.uiframework.widget.core.EmailField
import com.gzq.uiframework.widget.core.ExtendedFloatingActionButton
import com.gzq.uiframework.widget.core.FabSize
import com.gzq.uiframework.widget.core.FloatingActionButton
import com.gzq.uiframework.widget.core.FlowRow
import com.gzq.uiframework.widget.core.Icon
import com.gzq.uiframework.widget.core.IconButton
import com.gzq.uiframework.widget.core.Image
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.widget.core.LinearProgressIndicator
import com.gzq.uiframework.widget.core.ListItem
import com.gzq.uiframework.widget.core.NumberField
import com.gzq.uiframework.widget.core.OutlinedCard
import com.gzq.uiframework.widget.core.PasswordField
import com.gzq.uiframework.widget.core.RadioButton
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SearchBar
import com.gzq.uiframework.widget.core.SegmentedControl
import com.gzq.uiframework.widget.core.SegmentedControlSize
import com.gzq.uiframework.widget.core.Slider
import com.gzq.uiframework.widget.core.Switch
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.widget.core.TextArea
import com.gzq.uiframework.widget.core.TextButton
import com.gzq.uiframework.renderer.node.TextDecoration
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.TextField
import com.gzq.uiframework.widget.core.TextFieldSize
import com.gzq.uiframework.widget.core.TextFieldVariant
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

// ────────────────────────────────────────────
// Text
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseText() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "字号 (Style)", subtitle = "不同 fontSizeSp 对比") {
            listOf(12, 14, 16, 20, 24, 32).forEach { size ->
                Text(
                    text = "fontSize = ${size}sp",
                    style = UiTextStyle(fontSizeSp = size.sp),
                    modifier = Modifier.margin(bottom = 4.dp),
                )
            }
        }

        DemoSection(title = "颜色 (Color)", subtitle = "使用主题颜色和自定义颜色") {
            Text(text = "Primary Color", color = Theme.colors.primary)
            Text(text = "Secondary Color", color = TextDefaults.secondaryColor())
            Text(text = "Accent Color", color = Theme.colors.accent)
        }

        DemoSection(title = "对齐 (TextAlign)", subtitle = "Start / Center / End") {
            TextAlign.entries.forEach { align ->
                Text(
                    text = "textAlign = $align",
                    textAlign = align,
                    modifier = Modifier.fillMaxWidth().margin(bottom = 4.dp),
                )
            }
        }

        DemoSection(title = "装饰线 (TextDecoration)", subtitle = "下划线、删除线") {
            Text(text = "Underline", textDecoration = TextDecoration.Underline)
            Text(text = "LineThrough", textDecoration = TextDecoration.LineThrough)
            Text(
                text = "Underline + LineThrough",
                textDecoration = TextDecoration.UnderlineLineThrough,
            )
        }

        DemoSection(title = "截断 (MaxLines + Overflow)", subtitle = "单行截断 Ellipsis") {
            Text(
                text = "这是一段很长的文本，用于演示单行截断效果。当文本超出容器宽度时，会显示省略号来表示还有更多内容未显示。",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "这是一段很长的文本，用于演示两行截断效果。当文本超出两行时会被截断并显示省略号。这里需要足够长的文字才能触发截断效果。",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().margin(top = 8.dp),
            )
        }
    }
}

// ────────────────────────────────────────────
// Image / Icon
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseImageIcon() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "Image 缩放模式", subtitle = "contentScale 对比") {
            Row(
                spacing = 12.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ImageContentScale.entries.forEach { scale ->
                    Column(spacing = 4.dp) {
                        Image(
                            source = ImageSource.Resource(R.drawable.demo_media_icon),
                            contentScale = scale,
                            modifier = Modifier.size(64.dp, 64.dp),
                        )
                        Text(
                            text = scale.name,
                            style = UiTextStyle(fontSizeSp = 12.sp),
                        )
                    }
                }
            }
        }

        DemoSection(title = "Image tint", subtitle = "图片着色对比") {
            Row(spacing = 12.dp) {
                Image(
                    source = ImageSource.Resource(R.drawable.demo_media_icon),
                    modifier = Modifier.size(48.dp, 48.dp),
                )
                Image(
                    source = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(48.dp, 48.dp),
                )
                Image(
                    source = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.accent,
                    modifier = Modifier.size(48.dp, 48.dp),
                )
            }
        }

        DemoSection(title = "Icon 尺寸与着色", subtitle = "默认 tint 和自定义 tint") {
            Row(
                spacing = 16.dp,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                listOf(16, 24, 32, 48).forEach { s ->
                    Icon(
                        source = ImageSource.Resource(R.drawable.demo_media_icon),
                        size = s.dp,
                    )
                }
            }
            Row(
                spacing = 16.dp,
                verticalAlignment = VerticalAlignment.Center,
                modifier = Modifier.margin(top = 8.dp),
            ) {
                Icon(
                    source = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.primary,
                )
                Icon(
                    source = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.accent,
                )
                Icon(
                    source = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.textSecondary,
                )
            }
        }
    }
}

// ────────────────────────────────────────────
// Divider
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseDivider() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "默认分隔线", subtitle = "使用主题默认颜色和粗细") {
            Divider()
        }

        DemoSection(title = "颜色对比", subtitle = "不同颜色的分隔线") {
            Text(text = "Primary", style = UiTextStyle(fontSizeSp = 13.sp))
            Divider(color = Theme.colors.primary)
            Text(
                text = "Accent",
                style = UiTextStyle(fontSizeSp = 13.sp),
                modifier = Modifier.margin(top = 8.dp),
            )
            Divider(color = Theme.colors.accent)
            Text(
                text = "TextSecondary",
                style = UiTextStyle(fontSizeSp = 13.sp),
                modifier = Modifier.margin(top = 8.dp),
            )
            Divider(color = Theme.colors.textSecondary)
        }

        DemoSection(title = "粗细对比", subtitle = "不同 thickness 的分隔线") {
            listOf(1, 2, 4, 8).forEach { t ->
                Text(
                    text = "thickness = ${t}dp",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                )
                Divider(
                    thickness = t.dp,
                    modifier = Modifier.margin(bottom = 8.dp),
                )
            }
        }
    }
}

// ────────────────────────────────────────────
// Button / TextButton
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseButton() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "变体 × 尺寸矩阵", subtitle = "5 种 variant × 3 种 size") {
            ButtonVariant.entries.forEach { variant ->
                Text(
                    text = variant.name,
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(bottom = 4.dp),
                )
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.margin(bottom = 12.dp),
                ) {
                    ButtonSize.entries.forEach { size ->
                        Button(
                            text = size.name,
                            variant = variant,
                            size = size,
                            onClick = {},
                        )
                    }
                }
            }
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Row(spacing = 8.dp) {
                Button(text = "Enabled", onClick = {})
                Button(text = "Disabled", enabled = false, onClick = {})
            }
        }

        DemoSection(title = "带图标", subtitle = "leadingIcon / trailingIcon") {
            val icon = ImageSource.Resource(R.drawable.demo_media_icon)
            Column(spacing = 8.dp) {
                Button(text = "Leading Icon", leadingIcon = icon, onClick = {})
                Button(text = "Trailing Icon", trailingIcon = icon, onClick = {})
                Button(
                    text = "Both Icons",
                    leadingIcon = icon,
                    trailingIcon = icon,
                    onClick = {},
                )
            }
        }

        DemoSection(title = "TextButton", subtitle = "尺寸对比和禁用态") {
            Row(
                spacing = 8.dp,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                ButtonSize.entries.forEach { size ->
                    TextButton(
                        text = size.name,
                        size = size,
                        onClick = {},
                    )
                }
            }
            TextButton(
                text = "Disabled TextButton",
                enabled = false,
                onClick = {},
                modifier = Modifier.margin(top = 8.dp),
            )
        }
    }
}

// ────────────────────────────────────────────
// IconButton
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseIconButton() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "变体对比", subtitle = "5 种 ButtonVariant") {
            Row(
                spacing = 8.dp,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                ButtonVariant.entries.forEach { variant ->
                    IconButton(
                        icon = ImageSource.Resource(R.drawable.demo_media_icon),
                        variant = variant,
                        onClick = {},
                    )
                }
            }
            Row(
                spacing = 8.dp,
                modifier = Modifier.margin(top = 4.dp),
            ) {
                ButtonVariant.entries.forEach { variant ->
                    Text(
                        text = variant.name,
                        style = UiTextStyle(fontSizeSp = 11.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }

        DemoSection(title = "自定义 Tint", subtitle = "着色自定义") {
            Row(spacing = 12.dp) {
                IconButton(
                    icon = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.primary,
                    onClick = {},
                )
                IconButton(
                    icon = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.accent,
                    onClick = {},
                )
                IconButton(
                    icon = ImageSource.Resource(R.drawable.demo_media_icon),
                    tint = Theme.colors.textSecondary,
                    onClick = {},
                )
            }
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Row(spacing = 8.dp) {
                IconButton(
                    icon = ImageSource.Resource(R.drawable.demo_media_icon),
                    onClick = {},
                )
                IconButton(
                    icon = ImageSource.Resource(R.drawable.demo_media_icon),
                    enabled = false,
                    onClick = {},
                )
            }
        }
    }
}

// ────────────────────────────────────────────
// SegmentedControl
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseSegmentedControl() {
    val selectedCompact = remember { mutableStateOf(0) }
    val selectedMedium = remember { mutableStateOf(1) }
    val selectedLarge = remember { mutableStateOf(2) }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "尺寸对比", subtitle = "Compact / Medium / Large") {
            val items = listOf("选项 A", "选项 B", "选项 C")

            Text(text = "Compact", style = UiTextStyle(fontSizeSp = 14.sp))
            SegmentedControl(
                items = items,
                selectedIndex = selectedCompact.value,
                onSelectionChange = { selectedCompact.value = it },
                size = SegmentedControlSize.Compact,
                modifier = Modifier.fillMaxWidth().margin(bottom = 12.dp),
            )

            Text(text = "Medium", style = UiTextStyle(fontSizeSp = 14.sp))
            SegmentedControl(
                items = items,
                selectedIndex = selectedMedium.value,
                onSelectionChange = { selectedMedium.value = it },
                size = SegmentedControlSize.Medium,
                modifier = Modifier.fillMaxWidth().margin(bottom = 12.dp),
            )

            Text(text = "Large", style = UiTextStyle(fontSizeSp = 14.sp))
            SegmentedControl(
                items = items,
                selectedIndex = selectedLarge.value,
                onSelectionChange = { selectedLarge.value = it },
                size = SegmentedControlSize.Large,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            SegmentedControl(
                items = listOf("A", "B", "C"),
                selectedIndex = 0,
                onSelectionChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ────────────────────────────────────────────
// Chip
// ────────────────────────────────────────────

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

// ────────────────────────────────────────────
// FAB
// ────────────────────────────────────────────

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

// ────────────────────────────────────────────
// TextField
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseTextField() {
    val filledValue = remember { mutableStateOf("") }
    val tonalValue = remember { mutableStateOf("") }
    val outlinedValue = remember { mutableStateOf("") }
    val errorValue = remember { mutableStateOf("错误示例") }
    val readOnlyValue = remember { mutableStateOf("只读内容") }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "变体对比", subtitle = "Filled / Tonal / Outlined") {
            TextField(
                value = filledValue.value,
                onValueChange = { filledValue.value = it },
                label = "Filled",
                hint = "请输入",
                variant = TextFieldVariant.Filled,
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            )
            TextField(
                value = tonalValue.value,
                onValueChange = { tonalValue.value = it },
                label = "Tonal",
                hint = "请输入",
                variant = TextFieldVariant.Tonal,
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            )
            TextField(
                value = outlinedValue.value,
                onValueChange = { outlinedValue.value = it },
                label = "Outlined",
                hint = "请输入",
                variant = TextFieldVariant.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "尺寸对比", subtitle = "Compact / Medium / Large") {
            TextFieldSize.entries.forEach { size ->
                val sizeValue = remember { mutableStateOf("") }
                TextField(
                    value = sizeValue.value,
                    onValueChange = { sizeValue.value = it },
                    label = size.name,
                    hint = "${size.name} size",
                    size = size,
                    modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
                )
            }
        }

        DemoSection(title = "辅助文本", subtitle = "label / hint / supportingText") {
            val v = remember { mutableStateOf("") }
            TextField(
                value = v.value,
                onValueChange = { v.value = it },
                label = "用户名",
                hint = "请输入用户名",
                supportingText = "用户名长度为 3-20 个字符",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "错误态", subtitle = "isError = true") {
            TextField(
                value = errorValue.value,
                onValueChange = { errorValue.value = it },
                label = "邮箱",
                isError = true,
                supportingText = "邮箱格式不正确",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "只读", subtitle = "readOnly = true") {
            TextField(
                value = readOnlyValue.value,
                onValueChange = {},
                label = "只读字段",
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "字数限制", subtitle = "maxLength = 20") {
            val v = remember { mutableStateOf("") }
            TextField(
                value = v.value,
                onValueChange = { v.value = it },
                label = "限制字数",
                maxLength = 20,
                supportingText = "${v.value.length}/20",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ────────────────────────────────────────────
// PasswordField / EmailField / NumberField / TextArea
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseTextFieldVariants() {
    val pwdValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    val numberValue = remember { mutableStateOf("") }
    val areaValue = remember { mutableStateOf("") }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "PasswordField", subtitle = "密码输入框") {
            PasswordField(
                value = pwdValue.value,
                onValueChange = { pwdValue.value = it },
                label = "密码",
                hint = "请输入密码",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "EmailField", subtitle = "邮箱输入框") {
            EmailField(
                value = emailValue.value,
                onValueChange = { emailValue.value = it },
                label = "邮箱",
                hint = "user@example.com",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "NumberField", subtitle = "数字输入框") {
            NumberField(
                value = numberValue.value,
                onValueChange = { numberValue.value = it },
                label = "数量",
                hint = "请输入数字",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "TextArea", subtitle = "多行文本输入") {
            TextArea(
                value = areaValue.value,
                onValueChange = { areaValue.value = it },
                label = "备注",
                hint = "请输入备注信息...",
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ────────────────────────────────────────────
// Checkbox
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseCheckbox() {
    val checked1 = remember { mutableStateOf(true) }
    val checked2 = remember { mutableStateOf(false) }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "基础用法", subtitle = "checked / unchecked 切换") {
            Checkbox(
                text = "选中状态",
                checked = checked1.value,
                onCheckedChange = { checked1.value = it },
            )
            Checkbox(
                text = "未选中状态",
                checked = checked2.value,
                onCheckedChange = { checked2.value = it },
            )
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Checkbox(
                text = "禁用 - 选中",
                checked = true,
                onCheckedChange = {},
                enabled = false,
            )
            Checkbox(
                text = "禁用 - 未选中",
                checked = false,
                onCheckedChange = {},
                enabled = false,
            )
        }

        DemoSection(title = "自定义颜色", subtitle = "checkedColor / uncheckedColor") {
            val c = remember { mutableStateOf(true) }
            Checkbox(
                text = "自定义颜色",
                checked = c.value,
                onCheckedChange = { c.value = it },
                checkedColor = Theme.colors.accent,
                uncheckedColor = Theme.colors.textSecondary,
            )
        }
    }
}

// ────────────────────────────────────────────
// Switch
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseSwitch() {
    val checked1 = remember { mutableStateOf(true) }
    val checked2 = remember { mutableStateOf(false) }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "基础用法", subtitle = "checked / unchecked 切换") {
            Switch(
                text = "已开启",
                checked = checked1.value,
                onCheckedChange = { checked1.value = it },
            )
            Switch(
                text = "已关闭",
                checked = checked2.value,
                onCheckedChange = { checked2.value = it },
            )
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Switch(
                text = "禁用 - 开启",
                checked = true,
                onCheckedChange = {},
                enabled = false,
            )
            Switch(
                text = "禁用 - 关闭",
                checked = false,
                onCheckedChange = {},
                enabled = false,
            )
        }

        DemoSection(title = "自定义颜色", subtitle = "thumbColor / trackColor") {
            val c = remember { mutableStateOf(true) }
            Switch(
                text = "自定义颜色",
                checked = c.value,
                onCheckedChange = { c.value = it },
                thumbColor = Theme.colors.accent,
                trackColor = Theme.colors.surfaceVariant,
            )
        }
    }
}

// ────────────────────────────────────────────
// RadioButton
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseRadioButton() {
    val selectedIndex = remember { mutableStateOf(0) }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "基础用法", subtitle = "单选按钮组") {
            listOf("选项 A", "选项 B", "选项 C").forEachIndexed { index, label ->
                RadioButton(
                    text = label,
                    checked = selectedIndex.value == index,
                    onCheckedChange = { if (it) selectedIndex.value = index },
                )
            }
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            RadioButton(
                text = "禁用 - 选中",
                checked = true,
                onCheckedChange = {},
                enabled = false,
            )
            RadioButton(
                text = "禁用 - 未选中",
                checked = false,
                onCheckedChange = {},
                enabled = false,
            )
        }

        DemoSection(title = "自定义颜色", subtitle = "checkedColor / uncheckedColor") {
            val c = remember { mutableStateOf(true) }
            RadioButton(
                text = "自定义颜色",
                checked = c.value,
                onCheckedChange = { c.value = it },
                checkedColor = Theme.colors.accent,
                uncheckedColor = Theme.colors.textSecondary,
            )
        }
    }
}

// ────────────────────────────────────────────
// Slider
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseSlider() {
    val value1 = remember { mutableStateOf(50) }
    val value2 = remember { mutableStateOf(25) }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "基础用法", subtitle = "默认范围 0-100") {
            Text(text = "当前值: ${value1.value}")
            Slider(
                value = value1.value,
                onValueChange = { value1.value = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "自定义范围", subtitle = "min=0, max=50") {
            Text(text = "当前值: ${value2.value}")
            Slider(
                value = value2.value,
                onValueChange = { value2.value = it },
                min = 0,
                max = 50,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Slider(
                value = 30,
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "自定义颜色", subtitle = "thumbColor / trackColor") {
            val v = remember { mutableStateOf(60) }
            Slider(
                value = v.value,
                onValueChange = { v.value = it },
                thumbColor = Theme.colors.accent,
                trackColor = Theme.colors.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ────────────────────────────────────────────
// SearchBar
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseSearchBar() {
    val query1 = remember { mutableStateOf("") }
    val query2 = remember { mutableStateOf("搜索内容") }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "基础搜索", subtitle = "带占位文本") {
            SearchBar(
                query = query1.value,
                onQueryChange = { query1.value = it },
                placeholder = "搜索...",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "带清除按钮", subtitle = "trailingIcon 自定义") {
            SearchBar(
                query = query2.value,
                onQueryChange = { query2.value = it },
                placeholder = "搜索...",
                trailingIcon = if (query2.value.isNotEmpty()) {
                    {
                        IconButton(
                            icon = ImageSource.Resource(R.drawable.demo_media_icon),
                            onClick = { query2.value = "" },
                        )
                    }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            SearchBar(
                query = "",
                onQueryChange = {},
                placeholder = "搜索已禁用",
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ────────────────────────────────────────────
// LinearProgressIndicator
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseLinearProgress() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "确定模式", subtitle = "progress = 0.0 ~ 1.0") {
            listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f).forEach { p ->
                Text(
                    text = "progress = $p",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                )
                LinearProgressIndicator(
                    progress = p,
                    modifier = Modifier.margin(bottom = 8.dp),
                )
            }
        }

        DemoSection(title = "不确定模式", subtitle = "progress = null") {
            LinearProgressIndicator()
        }

        DemoSection(title = "自定义颜色与粗细", subtitle = "indicatorColor / trackThickness") {
            LinearProgressIndicator(
                progress = 0.6f,
                indicatorColor = Theme.colors.accent,
                trackColor = Theme.colors.surfaceVariant,
            )
            LinearProgressIndicator(
                progress = 0.4f,
                trackThickness = 8.dp,
                modifier = Modifier.margin(top = 12.dp),
            )
        }
    }
}

// ────────────────────────────────────────────
// CircularProgressIndicator
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseCircularProgress() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "确定模式", subtitle = "progress = 0.0 ~ 1.0") {
            Row(
                spacing = 16.dp,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                listOf(0.25f, 0.5f, 0.75f, 1.0f).forEach { p ->
                    Column(spacing = 4.dp) {
                        CircularProgressIndicator(progress = p)
                        Text(
                            text = "${(p * 100).toInt()}%",
                            style = UiTextStyle(fontSizeSp = 12.sp),
                        )
                    }
                }
            }
        }

        DemoSection(title = "不确定模式", subtitle = "progress = null") {
            CircularProgressIndicator()
        }

        DemoSection(title = "自定义颜色与尺寸", subtitle = "indicatorColor / size") {
            Row(
                spacing = 16.dp,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                CircularProgressIndicator(
                    progress = 0.7f,
                    indicatorColor = Theme.colors.accent,
                )
                CircularProgressIndicator(
                    progress = 0.5f,
                    size = 64.dp,
                    trackThickness = 6.dp,
                )
            }
        }
    }
}

// ────────────────────────────────────────────
// Badge / BadgedBox
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseBadge() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "数字徽标", subtitle = "count = 数字") {
            Row(
                spacing = 24.dp,
                verticalAlignment = VerticalAlignment.Center,
            ) {
                listOf(1, 9, 99, 100).forEach { count ->
                    BadgedBox(
                        badge = { Badge(count = count) },
                    ) {
                        Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                    }
                }
            }
        }

        DemoSection(title = "圆点徽标", subtitle = "count = null") {
            BadgedBox(
                badge = { Badge() },
            ) {
                Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
            }
        }

        DemoSection(title = "自定义颜色", subtitle = "containerColor / contentColor") {
            Row(spacing = 24.dp) {
                BadgedBox(
                    badge = {
                        Badge(
                            count = 5,
                            containerColor = Theme.colors.accent,
                            contentColor = Theme.colors.surface,
                        )
                    },
                ) {
                    Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                }
                BadgedBox(
                    badge = {
                        Badge(
                            count = 3,
                            containerColor = Theme.colors.primary,
                            contentColor = Theme.colors.surface,
                        )
                    },
                ) {
                    Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                }
            }
        }
    }
}

// ────────────────────────────────────────────
// ListItem
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseListItem() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "单行", subtitle = "仅 headlineText") {
            ListItem(headlineText = "单行列表项")
            Divider()
            ListItem(headlineText = "另一个单行列表项")
        }

        DemoSection(title = "双行", subtitle = "headlineText + supportingText") {
            ListItem(
                headlineText = "双行列表项",
                supportingText = "这是支持文本，提供额外信息",
            )
            Divider()
            ListItem(
                headlineText = "另一个双行列表项",
                supportingText = "另一段支持文本",
            )
        }

        DemoSection(title = "完整模式", subtitle = "overline + leading + trailing") {
            ListItem(
                headlineText = "完整列表项",
                supportingText = "支持文本详细描述",
                overlineText = "上方标签",
                leadingContent = {
                    Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                },
                trailingContent = {
                    Text(
                        text = "详情",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                },
            )
        }

        DemoSection(title = "可点击", subtitle = "onClick 回调") {
            val clickCount = remember { mutableStateOf(0) }
            ListItem(
                headlineText = "点击我",
                supportingText = "已点击 ${clickCount.value} 次",
                leadingContent = {
                    Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                },
                onClick = { clickCount.value++ },
            )
        }
    }
}

// ────────────────────────────────────────────
// Card
// ────────────────────────────────────────────

internal fun UiTreeBuilder.ShowcaseCard() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "变体对比", subtitle = "Filled / Elevated / Outlined") {
            Card(
                variant = CardVariant.Filled,
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "Filled Card", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "默认填充样式",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "Elevated Card", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "带阴影的卡片",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "Outlined Card", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "边框样式的卡片",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }

        DemoSection(title = "可点击", subtitle = "onClick 回调") {
            val clickCount = remember { mutableStateOf(0) }
            Card(
                onClick = { clickCount.value++ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "可点击的卡片", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "已点击 ${clickCount.value} 次",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Card(
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "禁用的卡片", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "此卡片不可交互",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }
    }
}
