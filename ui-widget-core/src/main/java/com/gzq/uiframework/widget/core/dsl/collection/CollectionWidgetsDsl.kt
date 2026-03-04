package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import com.gzq.uiframework.renderer.view.lazy.LazyListState

fun <T> UiTreeBuilder.LazyColumn(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentPadding: Int = 0,
    spacing: Int = 0,
    state: LazyListState? = null,
    modifier: Modifier = Modifier,
    itemContent: UiTreeBuilder.(T) -> Unit,
) {
    val localSnapshot = LocalContext.snapshot()
    val resolvedItems = items.map { item ->
        LazyListItem(
            key = key?.invoke(item),
            contentToken = item,
            sessionFactory = LazyListItemSessionFactory { container ->
                WidgetLazyListItemSession(
                    container = container,
                    localSnapshot = localSnapshot,
                    content = {
                        itemContent(item)
                    },
                )
            },
            sessionUpdater = { session ->
                (session as? WidgetLazyListItemSession)?.updateContent(
                    localSnapshot = localSnapshot,
                    content = {
                        itemContent(item)
                    },
                )
            },
        )
    }
    emit(
        type = NodeType.LazyColumn,
        spec = LazyColumnNodeProps(
            contentPadding = contentPadding,
            spacing = spacing,
            items = resolvedItems,
            state = state,
        ),
        modifier = modifier,
    )
}

@UiDslMarker
class TabPagerScope internal constructor() {
    private val pages = mutableListOf<TabPagerPage>()

    fun Page(
        title: String,
        key: Any? = title,
        contentToken: Any? = title,
        content: UiTreeBuilder.() -> Unit,
    ) {
        pages += TabPagerPage(
            title = title,
            key = key,
            contentToken = contentToken,
            content = content,
        )
    }

    internal fun build(): List<TabPagerPage> = pages.toList()
}

fun UiTreeBuilder.TabPager(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    key: Any? = null,
    backgroundColor: Int = TabPagerDefaults.backgroundColor(),
    indicatorColor: Int = TabPagerDefaults.indicatorColor(),
    cornerRadius: Int = TabPagerDefaults.cornerRadius(),
    indicatorHeight: Int = TabPagerDefaults.indicatorHeight(),
    tabPaddingHorizontal: Int = TabPagerDefaults.tabPaddingHorizontal(),
    tabPaddingVertical: Int = TabPagerDefaults.tabPaddingVertical(),
    selectedTextColor: Int = TabPagerDefaults.selectedTextColor(),
    unselectedTextColor: Int = TabPagerDefaults.unselectedTextColor(),
    rippleColor: Int = TabPagerDefaults.rippleColor(),
    modifier: Modifier = Modifier,
    pages: TabPagerScope.() -> Unit,
) {
    val builtPages = TabPagerScope().apply(pages).build()
    val localSnapshot = LocalContext.snapshot()
    val resolvedPages = builtPages.map { page ->
        TabPage(
            title = page.title,
            item = LazyListItem(
                key = page.key,
                contentToken = page.contentToken,
                sessionFactory = LazyListItemSessionFactory { container ->
                    WidgetLazyListItemSession(
                        container = container,
                        localSnapshot = localSnapshot,
                        content = page.content,
                    )
                },
                sessionUpdater = { session ->
                    (session as? WidgetLazyListItemSession)?.updateContent(
                        localSnapshot = localSnapshot,
                        content = page.content,
                    )
                },
            ),
        )
    }
    emit(
        type = NodeType.TabPager,
        key = key,
        spec = TabPagerNodeProps(
            pages = resolvedPages,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            backgroundColor = backgroundColor,
            indicatorColor = indicatorColor,
            cornerRadius = cornerRadius,
            indicatorHeight = indicatorHeight,
            tabPaddingHorizontal = tabPaddingHorizontal,
            tabPaddingVertical = tabPaddingVertical,
            selectedTextColor = selectedTextColor,
            unselectedTextColor = unselectedTextColor,
            rippleColor = rippleColor,
        ),
        modifier = modifier,
    )
}

private class WidgetLazyListItemSession(
    container: ViewGroup,
    localSnapshot: LocalSnapshot,
    content: UiTreeBuilder.() -> Unit,
) : LazyListItemSession {
    private var capturedLocals = localSnapshot
    private var renderContent = content
    private val session = RenderSession(
        container = container,
        content = {
            LocalContext.withSnapshot(capturedLocals) {
                renderContent()
            }
        },
    )

    override fun render() {
        session.render()
    }

    override fun dispose() {
        session.dispose()
    }

    fun updateContent(
        localSnapshot: LocalSnapshot,
        content: UiTreeBuilder.() -> Unit,
    ) {
        capturedLocals = localSnapshot
        renderContent = content
    }
}

internal data class TabPagerPage(
    val title: String,
    val key: Any?,
    val contentToken: Any?,
    val content: UiTreeBuilder.() -> Unit,
)
