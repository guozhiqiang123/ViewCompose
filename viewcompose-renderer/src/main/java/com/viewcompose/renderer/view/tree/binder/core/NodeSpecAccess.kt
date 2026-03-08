package com.viewcompose.renderer.view.tree

import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.NodeSpec

internal inline fun <reified T : NodeSpec> VNode.requireSpec(): T {
    return spec as? T ?: error(
        "VNode(type=$type) requires spec=${T::class.simpleName}, but was ${spec::class.simpleName}",
    )
}
