package com.viewcompose.widget.core

internal class LocalValue<T>(
    private val defaultFactory: () -> T,
) {
    fun default(): T = defaultFactory()
}

internal data class LocalSnapshot(
    val values: Map<LocalValue<*>, Any?>,
)

internal object LocalContext {
    private val currentValues = ThreadLocal<Map<LocalValue<*>, Any?>>()

    fun <T> current(local: LocalValue<T>): T {
        val values = currentValues.get().orEmpty()
        @Suppress("UNCHECKED_CAST")
        return values[local] as? T ?: local.default()
    }

    fun <T> provide(
        local: LocalValue<T>,
        value: T,
        block: () -> Unit,
    ) {
        val previous = currentValues.get().orEmpty()
        currentValues.set(previous + (local to value))
        try {
            block()
        } finally {
            currentValues.set(previous)
        }
    }

    fun snapshot(): LocalSnapshot {
        return LocalSnapshot(
            values = currentValues.get().orEmpty(),
        )
    }

    fun withSnapshot(
        snapshot: LocalSnapshot,
        block: () -> Unit,
    ) {
        val previous = currentValues.get().orEmpty()
        currentValues.set(snapshot.values)
        try {
            block()
        } finally {
            currentValues.set(previous)
        }
    }
}
