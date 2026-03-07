package com.viewcompose.widget.core

import com.viewcompose.renderer.layout.BoxAlignment
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.backgroundColor
import com.viewcompose.renderer.modifier.clip
import com.viewcompose.renderer.modifier.cornerRadius
import com.viewcompose.renderer.modifier.minWidth
import com.viewcompose.renderer.modifier.height
import com.viewcompose.renderer.modifier.padding
import com.viewcompose.renderer.modifier.size

fun UiTreeBuilder.Badge(
    count: Int? = null,
    containerColor: Int = BadgeDefaults.containerColor(),
    contentColor: Int = BadgeDefaults.contentColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    if (count != null && count <= 0) return
    if (count == null) {
        val dotSize = BadgeDefaults.dotSize()
        Box(
            key = key,
            modifier = Modifier
                .size(width = dotSize, height = dotSize)
                .backgroundColor(containerColor)
                .cornerRadius(dotSize / 2)
                .clip()
                .then(modifier),
        ) {}
    } else {
        val displayText = if (count > 99) "99+" else count.toString()
        val pillHeight = BadgeDefaults.pillHeight()
        val hPadding = BadgeDefaults.pillHorizontalPadding()
        val style = BadgeDefaults.textStyle()
        Box(
            key = key,
            contentAlignment = BoxAlignment.Center,
            modifier = Modifier
                .height(pillHeight)
                .minWidth(BadgeDefaults.pillMinWidth())
                .backgroundColor(containerColor)
                .cornerRadius(pillHeight / 2)
                .clip()
                .padding(horizontal = hPadding)
                .then(modifier),
        ) {
            Text(
                text = displayText,
                style = style,
                color = contentColor,
            )
        }
    }
}

fun UiTreeBuilder.BadgedBox(
    badge: UiTreeBuilder.() -> Unit,
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    Box(key = key, modifier = modifier) {
        content()
        Box(
            modifier = Modifier.align(BoxAlignment.TopEnd),
        ) {
            badge()
        }
    }
}
