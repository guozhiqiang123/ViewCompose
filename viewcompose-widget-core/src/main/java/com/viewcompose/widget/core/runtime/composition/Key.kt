package com.viewcompose.widget.core

internal object GroupKeyContext {
    private val currentKeys = ThreadLocal<List<Any?>>()

    fun <T> withKeys(
        keys: List<Any?>,
        block: () -> T,
    ): T {
        val previous = currentKeys.get().orEmpty()
        currentKeys.set(previous + keys)
        return try {
            block()
        } finally {
            currentKeys.set(previous)
        }
    }

    fun current(): List<Any?> = currentKeys.get().orEmpty()
}

fun <T> key(
    vararg keys: Any?,
    block: () -> T,
): T {
    return GroupKeyContext.withKeys(
        keys = keys.toList(),
        block = block,
    )
}
