package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.elevation
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.padding

fun UiTreeBuilder.TopAppBar(
    title: String,
    navigationIcon: (UiTreeBuilder.() -> Unit)? = null,
    actions: (RowScope.() -> Unit)? = null,
    containerColor: Int = TopAppBarDefaults.containerColor(),
    titleColor: Int = TopAppBarDefaults.titleColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val semanticModifier = Modifier
        .fillMaxWidth()
        .height(TopAppBarDefaults.height())
        .backgroundColor(containerColor)
        .padding(horizontal = TopAppBarDefaults.horizontalPadding())
        .then(modifier)
    Row(
        key = key,
        verticalAlignment = VerticalAlignment.Center,
        modifier = semanticModifier,
    ) {
        if (navigationIcon != null) {
            navigationIcon()
        }
        Text(
            text = title,
            style = TopAppBarDefaults.titleStyle(),
            color = titleColor,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(left = TopAppBarDefaults.titleStartPadding()),
        )
        if (actions != null) {
            Row(
                verticalAlignment = VerticalAlignment.Center,
            ) {
                actions()
            }
        }
    }
}

fun UiTreeBuilder.BottomAppBar(
    containerColor: Int = BottomAppBarDefaults.containerColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: RowScope.() -> Unit,
) {
    val semanticModifier = Modifier
        .fillMaxWidth()
        .height(BottomAppBarDefaults.height())
        .backgroundColor(containerColor)
        .elevation(BottomAppBarDefaults.elevation())
        .padding(horizontal = BottomAppBarDefaults.horizontalPadding())
        .then(modifier)
    Row(
        key = key,
        verticalAlignment = VerticalAlignment.Center,
        modifier = semanticModifier,
        content = content,
    )
}
