package com.viewcompose.runtime.observation

import com.viewcompose.runtime.mutableStateOf
import org.junit.Assert.assertEquals
import org.junit.Test

class RuntimeObservationTest {
    @Test
    fun `nested observeReads restores outer context`() {
        val state = mutableStateOf(0)
        var outerInvalidations = 0
        var innerInvalidations = 0

        val (_, outer) = RuntimeObservation.observeReads(
            onInvalidated = { outerInvalidations += 1 },
        ) {
            state.value
            val (_, inner) = RuntimeObservation.observeReads(
                onInvalidated = { innerInvalidations += 1 },
            ) {
                state.value
            }
            inner.dispose()
            state.value
        }

        state.value = 1

        assertEquals(1, outerInvalidations)
        assertEquals(0, innerInvalidations)
        outer.dispose()
    }

    @Test
    fun `repeated reads in one observation do not duplicate subscriptions`() {
        val state = mutableStateOf(0)
        var invalidations = 0

        val (_, observation) = RuntimeObservation.observeReads(
            onInvalidated = { invalidations += 1 },
        ) {
            state.value
            state.value
            state.value
        }
        state.value = 1

        assertEquals(1, invalidations)
        observation.dispose()
    }

    @Test
    fun `dispose detaches observation from future invalidations`() {
        val state = mutableStateOf(0)
        var invalidations = 0

        val (_, observation) = RuntimeObservation.observeReads(
            onInvalidated = { invalidations += 1 },
        ) {
            state.value
        }
        observation.dispose()
        state.value = 1

        assertEquals(0, invalidations)
    }
}
