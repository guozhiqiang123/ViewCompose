package com.gzq.uiframework.renderer.view.tree

import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter
import com.gzq.uiframework.renderer.view.lazy.LazyItemSpacingDecoration
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import android.view.Gravity

internal object ContainerViewBinder {
    data class LinearSpec(
        val spacing: Int,
        val arrangement: MainAxisArrangement,
        val gravity: Int,
    )

    data class BoxSpec(
        val gravity: Int,
    )

    data class LazyColumnSpec(
        val contentPadding: Int,
        val spacing: Int,
        val items: List<LazyListItem>,
    )

    data class TabPagerSpec(
        val pages: List<TabPage>,
        val selectedTabIndex: Int,
        val onTabSelected: ((Int) -> Unit)?,
        val backgroundColor: Int,
        val indicatorColor: Int,
        val cornerRadius: Int,
        val indicatorHeight: Int,
        val tabPaddingHorizontal: Int,
        val tabPaddingVertical: Int,
        val selectedTextColor: Int,
        val unselectedTextColor: Int,
        val rippleColor: Int,
    )

    data class SegmentedControlSpec(
        val items: List<SegmentedControlItem>,
        val selectedIndex: Int,
        val onSelectionChange: ((Int) -> Unit)?,
        val enabled: Boolean,
        val backgroundColor: Int,
        val indicatorColor: Int,
        val cornerRadius: Int,
        val textColor: Int,
        val selectedTextColor: Int,
        val rippleColor: Int,
        val textSizeSp: Int,
        val paddingHorizontal: Int,
        val paddingVertical: Int,
    )

    data class DividerSpec(
        val color: Int,
        val thickness: Int,
    )

    data class AndroidViewSpec(
        val update: ((android.view.View) -> Unit)?,
    )

    fun bindRow(
        view: DeclarativeLinearLayout,
        spec: LinearSpec,
    ) {
        view.orientation = LinearLayout.HORIZONTAL
        view.itemSpacing = spec.spacing
        view.mainAxisArrangement = spec.arrangement
        view.gravity = spec.gravity
    }

    fun bindColumn(
        view: DeclarativeLinearLayout,
        spec: LinearSpec,
    ) {
        view.orientation = LinearLayout.VERTICAL
        view.itemSpacing = spec.spacing
        view.mainAxisArrangement = spec.arrangement
        view.gravity = spec.gravity
    }

    fun bindBox(
        view: DeclarativeBoxLayout,
        spec: BoxSpec,
    ) {
        view.contentGravity = spec.gravity
    }

    fun bindLazyColumn(
        view: RecyclerView,
        spec: LazyColumnSpec,
    ) {
        val adapter = view.adapter as? LazyColumnAdapter ?: LazyColumnAdapter().also {
            view.adapter = it
        }
        applyLazyListPadding(view, spec.contentPadding)
        applyLazyListSpacing(view, spec.spacing)
        adapter.submitItems(spec.items)
    }

    fun bindTabPager(
        view: DeclarativeTabPagerLayout,
        spec: TabPagerSpec,
    ) {
        view.bind(
            pages = spec.pages,
            selectedTabIndex = spec.selectedTabIndex,
            onTabSelected = spec.onTabSelected,
            backgroundColor = spec.backgroundColor,
            indicatorColor = spec.indicatorColor,
            cornerRadius = spec.cornerRadius,
            indicatorHeight = spec.indicatorHeight,
            tabPaddingHorizontal = spec.tabPaddingHorizontal,
            tabPaddingVertical = spec.tabPaddingVertical,
            selectedTextColor = spec.selectedTextColor,
            unselectedTextColor = spec.unselectedTextColor,
            rippleColor = spec.rippleColor,
        )
    }

    fun bindSegmentedControl(
        view: DeclarativeSegmentedControlLayout,
        spec: SegmentedControlSpec,
    ) {
        view.bind(
            items = spec.items,
            selectedIndex = spec.selectedIndex,
            onSelectionChange = spec.onSelectionChange,
            enabled = spec.enabled,
            backgroundColor = spec.backgroundColor,
            indicatorColor = spec.indicatorColor,
            cornerRadius = spec.cornerRadius,
            textColor = spec.textColor,
            selectedTextColor = spec.selectedTextColor,
            rippleColor = spec.rippleColor,
            textSizeSp = spec.textSizeSp,
            paddingHorizontal = spec.paddingHorizontal,
            paddingVertical = spec.paddingVertical,
        )
    }

    fun readRowSpec(node: VNode): LinearSpec {
        val spec = node.spec as? RowNodeProps
        if (spec != null) {
            return LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = spec.verticalAlignment.toGravity(),
            )
        }
        return LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.RowMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = (node.props[TypedPropKeys.RowVerticalAlignment] ?: VerticalAlignment.Top).toGravity(),
        )
    }

    fun readColumnSpec(node: VNode): LinearSpec {
        val spec = node.spec as? ColumnNodeProps
        if (spec != null) {
            return LinearSpec(
                spacing = spec.spacing,
                arrangement = spec.arrangement,
                gravity = spec.horizontalAlignment.toGravity(),
            )
        }
        return LinearSpec(
            spacing = node.props[TypedPropKeys.LinearSpacing] ?: 0,
            arrangement = node.props[TypedPropKeys.ColumnMainAxisArrangement] ?: MainAxisArrangement.Start,
            gravity = (node.props[TypedPropKeys.ColumnHorizontalAlignment] ?: HorizontalAlignment.Start).toGravity(),
        )
    }

    fun readBoxSpec(node: VNode): BoxSpec {
        val spec = node.spec as? BoxNodeProps
        if (spec != null) {
            return BoxSpec(
                gravity = spec.contentAlignment.toGravity(),
            )
        }
        return BoxSpec(
            gravity = (node.props[TypedPropKeys.BoxAlignment] ?: BoxAlignment.TopStart).toGravity(),
        )
    }

    fun readLazyColumnSpec(node: VNode): LazyColumnSpec {
        val spec = node.spec as? LazyColumnNodeProps
        if (spec != null) {
            return LazyColumnSpec(
                contentPadding = spec.contentPadding,
                spacing = spec.spacing,
                items = spec.items,
            )
        }
        return LazyColumnSpec(
            contentPadding = node.props[TypedPropKeys.LazyContentPadding] ?: 0,
            spacing = node.props[TypedPropKeys.LazySpacing] ?: 0,
            items = node.props[TypedPropKeys.LazyItems] ?: emptyList(),
        )
    }

    fun readTabPagerSpec(node: VNode, defaultRippleColor: Int): TabPagerSpec {
        val spec = node.spec as? TabPagerNodeProps
        if (spec != null) {
            return TabPagerSpec(
                pages = spec.pages,
                selectedTabIndex = spec.selectedTabIndex,
                onTabSelected = spec.onTabSelected,
                backgroundColor = spec.backgroundColor,
                indicatorColor = spec.indicatorColor,
                cornerRadius = spec.cornerRadius,
                indicatorHeight = spec.indicatorHeight,
                tabPaddingHorizontal = spec.tabPaddingHorizontal,
                tabPaddingVertical = spec.tabPaddingVertical,
                selectedTextColor = spec.selectedTextColor,
                unselectedTextColor = spec.unselectedTextColor,
                rippleColor = spec.rippleColor,
            )
        }
        return TabPagerSpec(
            pages = node.props[TypedPropKeys.TabPages] ?: emptyList(),
            selectedTabIndex = node.props[TypedPropKeys.SelectedTabIndex] ?: 0,
            onTabSelected = node.props[TypedPropKeys.OnTabSelected],
            backgroundColor = node.props[TypedPropKeys.TabBackgroundColor] ?: 0,
            indicatorColor = node.props[TypedPropKeys.TabIndicatorColor] ?: 0,
            cornerRadius = node.props[TypedPropKeys.TabCornerRadius] ?: 0,
            indicatorHeight = node.props[TypedPropKeys.TabIndicatorHeight] ?: 0,
            tabPaddingHorizontal = node.props[TypedPropKeys.TabContentPaddingHorizontal] ?: 0,
            tabPaddingVertical = node.props[TypedPropKeys.TabContentPaddingVertical] ?: 0,
            selectedTextColor = node.props[TypedPropKeys.TabSelectedTextColor] ?: 0,
            unselectedTextColor = node.props[TypedPropKeys.TabUnselectedTextColor] ?: 0,
            rippleColor = node.props[TypedPropKeys.TabRippleColor] ?: defaultRippleColor,
        )
    }

    fun readSegmentedControlSpec(node: VNode, defaultRippleColor: Int): SegmentedControlSpec {
        val spec = node.spec as? SegmentedControlNodeProps
        if (spec != null) {
            return SegmentedControlSpec(
                items = spec.items,
                selectedIndex = spec.selectedIndex,
                onSelectionChange = spec.onSelectionChange,
                enabled = spec.enabled,
                backgroundColor = spec.backgroundColor,
                indicatorColor = spec.indicatorColor,
                cornerRadius = spec.cornerRadius,
                textColor = spec.textColor,
                selectedTextColor = spec.selectedTextColor,
                rippleColor = spec.rippleColor,
                textSizeSp = spec.textSizeSp,
                paddingHorizontal = spec.paddingHorizontal,
                paddingVertical = spec.paddingVertical,
            )
        }
        return SegmentedControlSpec(
            items = node.props[TypedPropKeys.SegmentItems] ?: emptyList(),
            selectedIndex = node.props[TypedPropKeys.SegmentSelectedIndex] ?: 0,
            onSelectionChange = node.props[TypedPropKeys.OnSegmentSelected],
            enabled = node.props[TypedPropKeys.Enabled] ?: true,
            backgroundColor = node.props[TypedPropKeys.SegmentBackgroundColor] ?: android.graphics.Color.TRANSPARENT,
            indicatorColor = node.props[TypedPropKeys.SegmentIndicatorColor] ?: android.graphics.Color.TRANSPARENT,
            cornerRadius = node.props[TypedPropKeys.SegmentCornerRadius] ?: 0,
            textColor = node.props[TypedPropKeys.SegmentTextColor] ?: android.graphics.Color.BLACK,
            selectedTextColor = node.props[TypedPropKeys.SegmentSelectedTextColor] ?: android.graphics.Color.WHITE,
            rippleColor = node.props[TypedPropKeys.SegmentRippleColor] ?: defaultRippleColor,
            textSizeSp = node.props[TypedPropKeys.SegmentTextSizeSp] ?: 14,
            paddingHorizontal = node.props[TypedPropKeys.SegmentContentPaddingHorizontal] ?: 0,
            paddingVertical = node.props[TypedPropKeys.SegmentContentPaddingVertical] ?: 0,
        )
    }

    fun readDividerSpec(node: VNode): DividerSpec {
        val spec = node.spec as? DividerNodeProps
        if (spec != null) {
            return DividerSpec(
                color = spec.color,
                thickness = spec.thickness,
            )
        }
        return DividerSpec(
            color = node.props[TypedPropKeys.DividerColor] ?: android.graphics.Color.BLACK,
            thickness = node.props[TypedPropKeys.DividerThickness] ?: 1,
        )
    }

    fun readAndroidViewSpec(node: VNode): AndroidViewSpec {
        val spec = node.spec as? AndroidViewNodeProps
        if (spec != null) {
            return AndroidViewSpec(update = spec.update)
        }
        return AndroidViewSpec(
            update = node.props[TypedPropKeys.ViewUpdate],
        )
    }

    internal fun applyLazyListPadding(
        recyclerView: RecyclerView,
        padding: Int,
    ) {
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = padding == 0
    }

    internal fun applyLazyListSpacing(
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

    internal fun VerticalAlignment.toGravity(): Int {
        return when (this) {
            VerticalAlignment.Top -> Gravity.TOP
            VerticalAlignment.Center -> Gravity.CENTER_VERTICAL
            VerticalAlignment.Bottom -> Gravity.BOTTOM
        }
    }

    internal fun HorizontalAlignment.toGravity(): Int {
        return when (this) {
            HorizontalAlignment.Start -> Gravity.START
            HorizontalAlignment.Center -> Gravity.CENTER_HORIZONTAL
            HorizontalAlignment.End -> Gravity.END
        }
    }

    internal fun BoxAlignment.toGravity(): Int {
        return when (this) {
            BoxAlignment.TopStart -> Gravity.TOP or Gravity.START
            BoxAlignment.TopCenter -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            BoxAlignment.TopEnd -> Gravity.TOP or Gravity.END
            BoxAlignment.CenterStart -> Gravity.CENTER_VERTICAL or Gravity.START
            BoxAlignment.Center -> Gravity.CENTER
            BoxAlignment.CenterEnd -> Gravity.CENTER_VERTICAL or Gravity.END
            BoxAlignment.BottomStart -> Gravity.BOTTOM or Gravity.START
            BoxAlignment.BottomCenter -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            BoxAlignment.BottomEnd -> Gravity.BOTTOM or Gravity.END
        }
    }
}
