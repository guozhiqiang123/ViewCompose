package com.viewcompose.widget.core

internal class EffectStore {
    private val slots = mutableListOf<EffectSlot>()
    private val pendingOperations = mutableListOf<EffectOperation>()
    private var nextIndex: Int = 0

    fun beginRender() {
        nextIndex = 0
        pendingOperations.clear()
    }

    fun register(
        keys: List<Any?>,
        effect: () -> (() -> Unit),
    ) {
        val index = nextIndex++
        val existing = slots.getOrNull(index)
        if (existing != null && existing.keys == keys) {
            pendingOperations += EffectOperation.Keep(index)
            return
        }
        pendingOperations += EffectOperation.Start(
            index = index,
            keys = keys,
            effect = effect,
        )
    }

    fun commit() {
        pendingOperations.forEach { operation ->
            when (operation) {
                is EffectOperation.Keep -> Unit
                is EffectOperation.Start -> {
                    val previous = slots.getOrNull(operation.index)
                    previous?.onDispose?.invoke()
                    val onDispose = operation.effect()
                    val nextSlot = EffectSlot(
                        keys = operation.keys,
                        onDispose = onDispose,
                    )
                    if (operation.index < slots.size) {
                        slots[operation.index] = nextSlot
                    } else {
                        slots += nextSlot
                    }
                }
            }
        }
        while (slots.size > nextIndex) {
            slots.removeAt(slots.lastIndex).onDispose?.invoke()
        }
        pendingOperations.clear()
    }

    fun disposeAll() {
        while (slots.isNotEmpty()) {
            slots.removeAt(slots.lastIndex).onDispose?.invoke()
        }
        pendingOperations.clear()
        nextIndex = 0
    }
}

private data class EffectSlot(
    val keys: List<Any?>,
    val onDispose: (() -> Unit)?,
)

private sealed interface EffectOperation {
    data class Keep(
        val index: Int,
    ) : EffectOperation

    data class Start(
        val index: Int,
        val keys: List<Any?>,
        val effect: () -> (() -> Unit),
    ) : EffectOperation
}

internal object EffectContext {
    private val currentStore = ThreadLocal<EffectStore?>()

    fun <T> withStore(
        store: EffectStore,
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

    fun currentStore(): EffectStore? = currentStore.get()
}

fun DisposableEffect(
    vararg keys: Any?,
    effect: () -> (() -> Unit),
) {
    val scopedKeys = GroupKeyContext.current() + keys.toList()
    EffectContext.currentStore()?.register(
        keys = scopedKeys,
        effect = effect,
    )
}
