package com.viewcompose.renderer.view.tree

data class RenderStructureStats(
    val vnodeCount: Int = 0,
    val mountedNodeCount: Int = 0,
    val maxVNodeDepth: Int = 0,
    val maxMountedDepth: Int = 0,
) {
    companion object {
        fun from(
            nodes: List<com.viewcompose.ui.node.VNode>,
            mountedNodes: List<MountedNode>,
        ): RenderStructureStats {
            return RenderStructureStats(
                vnodeCount = nodes.sumOf { it.deepNodeCount() },
                mountedNodeCount = mountedNodes.sumOf { it.deepNodeCount() },
                maxVNodeDepth = nodes.maxOfOrNull { it.deepDepth() } ?: 0,
                maxMountedDepth = mountedNodes.maxOfOrNull { it.deepDepth() } ?: 0,
            )
        }
    }
}

private fun com.viewcompose.ui.node.VNode.deepNodeCount(): Int {
    return 1 + children.sumOf { it.deepNodeCount() }
}

private fun com.viewcompose.ui.node.VNode.deepDepth(): Int {
    return 1 + (children.maxOfOrNull { it.deepDepth() } ?: 0)
}

private fun MountedNode.deepNodeCount(): Int {
    return 1 + children.sumOf { it.deepNodeCount() }
}

private fun MountedNode.deepDepth(): Int {
    return 1 + (children.maxOfOrNull { it.deepDepth() } ?: 0)
}
