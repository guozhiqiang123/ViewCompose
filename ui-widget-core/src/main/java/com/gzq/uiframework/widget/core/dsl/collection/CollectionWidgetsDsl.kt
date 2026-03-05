package com.gzq.uiframework.widget.core

import android.view.ViewGroup
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.collection.TabIndicatorPosition
import com.gzq.uiframework.renderer.node.collection.TabIndicatorWidthMode
import com.gzq.uiframework.renderer.node.collection.TabRowTab
import com.gzq.uiframework.renderer.node.spec.HorizontalPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyRowNodeProps
import com.gzq.uiframework.renderer.node.spec.TabRowNodeProps
import com.gzq.uiframework.renderer.view.lazy.LazyListState
import com.gzq.uiframework.renderer.view.lazy.PagerState

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

fun <T> UiTreeBuilder.LazyRow(
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
        type = NodeType.LazyRow,
        spec = LazyRowNodeProps(
            contentPadding = contentPadding,
            spacing = spacing,
            items = resolvedItems,
            state = state,
        ),
        modifier = modifier,
    )
}

// ─── HorizontalPager ───────────────────────────────────────────────

@UiDslMarker
class HorizontalPagerScope internal constructor() {
    private val pages = mutableListOf<HorizontalPagerPage>()

    fun Page(
        key: Any? = null,
        contentToken: Any? = null,
        content: UiTreeBuilder.() -> Unit,
    ) {
        pages += HorizontalPagerPage(
            key = key,
            contentToken = contentToken,
            content = content,
        )
    }

    internal fun build(): List<HorizontalPagerPage> = pages.toList()
}

fun UiTreeBuilder.HorizontalPager(
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    pagerState: PagerState? = null,
    offscreenPageLimit: Int = 1,
    userScrollEnabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
    pages: HorizontalPagerScope.() -> Unit,
) {
    val builtPages = HorizontalPagerScope().apply(pages).build()
    val localSnapshot = LocalContext.snapshot()
    val resolvedPages = builtPages.map { page ->
        LazyListItem(
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
        )
    }
    emit(
        type = NodeType.HorizontalPager,
        key = key,
        spec = HorizontalPagerNodeProps(
            pages = resolvedPages,
            currentPage = currentPage,
            onPageChanged = onPageChanged,
            offscreenPageLimit = offscreenPageLimit,
            pagerState = pagerState,
            userScrollEnabled = userScrollEnabled,
        ),
        modifier = modifier,
    )
}

internal data class HorizontalPagerPage(
    val key: Any?,
    val contentToken: Any?,
    val content: UiTreeBuilder.() -> Unit,
)

// ─── TabRow ────────────────────────────────────────────────────────

@UiDslMarker
class TabRowScope internal constructor() {
    private val tabs = mutableListOf<TabRowTabEntry>()

    fun Tab(
        key: Any? = null,
        content: UiTreeBuilder.(selected: Boolean) -> Unit,
    ) {
        tabs += TabRowTabEntry(
            key = key ?: tabs.size,
            content = content,
        )
    }

    internal fun build(): List<TabRowTabEntry> = tabs.toList()
}

fun UiTreeBuilder.TabRow(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    pagerState: PagerState? = null,
    indicatorColor: Int = TabRowDefaults.indicatorColor(),
    indicatorHeight: Int = TabRowDefaults.indicatorHeight(),
    indicatorCornerRadius: Int = TabRowDefaults.indicatorCornerRadius(),
    indicatorPosition: TabIndicatorPosition = TabIndicatorPosition.Bottom,
    indicatorWidthMode: TabIndicatorWidthMode = TabIndicatorWidthMode.MatchItem,
    indicatorFixedWidth: Int = 0,
    containerColor: Int = TabRowDefaults.containerColor(),
    scrollable: Boolean = false,
    equalWidth: Boolean = true,
    rippleColor: Int = TabRowDefaults.rippleColor(),
    itemSpacing: Int = 0,
    itemPaddingHorizontal: Int = TabRowDefaults.itemPaddingHorizontal(),
    itemPaddingVertical: Int = TabRowDefaults.itemPaddingVertical(),
    minItemWidth: Int = TabRowDefaults.minItemWidth(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    tabs: TabRowScope.() -> Unit,
) {
    val builtTabs = TabRowScope().apply(tabs).build()
    val localSnapshot = LocalContext.snapshot()
    val resolvedTabs = builtTabs.mapIndexed { index, entry ->
        val selected = index == selectedIndex
        TabRowTab(
            item = LazyListItem(
                key = entry.key,
                contentToken = Pair(entry.key, selected),
                sessionFactory = LazyListItemSessionFactory { container ->
                    WidgetLazyListItemSession(
                        container = container,
                        localSnapshot = localSnapshot,
                        content = { entry.content(this, selected) },
                    )
                },
                sessionUpdater = { session ->
                    (session as? WidgetLazyListItemSession)?.updateContent(
                        localSnapshot = localSnapshot,
                        content = { entry.content(this, selected) },
                    )
                },
            ),
        )
    }
    emit(
        type = NodeType.TabRow,
        key = key,
        spec = TabRowNodeProps(
            tabs = resolvedTabs,
            selectedIndex = selectedIndex,
            onTabSelected = onTabSelected,
            pagerState = pagerState,
            indicatorColor = indicatorColor,
            indicatorHeight = indicatorHeight,
            indicatorCornerRadius = indicatorCornerRadius,
            indicatorPosition = indicatorPosition,
            indicatorWidthMode = indicatorWidthMode,
            indicatorFixedWidth = indicatorFixedWidth,
            containerColor = containerColor,
            scrollable = scrollable,
            equalWidth = equalWidth,
            rippleColor = rippleColor,
            itemSpacing = itemSpacing,
            itemPaddingHorizontal = itemPaddingHorizontal,
            itemPaddingVertical = itemPaddingVertical,
            minItemWidth = minItemWidth,
        ),
        modifier = modifier,
    )
}

internal data class TabRowTabEntry(
    val key: Any,
    val content: UiTreeBuilder.(selected: Boolean) -> Unit,
)

// ─── TabPager (composite: TabRow + HorizontalPager) ────────────────

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
    pagerState: PagerState? = null,
    key: Any? = null,
    backgroundColor: Int = TabPagerDefaults.backgroundColor(),
    indicatorColor: Int = TabPagerDefaults.indicatorColor(),
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
    Column(key = key, modifier = modifier) {
        TabRow(
            selectedIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            pagerState = pagerState,
            containerColor = backgroundColor,
            indicatorColor = indicatorColor,
            indicatorHeight = indicatorHeight,
            rippleColor = rippleColor,
            itemPaddingHorizontal = tabPaddingHorizontal,
            itemPaddingVertical = tabPaddingVertical,
        ) {
            builtPages.forEach { page ->
                Tab(key = page.key) { selected ->
                    Text(
                        text = page.title,
                        color = if (selected) selectedTextColor else unselectedTextColor,
                    )
                }
            }
        }
        HorizontalPager(
            currentPage = selectedTabIndex,
            onPageChanged = onTabSelected,
            pagerState = pagerState,
            offscreenPageLimit = builtPages.size.coerceAtLeast(1),
            modifier = Modifier.weight(1f),
        ) {
            builtPages.forEach { page ->
                Page(
                    key = page.key,
                    contentToken = page.contentToken,
                    content = page.content,
                )
            }
        }
    }
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
