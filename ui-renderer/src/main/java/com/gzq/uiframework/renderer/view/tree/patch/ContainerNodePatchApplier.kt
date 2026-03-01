package com.gzq.uiframework.renderer.view.tree.patch

import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.tree.ContainerViewBinder
import com.gzq.uiframework.renderer.view.tree.LazyColumnNodePatch
import com.gzq.uiframework.renderer.view.tree.SegmentedControlNodePatch
import com.gzq.uiframework.renderer.view.tree.TabPagerNodePatch

internal object ContainerNodePatchApplier {
    fun applyLazyColumnPatch(
        view: RecyclerView,
        patch: LazyColumnNodePatch,
    ) {
        ContainerViewBinder.bindLazyColumn(
            view = view,
            spec = ContainerViewBinder.LazyColumnSpec(
                contentPadding = patch.next.contentPadding,
                spacing = patch.next.spacing,
                items = patch.next.items,
            ),
        )
    }

    fun applyTabPagerPatch(
        view: DeclarativeTabPagerLayout,
        patch: TabPagerNodePatch,
    ) {
        ContainerViewBinder.bindTabPager(
            view = view,
            spec = ContainerViewBinder.TabPagerSpec(
                pages = patch.next.pages,
                selectedTabIndex = patch.next.selectedTabIndex,
                onTabSelected = patch.next.onTabSelected,
                backgroundColor = patch.next.backgroundColor,
                indicatorColor = patch.next.indicatorColor,
                cornerRadius = patch.next.cornerRadius,
                indicatorHeight = patch.next.indicatorHeight,
                tabPaddingHorizontal = patch.next.tabPaddingHorizontal,
                tabPaddingVertical = patch.next.tabPaddingVertical,
                selectedTextColor = patch.next.selectedTextColor,
                unselectedTextColor = patch.next.unselectedTextColor,
                rippleColor = patch.next.rippleColor,
            ),
        )
    }

    fun applySegmentedControlPatch(
        view: DeclarativeSegmentedControlLayout,
        patch: SegmentedControlNodePatch,
    ) {
        ContainerViewBinder.bindSegmentedControl(
            view = view,
            spec = ContainerViewBinder.SegmentedControlSpec(
                items = patch.next.items,
                selectedIndex = patch.next.selectedIndex,
                onSelectionChange = patch.next.onSelectionChange,
                enabled = patch.next.enabled,
                backgroundColor = patch.next.backgroundColor,
                indicatorColor = patch.next.indicatorColor,
                cornerRadius = patch.next.cornerRadius,
                textColor = patch.next.textColor,
                selectedTextColor = patch.next.selectedTextColor,
                rippleColor = patch.next.rippleColor,
                textSizeSp = patch.next.textSizeSp,
                horizontalPadding = patch.next.horizontalPadding,
                verticalPadding = patch.next.verticalPadding,
            ),
        )
    }
}
