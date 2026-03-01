package com.gzq.uiframework.renderer.view.tree

import com.gzq.uiframework.renderer.node.VNode

internal object NodeBindingDiffer {
    fun shouldRebind(
        previous: VNode,
        next: VNode,
    ): Boolean {
        if (previous.type != next.type) {
            return true
        }
        if (previous.spec != next.spec) {
            return true
        }
        if (previous.props != next.props) {
            return true
        }
        if (previous.modifier != next.modifier) {
            return true
        }
        return false
    }
}
