package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.clip
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.elevation
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.rippleColor
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.node.ImageSource

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
        .rippleColor(FabDefaults.pressedColor())
        .clickable(onClick)
        .then(modifier)
    ProvideContentColor(contentColor) {
        Box(
            key = key,
            contentAlignment = BoxAlignment.Center,
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
        .rippleColor(FabDefaults.pressedColor())
        .clickable(onClick)
        .padding(horizontal = FabDefaults.extendedHorizontalPadding())
        .then(modifier)
    ProvideContentColor(contentColor) {
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
