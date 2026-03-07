package com.gzq.uiframework.renderer.view.tree

import android.view.View
import com.gzq.uiframework.renderer.node.VNode

class MountedNode(
    var vnode: VNode,
    val view: View,
    var children: List<MountedNode> = emptyList(),
)
