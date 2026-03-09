package com.viewcompose.widget.core

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.node.LazyListItem
import com.viewcompose.ui.node.LazyListItemSessionFactory
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.collection.TabIndicatorPosition
import com.viewcompose.ui.node.collection.TabIndicatorWidthMode
import com.viewcompose.ui.node.collection.TabRowTab
import com.viewcompose.ui.node.spec.HorizontalPagerNodeProps
import com.viewcompose.ui.node.spec.LazyColumnNodeProps
import com.viewcompose.ui.node.spec.LazyRowNodeProps
import com.viewcompose.ui.node.spec.LazyVerticalGridNodeProps
import com.viewcompose.ui.node.spec.TabRowNodeProps
import com.viewcompose.ui.node.spec.VerticalPagerNodeProps
import com.viewcompose.ui.state.LazyListState
import com.viewcompose.ui.state.PagerState

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

fun <T> UiTreeBuilder.LazyVerticalGrid(
    items: List<T>,
    spanCount: Int = 2,
    key: ((T) -> Any)? = null,
    contentPadding: Int = 0,
    horizontalSpacing: Int = 0,
    verticalSpacing: Int = 0,
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
        type = NodeType.LazyVerticalGrid,
        spec = LazyVerticalGridNodeProps(
            spanCount = spanCount,
            contentPadding = contentPadding,
            horizontalSpacing = horizontalSpacing,
            verticalSpacing = verticalSpacing,
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

// ─── VerticalPager ────────────────────────────────────────────────

fun UiTreeBuilder.VerticalPager(
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
        type = NodeType.VerticalPager,
        key = key,
        spec = VerticalPagerNodeProps(
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
