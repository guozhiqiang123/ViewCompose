package com.gzq.uiframework.renderer.view.tree

import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.container.DeclarativeTabRowLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter

internal object ViewTreeDisposer {
    fun disposeMountedNode(mountedNode: MountedNode) {
        mountedNode.children.forEach(::disposeMountedNode)
        (mountedNode.view as? DeclarativeTabPagerLayout)?.dispose()
        (mountedNode.view as? DeclarativeHorizontalPagerLayout)?.dispose()
        (mountedNode.view as? DeclarativeTabRowLayout)?.dispose()
        (mountedNode.view as? RecyclerView)?.let { recyclerView ->
            (recyclerView.adapter as? LazyColumnAdapter)?.disposeAll()
            (mountedNode.vnode.spec as? LazyColumnNodeProps)?.state?.recyclerView = null
        }
        mountedNode.children = emptyList()
    }
}
