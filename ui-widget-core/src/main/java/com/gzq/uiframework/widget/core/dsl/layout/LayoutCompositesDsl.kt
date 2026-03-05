package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.border
import com.gzq.uiframework.renderer.modifier.clip
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.elevation
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.minHeight
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.rippleColor
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.VerticalAlignment

fun UiTreeBuilder.Card(
    onClick: (() -> Unit)? = null,
    variant: CardVariant = CardVariant.Filled,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    val bgColor = CardDefaults.containerColor(variant)
    val radius = CardDefaults.cornerRadius()
    val elev = CardDefaults.elevation(variant)
    val bw = CardDefaults.borderWidth(variant)
    val bc = CardDefaults.borderColor(variant)
    val semanticModifier = Modifier
        .backgroundColor(bgColor)
        .cornerRadius(radius)
        .clip()
        .let { m -> if (elev > 0) m.elevation(elev) else m }
        .let { m -> if (bw > 0) m.border(bw, bc) else m }
        .let { m ->
            if (enabled && onClick != null) {
                m.rippleColor(CardDefaults.pressedColor()).clickable(onClick)
            } else {
                m
            }
        }
        .then(modifier)
    ProvideContentColor(CardDefaults.contentColor()) {
        Box(
            key = key,
            modifier = semanticModifier,
            content = content,
        )
    }
}

fun UiTreeBuilder.ListItem(
    headlineText: String,
    supportingText: String? = null,
    overlineText: String? = null,
    leadingContent: (UiTreeBuilder.() -> Unit)? = null,
    trailingContent: (UiTreeBuilder.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val hPadding = ListItemDefaults.horizontalPadding()
    val vPadding = ListItemDefaults.verticalPadding()
    val semanticModifier = Modifier
        .fillMaxWidth()
        .minHeight(ListItemDefaults.minHeight())
        .padding(horizontal = hPadding, vertical = vPadding)
        .let { m ->
            if (onClick != null) {
                m.clickable(onClick)
            } else {
                m
            }
        }
        .then(modifier)
    Row(
        key = key,
        spacing = ListItemDefaults.leadingTrailingSpacing(),
        verticalAlignment = VerticalAlignment.Center,
        modifier = semanticModifier,
    ) {
        if (leadingContent != null) {
            leadingContent()
        }
        Column(
            spacing = ListItemDefaults.textSpacing(),
            modifier = Modifier.weight(1f),
        ) {
            if (overlineText != null) {
                Text(
                    text = overlineText,
                    style = ListItemDefaults.overlineStyle(),
                    color = ListItemDefaults.overlineColor(),
                    maxLines = 1,
                )
            }
            Text(
                text = headlineText,
                style = ListItemDefaults.headlineStyle(),
                color = ListItemDefaults.headlineColor(),
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = ListItemDefaults.supportingStyle(),
                    color = ListItemDefaults.supportingColor(),
                )
            }
        }
        if (trailingContent != null) {
            trailingContent()
        }
    }
}

fun UiTreeBuilder.Scaffold(
    topBar: (UiTreeBuilder.() -> Unit)? = null,
    bottomBar: (UiTreeBuilder.() -> Unit)? = null,
    floatingActionButton: (UiTreeBuilder.() -> Unit)? = null,
    containerColor: Int = ScaffoldDefaults.containerColor(),
    contentColor: Int = ScaffoldDefaults.contentColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    ProvideContentColor(contentColor) {
        Column(
            key = key,
            modifier = Modifier
                .fillMaxSize()
                .backgroundColor(containerColor)
                .then(modifier),
        ) {
            if (topBar != null) {
                topBar()
            }
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                content()
                if (floatingActionButton != null) {
                    Box(
                        contentAlignment = BoxAlignment.BottomEnd,
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                    ) {
                        floatingActionButton()
                    }
                }
            }
            if (bottomBar != null) {
                bottomBar()
            }
        }
    }
}
