package com.viewcompose.runtime.composition

class SlotTable {
    val root: RecomposeScope = RecomposeScope(
        signature = RootSignature,
        parent = null,
    )

    fun dispose() {
        root.disposeRecursively()
    }

    internal object RootSignature
}
