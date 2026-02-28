package com.gzq.uiframework.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class DisposableEffectTest {
    @Test
    fun `reuses effect when key is unchanged`() {
        val store = EffectStore()
        val events = mutableListOf<String>()

        renderEffect(store, key = "stable", events = events)
        renderEffect(store, key = "stable", events = events)

        assertEquals(
            listOf("start:stable"),
            events,
        )
    }

    @Test
    fun `disposes and restarts effect when key changes`() {
        val store = EffectStore()
        val events = mutableListOf<String>()

        renderEffect(store, key = "A", events = events)
        renderEffect(store, key = "B", events = events)

        assertEquals(
            listOf("start:A", "dispose:A", "start:B"),
            events,
        )
    }

    @Test
    fun `disposes effect when slot disappears`() {
        val store = EffectStore()
        val events = mutableListOf<String>()

        renderEffect(store, key = "A", events = events)
        EffectContext.withStore(store) { Unit }
        store.commit()

        assertEquals(
            listOf("start:A", "dispose:A"),
            events,
        )
    }

    @Test
    fun `disposeAll disposes active effects`() {
        val store = EffectStore()
        val events = mutableListOf<String>()

        renderEffect(store, key = "A", events = events)
        store.disposeAll()

        assertEquals(
            listOf("start:A", "dispose:A"),
            events,
        )
    }

    @Test
    fun `reuses effect when composite keys are unchanged`() {
        val store = EffectStore()
        val events = mutableListOf<String>()

        renderEffect(
            store = store,
            events = events,
            keys = arrayOf("user", 1),
        )
        renderEffect(
            store = store,
            events = events,
            keys = arrayOf("user", 1),
        )

        assertEquals(
            listOf("start:user-1"),
            events,
        )
    }

    @Test
    fun `restarts effect when any composite key changes`() {
        val store = EffectStore()
        val events = mutableListOf<String>()

        renderEffect(
            store = store,
            events = events,
            keys = arrayOf("user", 1),
        )
        renderEffect(
            store = store,
            events = events,
            keys = arrayOf("user", 2),
        )

        assertEquals(
            listOf("start:user-1", "dispose:user-1", "start:user-2"),
            events,
        )
    }

    private fun renderEffect(
        store: EffectStore,
        key: String,
        events: MutableList<String>,
    ) {
        renderEffect(
            store = store,
            events = events,
            keys = arrayOf(key),
        )
    }

    private fun renderEffect(
        store: EffectStore,
        events: MutableList<String>,
        keys: Array<out Any?>,
    ) {
        val label = keys.joinToString(separator = "-")
        EffectContext.withStore(store) {
            DisposableEffect(*keys) {
                events += "start:$label"
                {
                    events += "dispose:$label"
                }
            }
        }
        store.commit()
    }
}
