package com.gzq.uiframework.runtime

internal class DerivedStateImpl<T>(
    private val block: () -> T,
) : State<T>, ObservableState {
    private val observers = LinkedHashSet<Observation>()

    private var dependencyObservation: Observation? = null
    private var cachedValue: Any? = Uninitialized
    private var dirty: Boolean = true

    override val value: T
        get() {
            RuntimeObservation.recordRead(this)
            if (dirty) {
                recompute()
            }
            @Suppress("UNCHECKED_CAST")
            return cachedValue as T
        }

    override fun addObserver(observer: Observation) {
        observers += observer
    }

    override fun removeObserver(observer: Observation) {
        observers -= observer
    }

    private fun recompute() {
        dependencyObservation?.dispose()
        val (nextValue, nextObservation) = RuntimeObservation.observeReads(
            onInvalidated = ::invalidate,
        ) {
            block()
        }
        dependencyObservation = nextObservation
        cachedValue = nextValue
        dirty = false
    }

    private fun invalidate() {
        if (dirty) {
            return
        }
        dirty = true
        observers.toList().forEach { observer ->
            observer.invalidate()
        }
    }

    private object Uninitialized
}
