package com.viewcompose.host.android.runtime

import android.view.ViewGroup
import com.viewcompose.renderer.view.tree.MountedNode
import com.viewcompose.renderer.view.tree.ViewTreeRenderer
import com.viewcompose.ui.node.VNode
import com.viewcompose.widget.core.CoreRenderEngine
import com.viewcompose.widget.core.CoreRenderFrame

class AndroidCoreRenderEngine : CoreRenderEngine {
    override fun renderInto(
        container: ViewGroup,
        previousMountedNodes: List<Any>,
        nodes: List<VNode>,
    ): CoreRenderFrame {
        val result = ViewTreeRenderer.renderInto(
            container = container,
            previous = previousMountedNodes.filterIsInstance<MountedNode>(),
            nodes = nodes,
        )
        return CoreRenderFrame(
            mountedNodes = result.mountedNodes,
            renderStats = result.stats,
            renderResult = result,
        )
    }

    override fun disposeMounted(
        container: ViewGroup,
        mountedNodes: List<Any>,
    ) {
        ViewTreeRenderer.disposeMounted(
            container = container,
            mountedNodes = mountedNodes.filterIsInstance<MountedNode>(),
        )
    }
}
