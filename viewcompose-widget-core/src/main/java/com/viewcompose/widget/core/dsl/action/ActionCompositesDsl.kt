package com.viewcompose.widget.core

import com.viewcompose.renderer.layout.BoxAlignment
import com.viewcompose.renderer.layout.VerticalAlignment
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.alpha
import com.viewcompose.renderer.modifier.backgroundColor
import com.viewcompose.renderer.modifier.border
import com.viewcompose.renderer.modifier.clickable
import com.viewcompose.renderer.modifier.clip
import com.viewcompose.renderer.modifier.cornerRadius
import com.viewcompose.renderer.modifier.elevation
import com.viewcompose.renderer.modifier.height
import com.viewcompose.renderer.modifier.padding
import com.viewcompose.renderer.modifier.size
import com.viewcompose.renderer.node.ImageSource

fun UiTreeBuilder.FloatingActionButton(
    onClick: () -> Unit,
    size: FabSize = FabSize.Medium,
    containerColor: Int = FabDefaults.containerColor(),
    contentColor: Int = FabDefaults.contentColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: UiTreeBuilder.() -> Unit,
) {
    val fabSize = FabDefaults.size(size)
    val radius = FabDefaults.cornerRadius(size)
    val semanticModifier = Modifier
        .size(width = fabSize, height = fabSize)
        .backgroundColor(containerColor)
        .cornerRadius(radius)
        .elevation(FabDefaults.elevation())
        .clip()
        .clickable(onClick)
        .then(modifier)
    ProvideLocal(LocalContentColor, contentColor) {
        Box(
            key = key,
            contentAlignment = BoxAlignment.Center,
            rippleColor = FabDefaults.pressedColor(),
            modifier = semanticModifier,
        ) {
            content()
        }
    }
}

fun UiTreeBuilder.ExtendedFloatingActionButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageSource? = null,
    containerColor: Int = FabDefaults.containerColor(),
    contentColor: Int = FabDefaults.contentColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val radius = FabDefaults.extendedCornerRadius()
    val semanticModifier = Modifier
        .height(FabDefaults.extendedHeight())
        .backgroundColor(containerColor)
        .cornerRadius(radius)
        .elevation(FabDefaults.elevation())
        .clip()
        .clickable(onClick)
        .padding(horizontal = FabDefaults.extendedHorizontalPadding())
        .then(modifier)
    ProvideLocal(LocalContentColor, contentColor) {
        Row(
            key = key,
            spacing = if (icon != null) FabDefaults.extendedIconSpacing() else 0,
            verticalAlignment = VerticalAlignment.Center,
            modifier = semanticModifier,
        ) {
            if (icon != null) {
                Icon(
                    source = icon,
                    tint = contentColor,
                    size = FabDefaults.iconSize(FabSize.Medium),
                )
            }
            Text(
                text = text,
                style = FabDefaults.extendedTextStyle(),
                color = contentColor,
            )
        }
    }
}

fun UiTreeBuilder.Chip(
    label: String,
    onClick: () -> Unit,
    variant: ChipVariant = ChipVariant.Assist,
    selected: Boolean = false,
    leadingIcon: ImageSource? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val bgColor = ChipDefaults.containerColor(variant, selected, enabled)
    val cColor = ChipDefaults.contentColor(variant, selected, enabled)
    val bw = ChipDefaults.borderWidth(variant, selected)
    val bc = ChipDefaults.borderColor(variant, selected, enabled)
    val radius = ChipDefaults.cornerRadius()
    val leftPadding = if (leadingIcon != null) {
        ChipDefaults.leadingIconPadding()
    } else {
        ChipDefaults.horizontalPadding()
    }
    val rightPadding = if (onTrailingIconClick != null) {
        ChipDefaults.leadingIconPadding()
    } else {
        ChipDefaults.horizontalPadding()
    }
    val semanticModifier = Modifier
        .height(ChipDefaults.height())
        .backgroundColor(bgColor)
        .let { m -> if (bw > 0) m.border(bw, bc) else m }
        .cornerRadius(radius)
        .clip()
        .let { m ->
            if (enabled) {
                m.clickable(onClick)
            } else {
                m.alpha(0.38f)
            }
        }
        .padding(left = leftPadding, right = rightPadding)
        .then(modifier)
    ProvideLocal(LocalContentColor, cColor) {
        Row(
            key = key,
            spacing = ChipDefaults.iconSpacing(),
            verticalAlignment = VerticalAlignment.Center,
            modifier = semanticModifier,
        ) {
            if (leadingIcon != null) {
                Icon(
                    source = leadingIcon,
                    tint = cColor,
                    size = ChipDefaults.iconSize(),
                )
            }
            Text(
                text = label,
                style = ChipDefaults.textStyle(),
                color = cColor,
                maxLines = 1,
            )
            if (onTrailingIconClick != null) {
                Icon(
                    source = ImageSource.Resource(android.R.drawable.ic_menu_close_clear_cancel),
                    tint = cColor,
                    size = ChipDefaults.trailingIconSize(),
                    modifier = Modifier.clickable(onTrailingIconClick),
                )
            }
        }
    }
}
