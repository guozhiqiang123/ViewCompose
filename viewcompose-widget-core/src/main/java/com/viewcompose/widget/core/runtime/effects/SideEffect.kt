package com.viewcompose.widget.core

internal class SideEffectStore {
    private val pendingEffects = mutableListOf<() -> Unit>()

    fun beginRender() {
        pendingEffects.clear()
    }

    fun register(effect: () -> Unit) {
        pendingEffects += effect
    }

    fun commit() {
        val effects = pendingEffects.toList()
        pendingEffects.clear()
        effects.forEach { effect ->
            effect()
        }
    }

    fun disposeAll() {
        pendingEffects.clear()
    }
}

internal object SideEffectContext {
    private val currentStore = ThreadLocal<SideEffectStore?>()

    fun <T> withStore(
        store: SideEffectStore,
        block: () -> T,
    ): T {
        val previous = currentStore.get()
        store.beginRender()
        currentStore.set(store)
        return try {
            block()
        } finally {
            currentStore.set(previous)
        }
    }

    fun currentStore(): SideEffectStore? = currentStore.get()
}

fun SideEffect(
    effect: () -> Unit,
) {
    SideEffectContext.currentStore()?.register(effect)
}
