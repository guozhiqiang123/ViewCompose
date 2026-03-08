package com.viewcompose.renderer.view.tree

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.BoxNodeProps
import com.viewcompose.renderer.node.spec.ButtonNodeProps
import com.viewcompose.renderer.node.spec.ColumnNodeProps
import com.viewcompose.renderer.node.spec.DividerNodeProps
import com.viewcompose.renderer.node.spec.FlowColumnNodeProps
import com.viewcompose.renderer.node.spec.FlowRowNodeProps
import com.viewcompose.renderer.node.spec.HorizontalPagerNodeProps
import com.viewcompose.renderer.node.spec.IconButtonNodeProps
import com.viewcompose.renderer.node.spec.ImageNodeProps
import com.viewcompose.renderer.node.spec.LazyColumnNodeProps
import com.viewcompose.renderer.node.spec.LazyRowNodeProps
import com.viewcompose.renderer.node.spec.LazyVerticalGridNodeProps
import com.viewcompose.renderer.node.spec.NavigationBarNodeProps
import com.viewcompose.renderer.node.spec.NodeSpec
import com.viewcompose.renderer.node.spec.ProgressIndicatorNodeProps
import com.viewcompose.renderer.node.spec.PullToRefreshNodeProps
import com.viewcompose.renderer.node.spec.RowNodeProps
import com.viewcompose.renderer.node.spec.ScrollableColumnNodeProps
import com.viewcompose.renderer.node.spec.ScrollableRowNodeProps
import com.viewcompose.renderer.node.spec.SegmentedControlNodeProps
import com.viewcompose.renderer.node.spec.SliderNodeProps
import com.viewcompose.renderer.node.spec.TabRowNodeProps
import com.viewcompose.renderer.node.spec.TextFieldNodeProps
import com.viewcompose.renderer.node.spec.TextNodeProps
import com.viewcompose.renderer.node.spec.ToggleNodeProps
import com.viewcompose.renderer.node.spec.VerticalPagerNodeProps
import com.viewcompose.renderer.view.container.DeclarativeBoxLayout
import com.viewcompose.renderer.view.container.DeclarativeFlowColumnLayout
import com.viewcompose.renderer.view.container.DeclarativeFlowRowLayout
import com.viewcompose.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.viewcompose.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.viewcompose.renderer.view.container.DeclarativeLinearLayout
import com.viewcompose.renderer.view.container.DeclarativeNavigationBarLayout
import com.viewcompose.renderer.view.container.DeclarativeScrollableColumnLayout
import com.viewcompose.renderer.view.container.DeclarativeScrollableRowLayout
import com.viewcompose.renderer.view.container.DeclarativeSegmentedControlLayout
import com.viewcompose.renderer.view.container.DeclarativeTabRowLayout
import com.viewcompose.renderer.view.container.DeclarativeVerticalPagerLayout
import com.viewcompose.renderer.view.tree.patch.ContainerNodePatchApplier
import com.viewcompose.renderer.view.tree.patch.ContentNodePatchApplier
import com.viewcompose.renderer.view.tree.patch.FeedbackNodePatchApplier
import com.viewcompose.renderer.view.tree.patch.InputNodePatchApplier
import com.viewcompose.renderer.view.tree.patch.MediaNodePatchApplier
import kotlin.reflect.KClass

private typealias BindBlock = (View, VNode) -> Unit
private typealias PatchApplyBlock = (View, NodeViewPatch) -> Unit
internal typealias PatchFactory = (NodeSpec, NodeSpec) -> NodeViewPatch

internal data class NodeBinderDescriptor(
    val nodeType: NodeType,
    val bind: BindBlock,
    val patch: NodePatchDescriptor? = null,
)

internal data class NodePatchDescriptor(
    val patchClass: KClass<out NodeViewPatch>,
    val specClass: KClass<out NodeSpec>,
    val factory: PatchFactory,
    val apply: PatchApplyBlock,
)

internal object NodeBinderDescriptors {
    val all: List<NodeBinderDescriptor> by lazy { buildDescriptors() }

    fun bindersByType(): Map<NodeType, BindBlock> = all.associateByUnique(
        keySelector = { it.nodeType },
        valueSelector = { it.bind },
        duplicateMessage = { "Duplicate binder descriptor for NodeType: $it" },
    )

    fun patchAppliersByType(): Map<KClass<out NodeViewPatch>, PatchApplyBlock> = all
        .mapNotNull { it.patch }
        .associateByUnique(
            keySelector = { it.patchClass },
            valueSelector = { it.apply },
            duplicateMessage = { "Duplicate patch applier descriptor for NodeViewPatch: ${it.simpleName}" },
        )

    fun patchFactoriesBySpec(): Map<KClass<out NodeSpec>, PatchFactory> = all
        .mapNotNull { it.patch }
        .associateByUnique(
            keySelector = { it.specClass },
            valueSelector = { it.factory },
            duplicateMessage = { "Duplicate patch factory descriptor for NodeSpec: ${it.simpleName}" },
        )

    private inline fun <K, V, T> List<T>.associateByUnique(
        keySelector: (T) -> K,
        valueSelector: (T) -> V,
        duplicateMessage: (K) -> String,
    ): Map<K, V> {
        val result = LinkedHashMap<K, V>(size)
        for (item in this) {
            val key = keySelector(item)
            require(!result.containsKey(key)) { duplicateMessage(key) }
            result[key] = valueSelector(item)
        }
        return result
    }

    private fun buildDescriptors(): List<NodeBinderDescriptor> {
        val textPatch = patchDescriptor<TextNodeProps, TextNodePatch>(
            factory = { previous, next -> TextNodePatch(previous, next) },
            apply = { view, patch ->
                ContentNodePatchApplier.applyTextPatch(
                    view = view as TextView,
                    patch = patch,
                )
            },
        )
        val textFieldPatch = patchDescriptor<TextFieldNodeProps, TextFieldNodePatch>(
            factory = { previous, next -> TextFieldNodePatch(previous, next) },
            apply = { view, patch ->
                InputNodePatchApplier.applyTextFieldPatch(
                    view = view as EditText,
                    patch = patch,
                )
            },
        )
        val togglePatch = patchDescriptor<ToggleNodeProps, ToggleNodePatch>(
            factory = { previous, next -> ToggleNodePatch(previous, next) },
            apply = { view, patch ->
                InputNodePatchApplier.applyTogglePatch(
                    view = view as android.widget.CompoundButton,
                    patch = patch,
                )
            },
        )
        val sliderPatch = patchDescriptor<SliderNodeProps, SliderNodePatch>(
            factory = { previous, next -> SliderNodePatch(previous, next) },
            apply = { view, patch ->
                InputNodePatchApplier.applySliderPatch(
                    view = view as android.widget.SeekBar,
                    patch = patch,
                )
            },
        )
        val progressPatch = patchDescriptor<ProgressIndicatorNodeProps, ProgressIndicatorNodePatch>(
            factory = { previous, next -> ProgressIndicatorNodePatch(previous, next) },
            apply = { view, patch ->
                FeedbackNodePatchApplier.applyProgressIndicatorPatch(
                    view = view as android.widget.ProgressBar,
                    patch = patch,
                )
            },
        )
        val buttonPatch = patchDescriptor<ButtonNodeProps, ButtonNodePatch>(
            factory = { previous, next -> ButtonNodePatch(previous, next) },
            apply = { view, patch ->
                ContentNodePatchApplier.applyButtonPatch(
                    view = view as android.widget.Button,
                    patch = patch,
                )
            },
        )
        val iconButtonPatch = patchDescriptor<IconButtonNodeProps, IconButtonNodePatch>(
            factory = { previous, next -> IconButtonNodePatch(previous, next) },
            apply = { view, patch ->
                MediaNodePatchApplier.applyIconButtonPatch(
                    view = view as android.widget.ImageButton,
                    patch = patch,
                )
            },
        )
        val rowPatch = patchDescriptor<RowNodeProps, RowNodePatch>(
            factory = { previous, next -> RowNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyRowPatch(
                    view = view as DeclarativeLinearLayout,
                    patch = patch,
                )
            },
        )
        val columnPatch = patchDescriptor<ColumnNodeProps, ColumnNodePatch>(
            factory = { previous, next -> ColumnNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyColumnPatch(
                    view = view as DeclarativeLinearLayout,
                    patch = patch,
                )
            },
        )
        val boxPatch = patchDescriptor<BoxNodeProps, BoxNodePatch>(
            factory = { previous, next -> BoxNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyBoxPatch(
                    view = view as DeclarativeBoxLayout,
                    patch = patch,
                )
            },
        )
        val dividerPatch = patchDescriptor<DividerNodeProps, DividerNodePatch>(
            factory = { previous, next -> DividerNodePatch(previous, next) },
            apply = { view, patch ->
                ContentNodePatchApplier.applyDividerPatch(
                    view = view,
                    patch = patch,
                )
            },
        )
        val imagePatch = patchDescriptor<ImageNodeProps, ImageNodePatch>(
            factory = { previous, next -> ImageNodePatch(previous, next) },
            apply = { view, patch ->
                MediaNodePatchApplier.applyImagePatch(
                    view = view as android.widget.ImageView,
                    patch = patch,
                )
            },
        )
        val lazyColumnPatch = patchDescriptor<LazyColumnNodeProps, LazyColumnNodePatch>(
            factory = { previous, next -> LazyColumnNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyLazyColumnPatch(
                    view = view as RecyclerView,
                    patch = patch,
                )
            },
        )
        val lazyRowPatch = patchDescriptor<LazyRowNodeProps, LazyRowNodePatch>(
            factory = { previous, next -> LazyRowNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyLazyRowPatch(
                    view = view as RecyclerView,
                    patch = patch,
                )
            },
        )
        val segmentedControlPatch = patchDescriptor<SegmentedControlNodeProps, SegmentedControlNodePatch>(
            factory = { previous, next -> SegmentedControlNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applySegmentedControlPatch(
                    view = view as DeclarativeSegmentedControlLayout,
                    patch = patch,
                )
            },
        )
        val scrollableColumnPatch = patchDescriptor<ScrollableColumnNodeProps, ScrollableColumnNodePatch>(
            factory = { previous, next -> ScrollableColumnNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyScrollableColumnPatch(
                    view = view as DeclarativeScrollableColumnLayout,
                    patch = patch,
                )
            },
        )
        val scrollableRowPatch = patchDescriptor<ScrollableRowNodeProps, ScrollableRowNodePatch>(
            factory = { previous, next -> ScrollableRowNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyScrollableRowPatch(
                    view = view as DeclarativeScrollableRowLayout,
                    patch = patch,
                )
            },
        )
        val flowRowPatch = patchDescriptor<FlowRowNodeProps, FlowRowNodePatch>(
            factory = { previous, next -> FlowRowNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyFlowRowPatch(
                    view = view as DeclarativeFlowRowLayout,
                    patch = patch,
                )
            },
        )
        val flowColumnPatch = patchDescriptor<FlowColumnNodeProps, FlowColumnNodePatch>(
            factory = { previous, next -> FlowColumnNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyFlowColumnPatch(
                    view = view as DeclarativeFlowColumnLayout,
                    patch = patch,
                )
            },
        )
        val navigationBarPatch = patchDescriptor<NavigationBarNodeProps, NavigationBarNodePatch>(
            factory = { previous, next -> NavigationBarNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyNavigationBarPatch(
                    view = view as DeclarativeNavigationBarLayout,
                    patch = patch,
                )
            },
        )
        val horizontalPagerPatch = patchDescriptor<HorizontalPagerNodeProps, HorizontalPagerNodePatch>(
            factory = { previous, next -> HorizontalPagerNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyHorizontalPagerPatch(
                    view = view as DeclarativeHorizontalPagerLayout,
                    patch = patch,
                )
            },
        )
        val tabRowPatch = patchDescriptor<TabRowNodeProps, TabRowNodePatch>(
            factory = { previous, next -> TabRowNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyTabRowPatch(
                    view = view as DeclarativeTabRowLayout,
                    patch = patch,
                )
            },
        )
        val verticalPagerPatch = patchDescriptor<VerticalPagerNodeProps, VerticalPagerNodePatch>(
            factory = { previous, next -> VerticalPagerNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyVerticalPagerPatch(
                    view = view as DeclarativeVerticalPagerLayout,
                    patch = patch,
                )
            },
        )
        val lazyVerticalGridPatch = patchDescriptor<LazyVerticalGridNodeProps, LazyVerticalGridNodePatch>(
            factory = { previous, next -> LazyVerticalGridNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyLazyVerticalGridPatch(
                    view = view as DeclarativeLazyVerticalGridLayout,
                    patch = patch,
                )
            },
        )
        val pullToRefreshPatch = patchDescriptor<PullToRefreshNodeProps, PullToRefreshNodePatch>(
            factory = { previous, next -> PullToRefreshNodePatch(previous, next) },
            apply = { view, patch ->
                ContainerNodePatchApplier.applyPullToRefreshPatch(
                    view = view as SwipeRefreshLayout,
                    patch = patch,
                )
            },
        )

        return listOf(
            descriptor(
                nodeType = NodeType.Text,
                bind = { view, node ->
                    ContentViewBinder.bindText(
                        view = view as TextView,
                        spec = ContentViewBinder.readTextSpec(node),
                    )
                },
                patch = textPatch,
            ),
            descriptor(
                nodeType = NodeType.TextField,
                bind = { view, node ->
                    InputViewBinder.bindTextField(
                        view = view as EditText,
                        spec = InputViewBinder.readTextFieldSpec(node),
                    )
                },
                patch = textFieldPatch,
            ),
            descriptor(
                nodeType = NodeType.Checkbox,
                bind = { view, node ->
                    InputViewBinder.bindCheckbox(
                        view = view as android.widget.CheckBox,
                        spec = InputViewBinder.readToggleSpec(node),
                    )
                },
                patch = togglePatch,
            ),
            descriptor(
                nodeType = NodeType.Switch,
                bind = { view, node ->
                    InputViewBinder.bindSwitch(
                        view = view as android.widget.Switch,
                        spec = InputViewBinder.readToggleSpec(node),
                    )
                },
                patch = togglePatch,
            ),
            descriptor(
                nodeType = NodeType.RadioButton,
                bind = { view, node ->
                    InputViewBinder.bindRadioButton(
                        view = view as android.widget.RadioButton,
                        spec = InputViewBinder.readToggleSpec(node),
                    )
                },
                patch = togglePatch,
            ),
            descriptor(
                nodeType = NodeType.Slider,
                bind = { view, node ->
                    InputViewBinder.bindSlider(
                        view = view as android.widget.SeekBar,
                        spec = InputViewBinder.readSliderSpec(node),
                    )
                },
                patch = sliderPatch,
            ),
            descriptor(
                nodeType = NodeType.LinearProgressIndicator,
                bind = { view, node ->
                    FeedbackViewBinder.bindLinearProgressIndicator(
                        view = view as LinearProgressIndicator,
                        spec = FeedbackViewBinder.readProgressSpec(node),
                    )
                },
                patch = progressPatch,
            ),
            descriptor(
                nodeType = NodeType.CircularProgressIndicator,
                bind = { view, node ->
                    FeedbackViewBinder.bindCircularProgressIndicator(
                        view = view as CircularProgressIndicator,
                        spec = FeedbackViewBinder.readProgressSpec(node),
                    )
                },
                patch = progressPatch,
            ),
            descriptor(
                nodeType = NodeType.Button,
                bind = { view, node ->
                    ContentViewBinder.bindButton(
                        view = view as android.widget.Button,
                        spec = ContentViewBinder.readButtonSpec(node),
                    )
                },
                patch = buttonPatch,
            ),
            descriptor(
                nodeType = NodeType.IconButton,
                bind = { view, node ->
                    MediaViewBinder.bindImage(
                        view = view as android.widget.ImageView,
                        spec = MediaViewBinder.readImageSpec(node),
                    )
                    MediaViewBinder.bindIconButton(
                        view = view as android.widget.ImageButton,
                        enabled = MediaViewBinder.readIconButtonEnabled(node),
                    )
                },
                patch = iconButtonPatch,
            ),
            descriptor(
                nodeType = NodeType.Row,
                bind = { view, node ->
                    ContainerViewBinder.bindRow(
                        view = view as DeclarativeLinearLayout,
                        spec = ContainerViewBinder.readRowSpec(node),
                    )
                },
                patch = rowPatch,
            ),
            descriptor(
                nodeType = NodeType.Column,
                bind = { view, node ->
                    ContainerViewBinder.bindColumn(
                        view = view as DeclarativeLinearLayout,
                        spec = ContainerViewBinder.readColumnSpec(node),
                    )
                },
                patch = columnPatch,
            ),
            descriptor(
                nodeType = NodeType.Box,
                bind = { view, node ->
                    ContainerViewBinder.bindBox(
                        view = view as DeclarativeBoxLayout,
                        spec = ContainerViewBinder.readBoxSpec(node),
                    )
                },
                patch = boxPatch,
            ),
            descriptor(
                nodeType = NodeType.Surface,
                bind = { view, node ->
                    ContainerViewBinder.bindBox(
                        view = view as DeclarativeBoxLayout,
                        spec = ContainerViewBinder.readBoxSpec(node),
                    )
                },
                patch = boxPatch,
            ),
            descriptor(
                nodeType = NodeType.Spacer,
                bind = { _, _ -> Unit },
            ),
            descriptor(
                nodeType = NodeType.Divider,
                bind = { view, node ->
                    view.setBackgroundColor(
                        ContainerViewBinder.readDividerSpec(node).color,
                    )
                },
                patch = dividerPatch,
            ),
            descriptor(
                nodeType = NodeType.Image,
                bind = { view, node ->
                    MediaViewBinder.bindImage(
                        view = view as android.widget.ImageView,
                        spec = MediaViewBinder.readImageSpec(node),
                    )
                },
                patch = imagePatch,
            ),
            descriptor(
                nodeType = NodeType.AndroidView,
                bind = { view, node ->
                    val update = ContainerViewBinder.readAndroidViewSpec(node).update
                    update?.invoke(view)
                },
            ),
            descriptor(
                nodeType = NodeType.LazyColumn,
                bind = { view, node ->
                    CollectionViewBinder.bindLazyColumn(
                        view = view as RecyclerView,
                        spec = CollectionViewBinder.readLazyColumnSpec(node),
                    )
                },
                patch = lazyColumnPatch,
            ),
            descriptor(
                nodeType = NodeType.LazyRow,
                bind = { view, node ->
                    CollectionViewBinder.bindLazyRow(
                        view = view as RecyclerView,
                        spec = CollectionViewBinder.readLazyRowSpec(node),
                    )
                },
                patch = lazyRowPatch,
            ),
            descriptor(
                nodeType = NodeType.SegmentedControl,
                bind = { view, node ->
                    PagerViewBinder.bindSegmentedControl(
                        view = view as DeclarativeSegmentedControlLayout,
                        spec = PagerViewBinder.readSegmentedControlSpec(node),
                    )
                },
                patch = segmentedControlPatch,
            ),
            descriptor(
                nodeType = NodeType.ScrollableColumn,
                bind = { view, node ->
                    ScrollableViewBinder.bindScrollableColumn(
                        view = view as DeclarativeScrollableColumnLayout,
                        spec = ScrollableViewBinder.readScrollableColumnSpec(node),
                    )
                },
                patch = scrollableColumnPatch,
            ),
            descriptor(
                nodeType = NodeType.ScrollableRow,
                bind = { view, node ->
                    ScrollableViewBinder.bindScrollableRow(
                        view = view as DeclarativeScrollableRowLayout,
                        spec = ScrollableViewBinder.readScrollableRowSpec(node),
                    )
                },
                patch = scrollableRowPatch,
            ),
            descriptor(
                nodeType = NodeType.FlowRow,
                bind = { view, node ->
                    ContainerViewBinder.bindFlowRow(
                        view = view as DeclarativeFlowRowLayout,
                        spec = ContainerViewBinder.readFlowRowSpec(node),
                    )
                },
                patch = flowRowPatch,
            ),
            descriptor(
                nodeType = NodeType.FlowColumn,
                bind = { view, node ->
                    ContainerViewBinder.bindFlowColumn(
                        view = view as DeclarativeFlowColumnLayout,
                        spec = ContainerViewBinder.readFlowColumnSpec(node),
                    )
                },
                patch = flowColumnPatch,
            ),
            descriptor(
                nodeType = NodeType.NavigationBar,
                bind = { view, node ->
                    CollectionViewBinder.bindNavigationBar(
                        view = view as DeclarativeNavigationBarLayout,
                        spec = CollectionViewBinder.readNavigationBarSpec(node),
                    )
                },
                patch = navigationBarPatch,
            ),
            descriptor(
                nodeType = NodeType.HorizontalPager,
                bind = { view, node ->
                    PagerViewBinder.bindHorizontalPager(
                        view = view as DeclarativeHorizontalPagerLayout,
                        spec = PagerViewBinder.readHorizontalPagerSpec(node),
                    )
                },
                patch = horizontalPagerPatch,
            ),
            descriptor(
                nodeType = NodeType.TabRow,
                bind = { view, node ->
                    PagerViewBinder.bindTabRow(
                        view = view as DeclarativeTabRowLayout,
                        spec = PagerViewBinder.readTabRowSpec(node),
                    )
                },
                patch = tabRowPatch,
            ),
            descriptor(
                nodeType = NodeType.VerticalPager,
                bind = { view, node ->
                    PagerViewBinder.bindVerticalPager(
                        view = view as DeclarativeVerticalPagerLayout,
                        spec = PagerViewBinder.readVerticalPagerSpec(node),
                    )
                },
                patch = verticalPagerPatch,
            ),
            descriptor(
                nodeType = NodeType.LazyVerticalGrid,
                bind = { view, node ->
                    CollectionViewBinder.bindLazyVerticalGrid(
                        view = view as DeclarativeLazyVerticalGridLayout,
                        spec = CollectionViewBinder.readLazyVerticalGridSpec(node),
                    )
                },
                patch = lazyVerticalGridPatch,
            ),
            descriptor(
                nodeType = NodeType.PullToRefresh,
                bind = { view, node ->
                    ScrollableViewBinder.bindPullToRefresh(
                        view = view as SwipeRefreshLayout,
                        spec = ScrollableViewBinder.readPullToRefreshSpec(node),
                    )
                },
                patch = pullToRefreshPatch,
            ),
        )
    }

    private fun descriptor(
        nodeType: NodeType,
        bind: BindBlock,
        patch: NodePatchDescriptor? = null,
    ): NodeBinderDescriptor = NodeBinderDescriptor(
        nodeType = nodeType,
        bind = bind,
        patch = patch,
    )

    private inline fun <reified S : NodeSpec, reified P : NodeViewPatch> patchDescriptor(
        noinline factory: (S, S) -> P,
        noinline apply: (View, P) -> Unit,
    ): NodePatchDescriptor {
        return NodePatchDescriptor(
            patchClass = P::class,
            specClass = S::class,
            factory = { previous, next -> factory(previous as S, next as S) },
            apply = { view, patch -> apply(view, patch as P) },
        )
    }
}
