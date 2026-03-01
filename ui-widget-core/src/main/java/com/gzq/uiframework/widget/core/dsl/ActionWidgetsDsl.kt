package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.props
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
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.OnClick, onClick)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.TextColor, contentColor)
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            set(TypedPropKeys.StyleMinHeight, ButtonDefaults.height(size))
            set(TypedPropKeys.StylePaddingLeft, ButtonDefaults.horizontalPadding(size))
            set(TypedPropKeys.StylePaddingTop, ButtonDefaults.verticalPadding(size))
            set(TypedPropKeys.StylePaddingRight, ButtonDefaults.horizontalPadding(size))
            set(TypedPropKeys.StylePaddingBottom, ButtonDefaults.verticalPadding(size))
            set(TypedPropKeys.StyleBackgroundColor, ButtonDefaults.containerColor(variant, enabled))
            set(TypedPropKeys.StyleBorderWidth, ButtonDefaults.borderWidth(variant))
            set(TypedPropKeys.StyleBorderColor, ButtonDefaults.borderColor(variant, enabled))
            set(TypedPropKeys.StyleCornerRadius, ButtonDefaults.cornerRadius())
            set(TypedPropKeys.StyleRippleColor, ButtonDefaults.pressedColor())
            set(TypedPropKeys.ButtonIconSize, iconSizeValue)
            set(TypedPropKeys.ButtonIconSpacing, iconSpacingValue)
            set(TypedPropKeys.ButtonLeadingIcon, leadingIcon)
            set(TypedPropKeys.ButtonTrailingIcon, trailingIcon)
        },
        spec = ButtonNodeProps(
            text = text,
            enabled = enabled,
            iconSpacing = iconSpacingValue,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            iconTint = contentColor,
            iconSize = iconSizeValue,
            onClick = onClick,
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
        props = props {
            set(TypedPropKeys.ImageSource, icon)
            set(TypedPropKeys.ImageContentDescription, contentDescription)
            set(TypedPropKeys.ImageContentScale, ImageContentScale.Inside)
            set(TypedPropKeys.ImageTint, tint)
            set(TypedPropKeys.OnClick, onClick)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.ImageRemoteLoader, ImageLoading.current)
            set(TypedPropKeys.StylePaddingLeft, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StylePaddingTop, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StylePaddingRight, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StylePaddingBottom, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StyleBackgroundColor, IconButtonDefaults.containerColor(variant, enabled))
            set(TypedPropKeys.StyleBorderWidth, IconButtonDefaults.borderWidth(variant))
            set(TypedPropKeys.StyleBorderColor, IconButtonDefaults.borderColor(variant, enabled))
            set(TypedPropKeys.StyleCornerRadius, IconButtonDefaults.cornerRadius())
            set(TypedPropKeys.StyleRippleColor, IconButtonDefaults.pressedColor())
        },
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
        props = props {
            set(TypedPropKeys.SegmentItems, resolvedItems)
            set(TypedPropKeys.SegmentSelectedIndex, selectedIndex)
            set(TypedPropKeys.OnSegmentSelected, onSelectionChange)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.SegmentBackgroundColor, backgroundColor)
            set(TypedPropKeys.SegmentIndicatorColor, indicatorColor)
            set(TypedPropKeys.SegmentCornerRadius, cornerRadius)
            set(TypedPropKeys.SegmentTextColor, textColor)
            set(TypedPropKeys.SegmentSelectedTextColor, selectedTextColor)
            set(TypedPropKeys.SegmentRippleColor, rippleColor)
            set(TypedPropKeys.SegmentTextSizeSp, textSizeSp)
            set(TypedPropKeys.SegmentContentPaddingHorizontal, horizontalPadding)
            set(TypedPropKeys.SegmentContentPaddingVertical, verticalPadding)
        },
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
