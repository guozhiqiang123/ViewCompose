package com.gzq.uiframework.renderer.view.tree

import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter
import com.gzq.uiframework.renderer.view.lazy.LazyItemSpacingDecoration
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage

internal object ContainerViewBinder {
    fun bindRow(
        view: DeclarativeLinearLayout,
        spacing: Int,
        arrangement: MainAxisArrangement,
        gravity: Int,
    ) {
        view.orientation = LinearLayout.HORIZONTAL
        view.itemSpacing = spacing
        view.mainAxisArrangement = arrangement
        view.gravity = gravity
    }

    fun bindColumn(
        view: DeclarativeLinearLayout,
        spacing: Int,
        arrangement: MainAxisArrangement,
        gravity: Int,
    ) {
        view.orientation = LinearLayout.VERTICAL
        view.itemSpacing = spacing
        view.mainAxisArrangement = arrangement
        view.gravity = gravity
    }

    fun bindBox(
        view: DeclarativeBoxLayout,
        gravity: Int,
    ) {
        view.contentGravity = gravity
    }

    fun bindLazyColumn(
        view: RecyclerView,
        contentPadding: Int,
        spacing: Int,
        items: List<com.gzq.uiframework.renderer.node.LazyListItem>,
    ) {
        val adapter = view.adapter as? LazyColumnAdapter ?: LazyColumnAdapter().also {
            view.adapter = it
        }
        applyLazyListPadding(view, contentPadding)
        applyLazyListSpacing(view, spacing)
        adapter.submitItems(items)
    }

    fun bindTabPager(
        view: DeclarativeTabPagerLayout,
        pages: List<TabPage>,
        selectedTabIndex: Int,
        onTabSelected: ((Int) -> Unit)?,
        backgroundColor: Int,
        indicatorColor: Int,
        cornerRadius: Int,
        indicatorHeight: Int,
        tabPaddingHorizontal: Int,
        tabPaddingVertical: Int,
        selectedTextColor: Int,
        unselectedTextColor: Int,
        rippleColor: Int,
    ) {
        view.bind(
            pages = pages,
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
        )
    }

    fun bindSegmentedControl(
        view: DeclarativeSegmentedControlLayout,
        items: List<SegmentedControlItem>,
        selectedIndex: Int,
        onSelectionChange: ((Int) -> Unit)?,
        enabled: Boolean,
        backgroundColor: Int,
        indicatorColor: Int,
        cornerRadius: Int,
        textColor: Int,
        selectedTextColor: Int,
        rippleColor: Int,
        textSizeSp: Int,
        horizontalPadding: Int,
        verticalPadding: Int,
    ) {
        view.bind(
            items = items,
            selectedIndex = selectedIndex,
            onSelectionChange = onSelectionChange,
            enabled = enabled,
            backgroundColor = backgroundColor,
            indicatorColor = indicatorColor,
            cornerRadius = cornerRadius,
            textColor = textColor,
            selectedTextColor = selectedTextColor,
            rippleColor = rippleColor,
            textSizeSp = textSizeSp,
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
        )
    }

    private fun applyLazyListPadding(
        recyclerView: RecyclerView,
        padding: Int,
    ) {
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = padding == 0
    }

    private fun applyLazyListSpacing(
        recyclerView: RecyclerView,
        spacing: Int,
    ) {
        val existing = recyclerView.getTag(R.id.ui_framework_lazy_spacing_decoration) as? LazyItemSpacingDecoration
        if (existing != null) {
            existing.updateSpacing(spacing)
            recyclerView.invalidateItemDecorations()
            return
        }
        val decoration = LazyItemSpacingDecoration(spacing)
        recyclerView.setTag(R.id.ui_framework_lazy_spacing_decoration, decoration)
        recyclerView.addItemDecoration(decoration)
    }
}
