package com.viewcompose.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class SideEffectTest {
    @Test
    fun `runs all registered effects on commit`() {
        val store = SideEffectStore()
        val events = mutableListOf<String>()

        renderSideEffects(store) {
            SideEffect { events += "first" }
            SideEffect { events += "second" }
        }

        assertEquals(
            listOf("first", "second"),
            events,
        )
    }

    @Test
    fun `runs side effects again on next render`() {
        val store = SideEffectStore()
        val events = mutableListOf<String>()

        renderSideEffects(store) {
            SideEffect { events += "render-1" }
        }
        renderSideEffects(store) {
            SideEffect { events += "render-2" }
        }

        assertEquals(
            listOf("render-1", "render-2"),
            events,
        )
    }

    @Test
    fun `side effect outside render context is ignored`() {
        val events = mutableListOf<String>()

        SideEffect { events += "outside" }

        assertEquals(
            emptyList<String>(),
            events,
        )
    }

    private fun renderSideEffects(
        store: SideEffectStore,
        block: () -> Unit,
    ) {
        SideEffectContext.withStore(store) {
            block()
        }
        store.commit()
    }
}
