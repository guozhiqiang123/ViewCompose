package com.viewcompose.runtime.observation

internal interface ObservableState {
    fun addObserver(observer: Observation)
    fun removeObserver(observer: Observation)
}

class Observation internal constructor(
    private val onInvalidated: () -> Unit,
) {
    private val states = LinkedHashSet<ObservableState>()

    internal fun record(state: ObservableState) {
        if (states.add(state)) {
            state.addObserver(this)
        }
    }

    internal fun invalidate() {
        onInvalidated()
    }

    fun dispose() {
        states.forEach { state ->
            state.removeObserver(this)
        }
        states.clear()
    }
}

object RuntimeObservation {
    private val currentObservation = ThreadLocal<Observation?>()

    fun <T> observeReads(
        onInvalidated: () -> Unit,
        block: () -> T,
    ): Pair<T, Observation> {
        val observation = Observation(onInvalidated)
        val previous = currentObservation.get()
        currentObservation.set(observation)
        return try {
            block() to observation
        } finally {
            currentObservation.set(previous)
        }
    }

    internal fun recordRead(state: ObservableState) {
        currentObservation.get()?.record(state)
    }
}
