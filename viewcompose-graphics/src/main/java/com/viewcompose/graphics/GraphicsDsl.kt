package com.viewcompose.graphics

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.DrawBehindModifierElement
import com.viewcompose.ui.modifier.DrawWithCacheModifierElement
import com.viewcompose.ui.modifier.DrawWithContentModifierElement
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.CanvasNodeProps
import com.viewcompose.widget.core.UiTreeBuilder

typealias DrawBlock = com.viewcompose.ui.graphics.DrawBlock
typealias DrawContentBlock = com.viewcompose.ui.graphics.DrawContentBlock
typealias DrawCacheBuildBlock = com.viewcompose.ui.graphics.DrawCacheBuildBlock
typealias DrawContext = com.viewcompose.ui.graphics.DrawContext
typealias DrawContentScope = com.viewcompose.ui.graphics.DrawContentScope
typealias DrawCacheScope = com.viewcompose.ui.graphics.DrawCacheScope

fun UiTreeBuilder.Canvas(
    key: Any? = null,
    modifier: Modifier = Modifier,
    onDraw: DrawBlock,
) {
    emit(
        type = NodeType.Canvas,
        key = key,
        spec = CanvasNodeProps(
            onDraw = onDraw,
        ),
        modifier = modifier,
    )
}

fun Modifier.drawBehind(
    key: Any = Unit,
    onDraw: DrawBlock,
): Modifier {
    return then(
        DrawBehindModifierElement(
            key = key,
            onDraw = onDraw,
        ),
    )
}

fun Modifier.drawWithContent(
    key: Any = Unit,
    onDraw: DrawContentBlock,
): Modifier {
    return then(
        DrawWithContentModifierElement(
            key = key,
            onDraw = onDraw,
        ),
    )
}

fun Modifier.drawWithCache(
    key: Any = Unit,
    onBuildDrawCache: DrawCacheBuildBlock,
): Modifier {
    return then(
        DrawWithCacheModifierElement(
            key = key,
            onBuildDrawCache = onBuildDrawCache,
        ),
    )
}

fun Modifier.draw(
    key: Any = Unit,
    onDraw: DrawBlock,
): Modifier {
    return drawBehind(
        key = key,
        onDraw = onDraw,
    )
}

fun Modifier.drawCache(
    key: Any = Unit,
    onBuildDrawCache: DrawCacheBuildBlock,
): Modifier {
    return drawWithCache(
        key = key,
        onBuildDrawCache = onBuildDrawCache,
    )
}
