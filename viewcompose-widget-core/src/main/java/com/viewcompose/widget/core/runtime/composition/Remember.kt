package com.viewcompose.widget.core

internal class RememberStore {
    private val slots = mutableListOf<RememberSlot>()
    private var nextIndex: Int = 0

    fun beginRender() {
        nextIndex = 0
    }

    fun <T> remember(
        keys: List<Any?>,
        calculation: () -> T,
    ): T {
        if (nextIndex < slots.size) {
            val slot = slots[nextIndex]
            nextIndex += 1
            if (slot.keys == keys) {
                @Suppress("UNCHECKED_CAST")
                return slot.value as T
            }
            val value = calculation()
            slots[nextIndex - 1] = RememberSlot(
                value = value,
                keys = keys,
            )
            return value
        }
        val value = calculation()
        slots += RememberSlot(
            value = value,
            keys = keys,
        )
        nextIndex += 1
        return value
    }
}

private data class RememberSlot(
    val value: Any?,
    val keys: List<Any?>,
)

internal object RememberContext {
    private val currentStore = ThreadLocal<RememberStore?>()

    fun <T> withStore(
        store: RememberStore,
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

    fun currentStore(): RememberStore? = currentStore.get()
}

fun <T> remember(calculation: () -> T): T {
    return remember(*emptyArray(), calculation = calculation)
}

fun <T> remember(
    vararg keys: Any?,
    calculation: () -> T,
): T {
    val store = RememberContext.currentStore() ?: return calculation()
    val scopedKeys = GroupKeyContext.current() + keys.toList()
    return store.remember(
        keys = scopedKeys,
        calculation = calculation,
    )
}
