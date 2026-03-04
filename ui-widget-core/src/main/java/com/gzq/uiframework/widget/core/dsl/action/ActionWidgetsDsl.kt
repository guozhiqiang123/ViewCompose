package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps

fun UiTreeBuilder.Button(
    text: String,
    onClick: (() -> Unit)? = null,
    leadingIcon: ImageSource.Resource? = null,
    trailingIcon: ImageSource.Resource? = null,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    style: UiTextStyle = ButtonDefaults.textStyle(size),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val contentColor = ButtonDefaults.contentColor(variant, enabled)
    val iconSizeValue = ButtonDefaults.iconSize(size)
    val iconSpacingValue = ButtonDefaults.iconSpacing(size)
    emit(
        type = NodeType.Button,
        key = key,
        spec = ButtonNodeProps(
            text = text,
            enabled = enabled,
            onClick = onClick,
            textColor = contentColor,
            textSizeSp = style.fontSizeSp,
            backgroundColor = ButtonDefaults.containerColor(variant, enabled),
            borderWidth = ButtonDefaults.borderWidth(variant),
            borderColor = ButtonDefaults.borderColor(variant, enabled),
            cornerRadius = ButtonDefaults.cornerRadius(),
            rippleColor = ButtonDefaults.pressedColor(),
            minHeight = ButtonDefaults.height(size),
            paddingHorizontal = ButtonDefaults.horizontalPadding(size),
            paddingVertical = ButtonDefaults.verticalPadding(size),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            iconTint = contentColor,
            iconSize = iconSizeValue,
            iconSpacing = iconSpacingValue,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.IconButton(
    icon: ImageSource,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val tint = IconButtonDefaults.contentColor(variant, enabled)
    val contentPaddingValue = IconButtonDefaults.contentPadding(size)
    val semanticModifier = Modifier
        .size(
            width = IconButtonDefaults.size(size),
            height = IconButtonDefaults.size(size),
        )
        .then(
            if (enabled && onClick != null) {
                Modifier.clickable(onClick)
            } else {
                Modifier
            },
        )
        .then(modifier)
    emit(
        type = NodeType.IconButton,
        key = key,
        spec = IconButtonNodeProps(
            contentDescription = contentDescription,
            contentScale = ImageContentScale.Inside,
            tint = tint,
            source = icon,
            placeholder = null,
            error = null,
            fallback = null,
            remoteImageLoader = ImageLoading.current,
            enabled = enabled,
            backgroundColor = IconButtonDefaults.containerColor(variant, enabled),
            borderWidth = IconButtonDefaults.borderWidth(variant),
            borderColor = IconButtonDefaults.borderColor(variant, enabled),
            cornerRadius = IconButtonDefaults.cornerRadius(),
            rippleColor = IconButtonDefaults.pressedColor(),
            contentPadding = contentPaddingValue,
        ),
        modifier = semanticModifier,
    )
}

fun UiTreeBuilder.SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    size: SegmentedControlSize = SegmentedControlSize.Medium,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val resolvedItems = items.map { label -> SegmentedControlItem(label = label) }
    val backgroundColor = SegmentedControlDefaults.backgroundColor(enabled)
    val indicatorColor = SegmentedControlDefaults.indicatorColor(enabled)
    val cornerRadius = SegmentedControlDefaults.cornerRadius()
    val textColor = SegmentedControlDefaults.textColor(enabled)
    val selectedTextColor = SegmentedControlDefaults.selectedTextColor(enabled)
    val rippleColor = SegmentedControlDefaults.rippleColor(enabled)
    val textSizeSp = SegmentedControlDefaults.textStyle(size).fontSizeSp
    val horizontalPadding = SegmentedControlDefaults.horizontalPadding(size)
    val verticalPadding = SegmentedControlDefaults.verticalPadding(size)
    emit(
        type = NodeType.SegmentedControl,
        key = key,
        spec = SegmentedControlNodeProps(
            items = resolvedItems,
            selectedIndex = selectedIndex,
            onSelectionChange = onSelectionChange,
            enabled = enabled,
            backgroundColor = backgroundColor,
            indicatorColor = indicatorColor,
            cornerRadius = cornerRadius,
            textColor = textColor,
            selectedTextColor = selectedTextColor,
            rippleColor = rippleColor,
            textSizeSp = textSizeSp,
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
        ),
        modifier = Modifier
            .height(SegmentedControlDefaults.height(size))
            .then(modifier),
    )
}
