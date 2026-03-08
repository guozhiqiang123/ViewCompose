package com.viewcompose.renderer.view.tree

import androidx.recyclerview.widget.RecyclerView
import com.viewcompose.renderer.node.NodeType
import com.viewcompose.renderer.node.spec.LazyColumnNodeProps
import com.viewcompose.renderer.node.spec.LazyRowNodeProps
import com.viewcompose.renderer.view.container.DeclarativeHorizontalPagerLayout
import com.viewcompose.renderer.view.container.DeclarativeLazyVerticalGridLayout
import com.viewcompose.renderer.view.container.DeclarativeTabRowLayout
import com.viewcompose.renderer.view.container.DeclarativeVerticalPagerLayout
import com.viewcompose.renderer.view.lazy.LazyListAdapter

internal object ViewTreeDisposer {
    fun disposeMountedNode(mountedNode: MountedNode) {
        mountedNode.children.forEach(::disposeMountedNode)
        (mountedNode.view as? DeclarativeHorizontalPagerLayout)?.dispose()
        (mountedNode.view as? DeclarativeVerticalPagerLayout)?.dispose()
        (mountedNode.view as? DeclarativeTabRowLayout)?.dispose()
        (mountedNode.view as? DeclarativeLazyVerticalGridLayout)?.dispose()
        (mountedNode.view as? RecyclerView)?.let { recyclerView ->
            if (mountedNode.view !is DeclarativeLazyVerticalGridLayout) {
                (recyclerView.adapter as? LazyListAdapter)?.disposeAll()
            }
            when (mountedNode.vnode.type) {
                NodeType.LazyColumn -> mountedNode.vnode.requireSpec<LazyColumnNodeProps>().state?.recyclerView = null
                NodeType.LazyRow -> mountedNode.vnode.requireSpec<LazyRowNodeProps>().state?.recyclerView = null
                else -> Unit
            }
        }
        mountedNode.children = emptyList()
    }
}
