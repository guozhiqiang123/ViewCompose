package com.gzq.uiframework.renderer.view.tree

import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.view.container.DeclarativeTabPagerLayout
import com.gzq.uiframework.renderer.view.lazy.LazyColumnAdapter

internal object ViewTreeDisposer {
    fun disposeMountedNode(mountedNode: MountedNode) {
        mountedNode.children.forEach(::disposeMountedNode)
        (mountedNode.view as? DeclarativeTabPagerLayout)?.dispose()
        (mountedNode.view as? RecyclerView)
            ?.adapter
            ?.let { adapter ->
                (adapter as? LazyColumnAdapter)?.disposeAll()
            }
        mountedNode.children = emptyList()
    }
}
