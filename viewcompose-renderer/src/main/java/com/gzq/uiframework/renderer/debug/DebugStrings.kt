package com.gzq.uiframework.renderer.debug

import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.InsertPatch
import com.gzq.uiframework.renderer.reconcile.ReconcileResult
import com.gzq.uiframework.renderer.reconcile.RemovePatch
import com.gzq.uiframework.renderer.reconcile.ReusePatch
import com.gzq.uiframework.renderer.view.tree.RenderStats
import com.gzq.uiframework.renderer.view.tree.RenderStructureStats
import com.gzq.uiframework.renderer.view.tree.RenderTreeResult

fun List<VNode>.debugTree(): String {
    if (isEmpty()) {
        return "<empty>"
    }
    return joinToString(separator = "\n") { node ->
        node.debugTree(indent = 0)
    }
}

fun <T> ReconcileResult<T>.debugSummary(): String {
    val parts = mutableListOf<String>()
    patches.forEach { patch ->
        val line = when (patch) {
            is InsertPatch -> "insert@${patch.targetIndex}:${patch.nextVNode.type}"
            is ReusePatch -> "reuse ${patch.previousIndex}->${patch.targetIndex}:${patch.nextVNode.type}"
        }
        parts += line
    }
    removals.forEach { removal ->
        parts += "remove@${removal.previousIndex}"
    }
    return if (parts.isEmpty()) {
        "<no patches>"
    } else {
        parts.joinToString(separator = "\n")
    }
}

fun RenderTreeResult.debugSummary(): String {
    val reconcile = reconcileResult.debugSummary()
    val stats = stats.debugSummary()
    val structure = structure.debugSummary()
    return buildString {
        append(reconcile)
        append("\n--\n")
        append(stats)
        append("\n")
        append(structure)
        if (warnings.isNotEmpty()) {
            append("\n")
            append(warnings.joinToString(separator = "\n") { warning -> "warning: $warning" })
        }
    }
}

fun RenderStats.debugSummary(): String {
    return "inserts=$inserts reuses=$reuses removals=$removals rebound=$reboundNodes patched=$patchedNodes skipped=$skippedBindings subtreeSkipped=$skippedSubtrees"
}

fun RenderStats.debugBindingsByType(): String {
    if (bindingsByType.isEmpty()) {
        return "<no per-type data>"
    }
    return bindingsByType.entries
        .sortedByDescending { it.value.patched + it.value.rebound }
        .joinToString(separator = "\n") { (type, stats) ->
            val typeName = type::class.simpleName ?: "?"
            "  $typeName: patched=${stats.patched} rebound=${stats.rebound} skipped=${stats.skipped}"
        }
}

fun RenderStructureStats.debugSummary(): String {
    return "vnodeCount=$vnodeCount mountedCount=$mountedNodeCount vnodeDepth=$maxVNodeDepth mountedDepth=$maxMountedDepth"
}

private fun VNode.debugTree(indent: Int): String {
    val prefix = "  ".repeat(indent)
    val label = buildString {
        append(type)
        if (key != null) {
            append("(key=")
            append(key)
            append(")")
        }
    }
    if (children.isEmpty()) {
        return prefix + label
    }
    return buildString {
        append(prefix)
        append(label)
        children.forEach { child ->
            append('\n')
            append(child.debugTree(indent + 1))
        }
    }
}
