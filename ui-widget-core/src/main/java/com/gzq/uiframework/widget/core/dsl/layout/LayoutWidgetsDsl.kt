package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.props
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.FlowRowNodeProps
import com.gzq.uiframework.renderer.node.spec.PullToRefreshNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.ScrollableColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.ScrollableRowNodeProps

fun UiTreeBuilder.Box(
    key: Any? = null,
    contentAlignment: BoxAlignment = BoxAlignment.TopStart,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.Box,
        key = key,
        spec = BoxNodeProps(
            contentAlignment = contentAlignment,
        ),
        modifier = modifier,
        children = BoxScope().apply(content).build(),
    )
}

fun UiTreeBuilder.AnchorTarget(
    anchorId: String,
    key: Any? = null,
    contentAlignment: BoxAlignment = BoxAlignment.TopStart,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.Box,
        key = key,
        props = props {
            set(TypedPropKeys.AnchorId, anchorId)
        },
        spec = BoxNodeProps(
            contentAlignment = contentAlignment,
        ),
        modifier = modifier,
        children = BoxScope().apply(content).build(),
    )
}

fun UiTreeBuilder.Surface(
    key: Any? = null,
    variant: SurfaceVariant = SurfaceVariant.Default,
    enabled: Boolean = true,
    contentAlignment: BoxAlignment = BoxAlignment.TopStart,
    contentColor: Int = SurfaceDefaults.contentColor(variant),
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    val semanticModifier = Modifier
        .then(
            if (enabled && onClick != null) {
                Modifier.clickable(onClick)
            } else {
                Modifier
            },
        )
        .then(modifier)
    ProvideContentColor(contentColor) {
        emitResolved(
            type = NodeType.Surface,
            key = key,
            props = props {
                set(TypedPropKeys.BoxAlignment, contentAlignment)
                set(TypedPropKeys.StyleBackgroundColor, SurfaceDefaults.backgroundColor(variant))
                set(TypedPropKeys.StyleCornerRadius, SurfaceDefaults.cardCornerRadius())
                set(TypedPropKeys.StyleRippleColor, SurfaceDefaults.pressedColor())
                if (!enabled) {
                    set(TypedPropKeys.StyleAlpha, SurfaceDefaults.disabledAlpha())
                }
            },
            spec = BoxNodeProps(
                contentAlignment = contentAlignment,
            ),
            modifier = semanticModifier,
            children = BoxScope().apply(content).build(),
        )
    }
}

fun UiTreeBuilder.Spacer(
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Spacer,
        key = key,
        modifier = modifier,
    )
}

@Deprecated(
    message = "FlexibleSpacer is parent-data. Prefer RowScope.FlexibleSpacer(...) or ColumnScope.FlexibleSpacer(...).",
)
fun UiTreeBuilder.FlexibleSpacer(
    weight: Float = 1f,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    require(weight > 0f) {
        "weight must be > 0"
    }
    Spacer(
        key = key,
        modifier = modifier.then(WeightModifierElement(weight)),
    )
}

fun UiTreeBuilder.Divider(
    color: Int = DividerDefaults.color(),
    thickness: Int = DividerDefaults.thickness(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Divider,
        key = key,
        spec = DividerNodeProps(
            color = color,
            thickness = thickness,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Row(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    modifier: Modifier = Modifier,
    content: RowScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.Row,
        key = key,
        spec = RowNodeProps(
            spacing = spacing,
            arrangement = arrangement,
            verticalAlignment = verticalAlignment,
        ),
        modifier = modifier,
        children = RowScope().apply(content).build(),
    )
}

fun UiTreeBuilder.Column(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    modifier: Modifier = Modifier,
    content: ColumnScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.Column,
        key = key,
        spec = ColumnNodeProps(
            spacing = spacing,
            arrangement = arrangement,
            horizontalAlignment = horizontalAlignment,
        ),
        modifier = modifier,
        children = ColumnScope().apply(content).build(),
    )
}

fun UiTreeBuilder.ScrollableColumn(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    modifier: Modifier = Modifier,
    content: ColumnScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.ScrollableColumn,
        key = key,
        spec = ScrollableColumnNodeProps(
            spacing = spacing,
            arrangement = arrangement,
            horizontalAlignment = horizontalAlignment,
        ),
        modifier = modifier,
        children = ColumnScope().apply(content).build(),
    )
}

fun UiTreeBuilder.ScrollableRow(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    modifier: Modifier = Modifier,
    content: RowScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.ScrollableRow,
        key = key,
        spec = ScrollableRowNodeProps(
            spacing = spacing,
            arrangement = arrangement,
            verticalAlignment = verticalAlignment,
        ),
        modifier = modifier,
        children = RowScope().apply(content).build(),
    )
}

fun UiTreeBuilder.FlowRow(
    key: Any? = null,
    horizontalSpacing: Int = 0,
    verticalSpacing: Int = 0,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
    content: LayoutScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.FlowRow,
        key = key,
        spec = FlowRowNodeProps(
            horizontalSpacing = horizontalSpacing,
            verticalSpacing = verticalSpacing,
            maxItemsInEachRow = maxItemsInEachRow,
        ),
        modifier = modifier,
        children = LayoutScope().apply(content).build(),
    )
}

fun UiTreeBuilder.FlowColumn(
    key: Any? = null,
    horizontalSpacing: Int = 0,
    verticalSpacing: Int = 0,
    maxItemsInEachColumn: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
    content: LayoutScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.FlowColumn,
        key = key,
        spec = FlowColumnNodeProps(
            horizontalSpacing = horizontalSpacing,
            verticalSpacing = verticalSpacing,
            maxItemsInEachColumn = maxItemsInEachColumn,
        ),
        modifier = modifier,
        children = LayoutScope().apply(content).build(),
    )
}

fun UiTreeBuilder.PullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    indicatorColor: Int = PullToRefreshDefaults.indicatorColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: ScrollableScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.PullToRefresh,
        key = key,
        spec = PullToRefreshNodeProps(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            indicatorColor = indicatorColor,
        ),
        modifier = modifier,
        children = ScrollableScope().apply(content).build(),
    )
}
