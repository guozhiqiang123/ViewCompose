package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.Props

fun UiTreeBuilder.Text(
    text: String,
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Text,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.TEXT to text,
            ),
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Box(
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
        type = NodeType.Box,
        key = key,
        modifier = modifier,
        content = content,
    )
}

fun UiTreeBuilder.Row(
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
        type = NodeType.Row,
        key = key,
        modifier = modifier,
        content = content,
    )
}

fun UiTreeBuilder.Column(
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
        type = NodeType.Column,
        key = key,
        modifier = modifier,
        content = content,
    )
}
