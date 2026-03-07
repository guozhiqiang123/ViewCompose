package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clip
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.minWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.size

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
