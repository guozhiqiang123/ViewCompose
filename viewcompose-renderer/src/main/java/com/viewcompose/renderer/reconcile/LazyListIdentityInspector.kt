package com.viewcompose.renderer.reconcile

import com.viewcompose.renderer.node.LazyListItem

data class LazyListIdentityAnalysis(
    val missingKeyIndexes: List<Int>,
    val duplicateKeys: List<Any>,
) {
    val supportsKeyedDiff: Boolean
        get() = missingKeyIndexes.isEmpty() && duplicateKeys.isEmpty()

    fun warningMessage(listName: String): String? {
        if (supportsKeyedDiff) {
            return null
        }
        val parts = buildList {
            if (missingKeyIndexes.isNotEmpty()) {
                add("missing keys at indexes $missingKeyIndexes")
            }
            if (duplicateKeys.isNotEmpty()) {
                add("duplicate keys $duplicateKeys")
            }
        }
        return "LazyColumn $listName cannot use keyed diff: ${parts.joinToString()}"
    }
}

object LazyListIdentityInspector {
    fun analyze(items: List<LazyListItem>): LazyListIdentityAnalysis {
        val missingKeyIndexes = items.mapIndexedNotNull { index, item ->
            index.takeIf { item.key == null }
        }
        val duplicateKeys = items
            .mapNotNull { it.key }
            .groupingBy { it }
            .eachCount()
            .filterValues { count -> count > 1 }
            .keys
            .toList()
        return LazyListIdentityAnalysis(
            missingKeyIndexes = missingKeyIndexes,
            duplicateKeys = duplicateKeys,
        )
    }
}
