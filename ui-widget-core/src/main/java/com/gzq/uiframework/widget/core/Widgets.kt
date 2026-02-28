package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.View
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
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

fun UiTreeBuilder.Button(
    text: String,
    onClick: (() -> Unit)? = null,
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Button,
        key = key,
        props = Props(
            values = buildMap {
                put(PropKeys.TEXT, text)
                put(PropKeys.ON_CLICK, onClick)
            },
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.AndroidView(
    factory: (Context) -> View,
    update: (View) -> Unit = {},
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.AndroidView,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.VIEW_FACTORY to factory,
                PropKeys.VIEW_UPDATE to update,
            ),
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Box(
    key: Any? = null,
    contentAlignment: BoxAlignment = BoxAlignment.TopStart,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
        type = NodeType.Box,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.BOX_ALIGNMENT to contentAlignment,
            ),
        ),
        modifier = modifier,
        content = content,
    )
}

fun UiTreeBuilder.Spacer(
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Spacer,
        key = key,
        modifier = modifier,
    )
}

fun UiTreeBuilder.FlexibleSpacer(
    weight: Float = 1f,
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    Spacer(
        key = key,
        modifier = modifier.weight(weight),
    )
}

fun UiTreeBuilder.Divider(
    color: Int,
    thickness: Int = 1,
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Divider,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.DIVIDER_COLOR to color,
                PropKeys.DIVIDER_THICKNESS to thickness,
            ),
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Row(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
        type = NodeType.Row,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.LINEAR_SPACING to spacing,
                PropKeys.ROW_MAIN_AXIS_ARRANGEMENT to arrangement,
                PropKeys.ROW_VERTICAL_ALIGNMENT to verticalAlignment,
            ),
        ),
        modifier = modifier,
        content = content,
    )
}

fun UiTreeBuilder.Column(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
        type = NodeType.Column,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.LINEAR_SPACING to spacing,
                PropKeys.COLUMN_MAIN_AXIS_ARRANGEMENT to arrangement,
                PropKeys.COLUMN_HORIZONTAL_ALIGNMENT to horizontalAlignment,
            ),
        ),
        modifier = modifier,
        content = content,
    )
}

fun <T> UiTreeBuilder.LazyColumn(
    items: List<T>,
    key: ((T) -> Any)? = null,
    modifier: Modifier = Modifier.Empty,
    itemContent: UiTreeBuilder.(T) -> Unit,
) {
    emit(
        type = NodeType.LazyColumn,
        props = Props(
            values = mapOf(
                PropKeys.LAZY_ITEMS to items.map { item ->
                    LazyListItem(
                        key = key?.invoke(item),
                        contentToken = item,
                        sessionFactory = LazyListItemSessionFactory { container ->
                            WidgetLazyListItemSession(
                                container = container,
                                content = {
                                    itemContent(item)
                                },
                            )
                        },
                        sessionUpdater = { session ->
                            (session as? WidgetLazyListItemSession)?.updateContent {
                                itemContent(item)
                            }
                        },
                    )
                },
            ),
        ),
        modifier = modifier,
    )
}

private class WidgetLazyListItemSession(
    container: android.view.ViewGroup,
    content: UiTreeBuilder.() -> Unit,
) : LazyListItemSession {
    private var renderContent = content
    private val session = RenderSession(
        container = container,
        content = {
            renderContent()
        },
    )

    override fun render() {
        session.render()
    }

    override fun dispose() {
        session.dispose()
    }

    fun updateContent(
        content: UiTreeBuilder.() -> Unit,
    ) {
        renderContent = content
    }
}
