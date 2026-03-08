package com.viewcompose.widget.core

import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.MainAxisArrangement
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.node.VNode
import com.viewcompose.ui.state.LazyListState
import com.viewcompose.ui.state.PagerState

@UiDslMarker
class ScrollableScope internal constructor() {
    private val delegate = UiTreeBuilder()

    // ── 纵向滚动 ──

    fun <T> LazyColumn(
        items: List<T>,
        key: ((T) -> Any)? = null,
        contentPadding: Int = 0,
        spacing: Int = 0,
        state: LazyListState? = null,
        modifier: Modifier = Modifier,
        itemContent: UiTreeBuilder.(T) -> Unit,
    ) {
        with(delegate) { LazyColumn(items, key, contentPadding, spacing, state, modifier, itemContent) }
    }

    fun ScrollableColumn(
        key: Any? = null,
        spacing: Int = 0,
        arrangement: MainAxisArrangement = MainAxisArrangement.Start,
        horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
        modifier: Modifier = Modifier,
        content: ColumnScope.() -> Unit,
    ) {
        with(delegate) { ScrollableColumn(key, spacing, arrangement, horizontalAlignment, modifier, content) }
    }

    // ── 横向滚动 ──

    fun <T> LazyRow(
        items: List<T>,
        key: ((T) -> Any)? = null,
        contentPadding: Int = 0,
        spacing: Int = 0,
        state: LazyListState? = null,
        modifier: Modifier = Modifier,
        itemContent: UiTreeBuilder.(T) -> Unit,
    ) {
        with(delegate) { LazyRow(items, key, contentPadding, spacing, state, modifier, itemContent) }
    }

    fun ScrollableRow(
        key: Any? = null,
        spacing: Int = 0,
        arrangement: MainAxisArrangement = MainAxisArrangement.Start,
        verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
        modifier: Modifier = Modifier,
        content: RowScope.() -> Unit,
    ) {
        with(delegate) { ScrollableRow(key, spacing, arrangement, verticalAlignment, modifier, content) }
    }

    // ── 网格 ──

    fun <T> LazyVerticalGrid(
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
        with(delegate) {
            LazyVerticalGrid(items, spanCount, key, contentPadding, horizontalSpacing, verticalSpacing, state, modifier, itemContent)
        }
    }

    // ── 翻页 ──

    fun HorizontalPager(
        currentPage: Int,
        onPageChanged: (Int) -> Unit,
        pagerState: PagerState? = null,
        offscreenPageLimit: Int = 1,
        userScrollEnabled: Boolean = true,
        key: Any? = null,
        modifier: Modifier = Modifier,
        pages: HorizontalPagerScope.() -> Unit,
    ) {
        with(delegate) {
            HorizontalPager(currentPage, onPageChanged, pagerState, offscreenPageLimit, userScrollEnabled, key, modifier, pages)
        }
    }

    fun VerticalPager(
        currentPage: Int,
        onPageChanged: (Int) -> Unit,
        pagerState: PagerState? = null,
        offscreenPageLimit: Int = 1,
        userScrollEnabled: Boolean = true,
        key: Any? = null,
        modifier: Modifier = Modifier,
        pages: HorizontalPagerScope.() -> Unit,
    ) {
        with(delegate) {
            VerticalPager(currentPage, onPageChanged, pagerState, offscreenPageLimit, userScrollEnabled, key, modifier, pages)
        }
    }

    internal fun build(): List<VNode> = delegate.build()
}
