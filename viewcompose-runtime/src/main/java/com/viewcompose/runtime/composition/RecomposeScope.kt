package com.viewcompose.runtime.composition

import com.viewcompose.runtime.observation.Observation

/**
 * Internal composition node for SlotTable-lite runtime.
 * Exposed as public for cross-module usage from widget-core.
 */
class RecomposeScope internal constructor(
    internal var signature: Any,
    internal val parent: RecomposeScope?,
) {
    internal val children: MutableList<RecomposeScope> = mutableListOf()
    internal val rememberSlots: MutableList<RememberSlot> = mutableListOf()
    internal val effectSlots: MutableList<DisposableEffectSlot> = mutableListOf()
    internal var observation: Observation? = null
    internal var cachedResult: Any? = Unset
    internal var dirty: Boolean = true
    internal var composed: Boolean = false
    internal var disposed: Boolean = false
    internal var localSnapshot: Any? = null
    internal var latestInputs: List<Any?> = emptyList()
    internal var childCursor: Int = 0
    internal var rememberCursor: Int = 0
    internal var effectCursor: Int = 0

    internal fun beginCompose() {
        childCursor = 0
        rememberCursor = 0
        effectCursor = 0
    }

    internal fun trimAfterCompose() {
        while (children.size > childCursor) {
            children.removeAt(children.lastIndex).disposeRecursively()
        }
        while (effectSlots.size > effectCursor) {
            effectSlots.removeAt(effectSlots.lastIndex).onDispose?.invoke()
        }
        while (rememberSlots.size > rememberCursor) {
            rememberSlots.removeAt(rememberSlots.lastIndex)
        }
    }

    internal fun disposeRecursively() {
        if (disposed) return
        disposed = true
        observation?.dispose()
        observation = null
        effectSlots.forEach { slot ->
            slot.onDispose?.invoke()
        }
        effectSlots.clear()
        rememberSlots.clear()
        children.forEach { child ->
            child.disposeRecursively()
        }
        children.clear()
        cachedResult = Unset
        dirty = true
        composed = false
        localSnapshot = null
        latestInputs = emptyList()
    }

    internal fun markDirty() {
        if (disposed) return
        dirty = true
    }

    internal fun markDirtyWithAncestors() {
        var current: RecomposeScope? = this
        while (current != null) {
            current.markDirty()
            current = current.parent
        }
    }

    fun localSnapshotOrNull(): Any? = localSnapshot

    fun updateLocalSnapshot(snapshot: Any?) {
        localSnapshot = snapshot
    }

    internal data class RememberSlot(
        var keys: List<Any?>,
        var value: Any?,
    )

    internal data class DisposableEffectSlot(
        var keys: List<Any?>,
        var onDispose: (() -> Unit)?,
    )

    internal object Unset
}
