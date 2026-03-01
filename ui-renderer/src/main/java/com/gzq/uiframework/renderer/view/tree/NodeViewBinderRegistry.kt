package com.gzq.uiframework.renderer.view.tree

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.view.container.DeclarativeBoxLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeLinearLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeSegmentedControlLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout

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
                ContainerViewBinder.bindLazyColumn(
                    view = view as RecyclerView,
                    spec = ContainerViewBinder.readLazyColumnSpec(node),
                )
            },
            NodeType.TabPager to { view, node ->
                ContainerViewBinder.bindTabPager(
                    view = view as DeclarativeTabPagerLayout,
                    spec = ContainerViewBinder.readTabPagerSpec(
                        node = node,
                        defaultRippleColor = defaultRippleColor,
                    ),
                )
            },
            NodeType.SegmentedControl to { view, node ->
                ContainerViewBinder.bindSegmentedControl(
                    view = view as DeclarativeSegmentedControlLayout,
                    spec = ContainerViewBinder.readSegmentedControlSpec(
                        node = node,
                        defaultRippleColor = defaultRippleColor,
                    ),
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
            is ButtonNodePatch -> {
                ContentViewBinder.applyButtonPatch(
                    view = view as android.widget.Button,
                    patch = patch,
                )
            }
            is TextNodePatch -> {
                ContentViewBinder.applyTextPatch(
                    view = view as TextView,
                    patch = patch,
                )
            }
            is TextFieldNodePatch -> {
                InputViewBinder.applyTextFieldPatch(
                    view = view as DeclarativeTextFieldLayout,
                    patch = patch,
                )
            }
        }
    }

    private fun readNodeTextColor(node: VNode): Int? {
        return node.props[TypedPropKeys.TextColor]
    }
}
