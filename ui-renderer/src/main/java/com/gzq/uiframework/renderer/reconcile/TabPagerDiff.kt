package com.gzq.uiframework.renderer.reconcile

import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.TabPage

sealed interface TabPagerUpdate {
    data class Insert(
        val index: Int,
        val page: TabPage,
    ) : TabPagerUpdate

    data class Remove(
        val index: Int,
    ) : TabPagerUpdate

    data class Move(
        val fromIndex: Int,
        val toIndex: Int,
    ) : TabPagerUpdate

    data class Change(
        val index: Int,
        val page: TabPage,
    ) : TabPagerUpdate

    data object ReloadAll : TabPagerUpdate
}

data class TabPagerDiffResult(
    val updates: List<TabPagerUpdate>,
    val pages: List<TabPage>,
)

object TabPagerDiff {
    fun calculate(
        previous: List<TabPage>,
        next: List<TabPage>,
    ): TabPagerDiffResult {
        val lazyDiff = LazyListDiff.calculate(
            previous = previous.map(TabPage::item),
            next = next.map(TabPage::item),
        )
        if (lazyDiff.updates == listOf(LazyListUpdate.ReloadAll)) {
            return TabPagerDiffResult(
                updates = listOf(TabPagerUpdate.ReloadAll),
                pages = next,
            )
        }

        val updates = lazyDiff.updates.map { update ->
            when (update) {
                is LazyListUpdate.Insert -> TabPagerUpdate.Insert(
                    index = update.index,
                    page = next[update.index],
                )
                is LazyListUpdate.Remove -> TabPagerUpdate.Remove(update.index)
                is LazyListUpdate.Move -> TabPagerUpdate.Move(update.fromIndex, update.toIndex)
                is LazyListUpdate.Change -> TabPagerUpdate.Change(
                    index = update.index,
                    page = next[update.index],
                )
                LazyListUpdate.ReloadAll -> TabPagerUpdate.ReloadAll
            }
        }.toMutableList()

        val previousByKey = previous.associateBy { it.item.key }
        next.forEachIndexed { index, page ->
            if (previousByKey[page.item.key]?.title != null &&
                previousByKey[page.item.key]?.title != page.title &&
                updates.none { it is TabPagerUpdate.Change && it.index == index }
            ) {
                updates += TabPagerUpdate.Change(index, page)
            }
        }

        return TabPagerDiffResult(
            updates = updates,
            pages = next,
        )
    }
}

object TabPagerSelectionResolver {
    fun resolve(
        pages: List<TabPage>,
        selectedIndex: Int,
    ): Int? {
        if (pages.isEmpty()) {
            return null
        }
        return selectedIndex.coerceIn(0, pages.lastIndex)
    }
}
