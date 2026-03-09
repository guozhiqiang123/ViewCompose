package com.viewcompose.renderer.view.tree

import android.widget.TextView
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.ButtonNodeProps
import com.viewcompose.ui.node.spec.DividerNodeProps
import com.viewcompose.ui.node.spec.TextNodeProps
import com.viewcompose.renderer.view.tree.patch.ContentNodePatchApplier

internal fun MutableList<NodeBinderDescriptor>.addContentNodeBinderDescriptors() {
    val textPatch = patchDescriptor<TextNodeProps, TextNodePatch>(
        factory = { previous, next -> TextNodePatch(previous, next) },
        apply = { view, patch ->
            ContentNodePatchApplier.applyTextPatch(
                view = view as TextView,
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
    val dividerPatch = patchDescriptor<DividerNodeProps, DividerNodePatch>(
        factory = { previous, next -> DividerNodePatch(previous, next) },
        apply = { view, patch ->
            ContentNodePatchApplier.applyDividerPatch(
                view = view,
                patch = patch,
            )
        },
    )

    add(
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
    )
    add(
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
    )
    add(
        descriptor(
            nodeType = NodeType.Spacer,
            bind = { _, _ -> Unit },
        ),
    )
    add(
        descriptor(
            nodeType = NodeType.Divider,
            bind = { view, node ->
                view.setBackgroundColor(
                    ContainerViewBinder.readDividerSpec(node).color,
                )
            },
            patch = dividerPatch,
        ),
    )
}
