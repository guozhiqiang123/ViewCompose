package com.viewcompose.runtime.state

import com.viewcompose.runtime.MutableState
import com.viewcompose.runtime.SnapshotMutationPolicy
import com.viewcompose.runtime.SnapshotRuntime
import com.viewcompose.runtime.observation.ObservableState
import com.viewcompose.runtime.observation.Observation
import com.viewcompose.runtime.observation.RuntimeObservation

internal class MutableStateImpl<T>(
    initialValue: T,
    private val policy: SnapshotMutationPolicy<T>,
) : MutableState<T>, ObservableState, SnapshotStateObject {
    private val recordLock = Any()
    private val observers = LinkedHashSet<Observation>()
    private var head: StateRecord<T> = StateRecord(
        snapshotId = 0,
        value = initialValue,
        next = null,
    )

    override var value: T
        get() {
            RuntimeObservation.recordRead(this)
            @Suppress("UNCHECKED_CAST")
            return SnapshotRuntime.readStateValue(this) as T
        }
        set(value) {
            SnapshotRuntime.writeStateValue(this, value)
        }

    override fun addObserver(observer: Observation) {
        observers += observer
    }

    override fun removeObserver(observer: Observation) {
        observers -= observer
    }

    override fun readAny(readId: Int): Any? = synchronized(recordLock) {
        readRecordLocked(readId).value
    }

    override fun equivalentAny(
        a: Any?,
        b: Any?,
    ): Boolean {
        @Suppress("UNCHECKED_CAST")
        return policy.equivalent(a as T, b as T)
    }

    override fun mergeAny(
        previous: Any?,
        current: Any?,
        applied: Any?,
    ): Any? {
        @Suppress("UNCHECKED_CAST")
        return policy.merge(
            previous = previous as T,
            current = current as T,
            applied = applied as T,
        )
    }

    override fun commitAny(
        snapshotId: Int,
        value: Any?,
    ): Boolean = synchronized(recordLock) {
        @Suppress("UNCHECKED_CAST")
        val next = value as T
        val applied = head.value
        if (policy.equivalent(applied, next)) {
            return@synchronized false
        }
        head = StateRecord(
            snapshotId = snapshotId,
            value = next,
            next = head,
        )
        true
    }

    override fun snapshotObservers(): List<Observation> = observers.toList()

    private fun readRecordLocked(readId: Int): StateRecord<T> {
        var current: StateRecord<T>? = head
        while (current != null) {
            if (current.snapshotId <= readId) {
                return current
            }
            current = current.next
        }
        var oldest = head
        var cursor = head.next
        while (cursor != null) {
            oldest = cursor
            cursor = cursor.next
        }
        return oldest
    }

    private data class StateRecord<T>(
        val snapshotId: Int,
        val value: T,
        val next: StateRecord<T>?,
    )
}
