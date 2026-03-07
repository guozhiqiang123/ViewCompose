package com.viewcompose.renderer.view.tree

import android.view.View
import com.viewcompose.renderer.node.VNode

class MountedNode(
    var vnode: VNode,
    val view: View,
    var children: List<MountedNode> = emptyList(),
)
