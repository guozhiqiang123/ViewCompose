package com.gzq.uiframework.widget.core

internal class RememberStore {
    private val slots = mutableListOf<Any?>()
    private var nextIndex: Int = 0

    fun beginRender() {
        nextIndex = 0
    }

    fun <T> remember(calculation: () -> T): T {
        if (nextIndex < slots.size) {
            @Suppress("UNCHECKED_CAST")
            return slots[nextIndex++] as T
        }
        val value = calculation()
        slots += value
        nextIndex += 1
        return value
    }
}

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
    val store = RememberContext.currentStore() ?: return calculation()
    return store.remember(calculation)
}
