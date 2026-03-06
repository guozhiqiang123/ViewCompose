package com.gzq.uiframework.renderer.view.tree

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeFlowRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeNavigationBarLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableColumnLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeScrollableRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabRowLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeVerticalPagerLayout
import com.gzq.uiframework.renderer.view.tree.patch.ContainerNodePatchApplier
import com.gzq.uiframework.renderer.view.tree.patch.ContentNodePatchApplier
import com.gzq.uiframework.renderer.view.tree.patch.FeedbackNodePatchApplier
import com.gzq.uiframework.renderer.view.tree.patch.InputNodePatchApplier
import com.gzq.uiframework.renderer.view.tree.patch.MediaNodePatchApplier

internal object NodeViewBinderRegistry {
    private lateinit var binders: Map<NodeType, (View, VNode) -> Unit>

    fun initialize(
        defaultRippleColor: Int,
    ) {
        if (::binders.isInitialized) {
            return
        }
        binders = mapOf(
            NodeType.Text to { view, node ->
                ContentViewBinder.bindText(
                    view = view as TextView,
                    spec = ContentViewBinder.readTextSpec(node),
                )
            },
            NodeType.TextField to { view, node ->
                InputViewBinder.bindTextField(
                    view = view as DeclarativeTextFieldLayout,
                    spec = InputViewBinder.readTextFieldSpec(node),
                )
            },
            NodeType.Checkbox to { view, node ->
                InputViewBinder.bindCheckbox(
                    view = view as android.widget.CheckBox,
                    spec = InputViewBinder.readToggleSpec(node),
                )
            },
            NodeType.Switch to { view, node ->
                InputViewBinder.bindSwitch(
                    view = view as android.widget.Switch,
                    spec = InputViewBinder.readToggleSpec(node),
                )
            },
            NodeType.RadioButton to { view, node ->
                InputViewBinder.bindRadioButton(
                    view = view as android.widget.RadioButton,
                    spec = InputViewBinder.readToggleSpec(node),
                )
            },
            NodeType.Slider to { view, node ->
                InputViewBinder.bindSlider(
                    view = view as android.widget.SeekBar,
                    spec = InputViewBinder.readSliderSpec(node),
                )
            },
            NodeType.LinearProgressIndicator to { view, node ->
                FeedbackViewBinder.bindLinearProgressIndicator(
                    view = view as LinearProgressIndicator,
                    spec = FeedbackViewBinder.readProgressSpec(node),
                )
            },
            NodeType.CircularProgressIndicator to { view, node ->
                FeedbackViewBinder.bindCircularProgressIndicator(
                    view = view as CircularProgressIndicator,
                    spec = FeedbackViewBinder.readProgressSpec(node),
                )
            },
            NodeType.Button to { view, node ->
                ContentViewBinder.bindButton(
                    view = view as android.widget.Button,
                    spec = ContentViewBinder.readButtonSpec(
                        node = node,
                        contentColor = readNodeTextColor(node) ?: Color.BLACK,
                    ),
                )
            },
            NodeType.IconButton to { view, node ->
                MediaViewBinder.bindImage(
                    view = view as android.widget.ImageView,
                    spec = MediaViewBinder.readImageSpec(node),
                )
                MediaViewBinder.bindIconButton(
                    view = view as android.widget.ImageButton,
                    enabled = MediaViewBinder.readIconButtonEnabled(node),
                )
            },
            NodeType.Row to { view, node ->
                ContainerViewBinder.bindRow(
                    view = view as DeclarativeLinearLayout,
                    spec = ContainerViewBinder.readRowSpec(node),
                )
            },
            NodeType.Column to { view, node ->
                ContainerViewBinder.bindColumn(
                    view = view as DeclarativeLinearLayout,
                    spec = ContainerViewBinder.readColumnSpec(node),
                )
            },
            NodeType.Box to { view, node ->
                ContainerViewBinder.bindBox(
                    view = view as DeclarativeBoxLayout,
                    spec = ContainerViewBinder.readBoxSpec(node),
                )
            },
            NodeType.Surface to { view, node ->
                ContainerViewBinder.bindBox(
                    view = view as DeclarativeBoxLayout,
                    spec = ContainerViewBinder.readBoxSpec(node),
                )
            },
            NodeType.Spacer to { _, _ -> Unit },
            NodeType.Divider to { view, node ->
                view.setBackgroundColor(
                    ContainerViewBinder.readDividerSpec(node).color,
                )
            },
            NodeType.Image to { view, node ->
                MediaViewBinder.bindImage(
                    view = view as android.widget.ImageView,
                    spec = MediaViewBinder.readImageSpec(node),
                )
            },
            NodeType.AndroidView to { view, node ->
                val update = ContainerViewBinder.readAndroidViewSpec(node).update
                update?.invoke(view)
            },
            NodeType.LazyColumn to { view, node ->
                CollectionViewBinder.bindLazyColumn(
                    view = view as RecyclerView,
                    spec = CollectionViewBinder.readLazyColumnSpec(node),
                )
            },
            NodeType.LazyRow to { view, node ->
                CollectionViewBinder.bindLazyRow(
                    view = view as RecyclerView,
                    spec = CollectionViewBinder.readLazyRowSpec(node),
                )
            },
            NodeType.TabPager to { view, node ->
                PagerViewBinder.bindTabPager(
                    view = view as DeclarativeTabPagerLayout,
                    spec = PagerViewBinder.readTabPagerSpec(
                        node = node,
                        defaultRippleColor = defaultRippleColor,
                    ),
                )
            },
            NodeType.SegmentedControl to { view, node ->
                PagerViewBinder.bindSegmentedControl(
                    view = view as DeclarativeSegmentedControlLayout,
                    spec = PagerViewBinder.readSegmentedControlSpec(
                        node = node,
                        defaultRippleColor = defaultRippleColor,
                    ),
                )
            },
            NodeType.ScrollableColumn to { view, node ->
                ScrollableViewBinder.bindScrollableColumn(
                    view = view as DeclarativeScrollableColumnLayout,
                    spec = ScrollableViewBinder.readScrollableColumnSpec(node),
                )
            },
            NodeType.ScrollableRow to { view, node ->
                ScrollableViewBinder.bindScrollableRow(
                    view = view as DeclarativeScrollableRowLayout,
                    spec = ScrollableViewBinder.readScrollableRowSpec(node),
                )
            },
            NodeType.FlowRow to { view, node ->
                ContainerViewBinder.bindFlowRow(
                    view = view as DeclarativeFlowRowLayout,
                    spec = ContainerViewBinder.readFlowRowSpec(node),
                )
            },
            NodeType.FlowColumn to { view, node ->
                ContainerViewBinder.bindFlowColumn(
                    view = view as DeclarativeFlowColumnLayout,
                    spec = ContainerViewBinder.readFlowColumnSpec(node),
                )
            },
            NodeType.NavigationBar to { view, node ->
                CollectionViewBinder.bindNavigationBar(
                    view = view as DeclarativeNavigationBarLayout,
                    spec = CollectionViewBinder.readNavigationBarSpec(node),
                )
            },
            NodeType.HorizontalPager to { view, node ->
                PagerViewBinder.bindHorizontalPager(
                    view = view as DeclarativeHorizontalPagerLayout,
                    spec = PagerViewBinder.readHorizontalPagerSpec(node),
                )
            },
            NodeType.TabRow to { view, node ->
                PagerViewBinder.bindTabRow(
                    view = view as DeclarativeTabRowLayout,
                    spec = PagerViewBinder.readTabRowSpec(node),
                )
            },
            NodeType.VerticalPager to { view, node ->
                PagerViewBinder.bindVerticalPager(
                    view = view as DeclarativeVerticalPagerLayout,
                    spec = PagerViewBinder.readVerticalPagerSpec(node),
                )
            },
            NodeType.LazyVerticalGrid to { view, node ->
                CollectionViewBinder.bindLazyVerticalGrid(
                    view = view as DeclarativeLazyVerticalGridLayout,
                    spec = CollectionViewBinder.readLazyVerticalGridSpec(node),
                )
            },
            NodeType.PullToRefresh to { view, node ->
                ScrollableViewBinder.bindPullToRefresh(
                    view = view as SwipeRefreshLayout,
                    spec = ScrollableViewBinder.readPullToRefreshSpec(node),
                )
            },
        )
    }

    fun bind(
        view: View,
        node: VNode,
    ) {
        binders.getValue(node.type).invoke(view, node)
    }

    fun applyPatch(
        view: View,
        patch: NodeViewPatch,
    ) {
        when (patch) {
            is RowNodePatch -> {
                ContainerNodePatchApplier.applyRowPatch(
                    view = view as DeclarativeLinearLayout,
                    patch = patch,
                )
            }
            is ColumnNodePatch -> {
                ContainerNodePatchApplier.applyColumnPatch(
                    view = view as DeclarativeLinearLayout,
                    patch = patch,
                )
            }
            is BoxNodePatch -> {
                ContainerNodePatchApplier.applyBoxPatch(
                    view = view as DeclarativeBoxLayout,
                    patch = patch,
                )
            }
            is ImageNodePatch -> {
                MediaNodePatchApplier.applyImagePatch(
                    view = view as android.widget.ImageView,
                    patch = patch,
                )
            }
            is IconButtonNodePatch -> {
                MediaNodePatchApplier.applyIconButtonPatch(
                    view = view as android.widget.ImageButton,
                    patch = patch,
                )
            }
            is ButtonNodePatch -> {
                ContentNodePatchApplier.applyButtonPatch(
                    view = view as android.widget.Button,
                    patch = patch,
                )
            }
            is TextNodePatch -> {
                ContentNodePatchApplier.applyTextPatch(
                    view = view as TextView,
                    patch = patch,
                )
            }
            is TextFieldNodePatch -> {
                InputNodePatchApplier.applyTextFieldPatch(
                    view = view as DeclarativeTextFieldLayout,
                    patch = patch,
                )
            }
            is TabPagerNodePatch -> {
                ContainerNodePatchApplier.applyTabPagerPatch(
                    view = view as DeclarativeTabPagerLayout,
                    patch = patch,
                )
            }
            is SegmentedControlNodePatch -> {
                ContainerNodePatchApplier.applySegmentedControlPatch(
                    view = view as DeclarativeSegmentedControlLayout,
                    patch = patch,
                )
            }
            is LazyColumnNodePatch -> {
                ContainerNodePatchApplier.applyLazyColumnPatch(
                    view = view as RecyclerView,
                    patch = patch,
                )
            }
            is LazyRowNodePatch -> {
                ContainerNodePatchApplier.applyLazyRowPatch(
                    view = view as RecyclerView,
                    patch = patch,
                )
            }
            is ToggleNodePatch -> {
                InputNodePatchApplier.applyTogglePatch(
                    view = view as android.widget.CompoundButton,
                    patch = patch,
                )
            }
            is SliderNodePatch -> {
                InputNodePatchApplier.applySliderPatch(
                    view = view as android.widget.SeekBar,
                    patch = patch,
                )
            }
            is ProgressIndicatorNodePatch -> {
                FeedbackNodePatchApplier.applyProgressIndicatorPatch(
                    view = view as android.widget.ProgressBar,
                    patch = patch,
                )
            }
            is DividerNodePatch -> {
                ContentNodePatchApplier.applyDividerPatch(
                    view = view,
                    patch = patch,
                )
            }
            is ScrollableColumnNodePatch -> {
                ContainerNodePatchApplier.applyScrollableColumnPatch(
                    view = view as DeclarativeScrollableColumnLayout,
                    patch = patch,
                )
            }
            is ScrollableRowNodePatch -> {
                ContainerNodePatchApplier.applyScrollableRowPatch(
                    view = view as DeclarativeScrollableRowLayout,
                    patch = patch,
                )
            }
            is FlowRowNodePatch -> {
                ContainerNodePatchApplier.applyFlowRowPatch(
                    view = view as DeclarativeFlowRowLayout,
                    patch = patch,
                )
            }
            is FlowColumnNodePatch -> {
                ContainerNodePatchApplier.applyFlowColumnPatch(
                    view = view as DeclarativeFlowColumnLayout,
                    patch = patch,
                )
            }
            is NavigationBarNodePatch -> {
                ContainerNodePatchApplier.applyNavigationBarPatch(
                    view = view as DeclarativeNavigationBarLayout,
                    patch = patch,
                )
            }
            is HorizontalPagerNodePatch -> {
                ContainerNodePatchApplier.applyHorizontalPagerPatch(
                    view = view as DeclarativeHorizontalPagerLayout,
                    patch = patch,
                )
            }
            is TabRowNodePatch -> {
                ContainerNodePatchApplier.applyTabRowPatch(
                    view = view as DeclarativeTabRowLayout,
                    patch = patch,
                )
            }
            is VerticalPagerNodePatch -> {
                ContainerNodePatchApplier.applyVerticalPagerPatch(
                    view = view as DeclarativeVerticalPagerLayout,
                    patch = patch,
                )
            }
            is LazyVerticalGridNodePatch -> {
                ContainerNodePatchApplier.applyLazyVerticalGridPatch(
                    view = view as DeclarativeLazyVerticalGridLayout,
                    patch = patch,
                )
            }
            is PullToRefreshNodePatch -> {
                ContainerNodePatchApplier.applyPullToRefreshPatch(
                    view = view as SwipeRefreshLayout,
                    patch = patch,
                )
            }
            else -> error("Unknown patch type: ${patch::class.simpleName}")
        }
    }

    private fun readNodeTextColor(node: VNode): Int? {
        val spec = node.spec as? ButtonNodeProps
        if (spec != null) return spec.textColor
        return node.props[TypedPropKeys.TextColor]
    }
}
