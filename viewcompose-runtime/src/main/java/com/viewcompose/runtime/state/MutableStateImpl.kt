package com.viewcompose.runtime.state

import com.viewcompose.runtime.MutableState
import com.viewcompose.runtime.observation.ObservableState
import com.viewcompose.runtime.observation.Observation
import com.viewcompose.runtime.observation.RuntimeObservation

internal class MutableStateImpl<T>(
    initialValue: T,
) : MutableState<T>, ObservableState {
    private val observers = LinkedHashSet<Observation>()

    private var backingValue: T = initialValue

    override var value: T
        get() {
            RuntimeObservation.recordRead(this)
            return backingValue
        }
        set(value) {
            if (backingValue == value) {
                return
            }
            backingValue = value
            observers.toList().forEach { observer ->
                observer.invalidate()
            }
        }

    override fun addObserver(observer: Observation) {
        observers += observer
    }

    override fun removeObserver(observer: Observation) {
        observers -= observer
    }
}
