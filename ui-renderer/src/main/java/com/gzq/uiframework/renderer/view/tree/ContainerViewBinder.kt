package com.gzq.uiframework.renderer.view.tree

import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter
import com.gzq.uiframework.renderer.view.lazy.LazyItemSpacingDecoration
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.VNode
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
        val horizontalPadding: Int,
        val verticalPadding: Int,
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
            horizontalPadding = spec.horizontalPadding,
            verticalPadding = spec.verticalPadding,
        )
    }

    fun readRowSpec(node: VNode): LinearSpec {
        return LinearSpec(
            spacing = node.props.values[PropKeys.LINEAR_SPACING] as? Int ?: 0,
            arrangement = node.props.values[PropKeys.ROW_MAIN_AXIS_ARRANGEMENT] as? MainAxisArrangement
                ?: MainAxisArrangement.Start,
            gravity = (node.props.values[PropKeys.ROW_VERTICAL_ALIGNMENT] as? VerticalAlignment
                ?: VerticalAlignment.Top).toGravity(),
        )
    }

    fun readColumnSpec(node: VNode): LinearSpec {
        return LinearSpec(
            spacing = node.props.values[PropKeys.LINEAR_SPACING] as? Int ?: 0,
            arrangement = node.props.values[PropKeys.COLUMN_MAIN_AXIS_ARRANGEMENT] as? MainAxisArrangement
                ?: MainAxisArrangement.Start,
            gravity = (node.props.values[PropKeys.COLUMN_HORIZONTAL_ALIGNMENT] as? HorizontalAlignment
                ?: HorizontalAlignment.Start).toGravity(),
        )
    }

    fun readBoxSpec(node: VNode): BoxSpec {
        return BoxSpec(
            gravity = (node.props.values[PropKeys.BOX_ALIGNMENT] as? BoxAlignment
                ?: BoxAlignment.TopStart).toGravity(),
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun readLazyColumnSpec(node: VNode): LazyColumnSpec {
        return LazyColumnSpec(
            contentPadding = node.props.values[PropKeys.LAZY_CONTENT_PADDING] as? Int ?: 0,
            spacing = node.props.values[PropKeys.LAZY_SPACING] as? Int ?: 0,
            items = node.props.values[PropKeys.LAZY_ITEMS] as? List<LazyListItem> ?: emptyList(),
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun readTabPagerSpec(node: VNode, defaultRippleColor: Int): TabPagerSpec {
        return TabPagerSpec(
            pages = node.props.values[PropKeys.TAB_PAGES] as? List<TabPage> ?: emptyList(),
            selectedTabIndex = node.props.values[PropKeys.SELECTED_TAB_INDEX] as? Int ?: 0,
            onTabSelected = node.props.values[PropKeys.ON_TAB_SELECTED] as? ((Int) -> Unit),
            backgroundColor = node.props.values[PropKeys.TAB_BACKGROUND_COLOR] as? Int ?: 0,
            indicatorColor = node.props.values[PropKeys.TAB_INDICATOR_COLOR] as? Int ?: 0,
            cornerRadius = node.props.values[PropKeys.TAB_CORNER_RADIUS] as? Int ?: 0,
            indicatorHeight = node.props.values[PropKeys.TAB_INDICATOR_HEIGHT] as? Int ?: 0,
            tabPaddingHorizontal = node.props.values[PropKeys.TAB_CONTENT_PADDING_HORIZONTAL] as? Int ?: 0,
            tabPaddingVertical = node.props.values[PropKeys.TAB_CONTENT_PADDING_VERTICAL] as? Int ?: 0,
            selectedTextColor = node.props.values[PropKeys.TAB_SELECTED_TEXT_COLOR] as? Int ?: 0,
            unselectedTextColor = node.props.values[PropKeys.TAB_UNSELECTED_TEXT_COLOR] as? Int ?: 0,
            rippleColor = node.props.values[PropKeys.TAB_RIPPLE_COLOR] as? Int ?: defaultRippleColor,
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun readSegmentedControlSpec(node: VNode, defaultRippleColor: Int): SegmentedControlSpec {
        return SegmentedControlSpec(
            items = node.props.values[PropKeys.SEGMENT_ITEMS] as? List<SegmentedControlItem> ?: emptyList(),
            selectedIndex = node.props.values[PropKeys.SEGMENT_SELECTED_INDEX] as? Int ?: 0,
            onSelectionChange = node.props.values[PropKeys.ON_SEGMENT_SELECTED] as? ((Int) -> Unit),
            enabled = node.props.values[PropKeys.ENABLED] as? Boolean ?: true,
            backgroundColor = node.props.values[PropKeys.SEGMENT_BACKGROUND_COLOR] as? Int ?: android.graphics.Color.TRANSPARENT,
            indicatorColor = node.props.values[PropKeys.SEGMENT_INDICATOR_COLOR] as? Int ?: android.graphics.Color.TRANSPARENT,
            cornerRadius = node.props.values[PropKeys.SEGMENT_CORNER_RADIUS] as? Int ?: 0,
            textColor = node.props.values[PropKeys.SEGMENT_TEXT_COLOR] as? Int ?: android.graphics.Color.BLACK,
            selectedTextColor = node.props.values[PropKeys.SEGMENT_SELECTED_TEXT_COLOR] as? Int ?: android.graphics.Color.WHITE,
            rippleColor = node.props.values[PropKeys.SEGMENT_RIPPLE_COLOR] as? Int ?: defaultRippleColor,
            textSizeSp = node.props.values[PropKeys.SEGMENT_TEXT_SIZE_SP] as? Int ?: 14,
            horizontalPadding = node.props.values[PropKeys.SEGMENT_CONTENT_PADDING_HORIZONTAL] as? Int ?: 0,
            verticalPadding = node.props.values[PropKeys.SEGMENT_CONTENT_PADDING_VERTICAL] as? Int ?: 0,
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

    private fun VerticalAlignment.toGravity(): Int {
        return when (this) {
            VerticalAlignment.Top -> Gravity.TOP
            VerticalAlignment.Center -> Gravity.CENTER_VERTICAL
            VerticalAlignment.Bottom -> Gravity.BOTTOM
        }
    }

    private fun HorizontalAlignment.toGravity(): Int {
        return when (this) {
            HorizontalAlignment.Start -> Gravity.START
            HorizontalAlignment.Center -> Gravity.CENTER_HORIZONTAL
            HorizontalAlignment.End -> Gravity.END
        }
    }

    private fun BoxAlignment.toGravity(): Int {
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
