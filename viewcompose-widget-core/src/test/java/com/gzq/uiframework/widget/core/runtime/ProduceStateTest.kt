package com.gzq.uiframework.widget.core

import com.gzq.uiframework.runtime.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ProduceStateTest {
    @Test
    fun `produceState reuses state holder across renders`() {
        val harness = ProduceStateHarness()

        val first = harness.render {
            produceState(initialValue = "initial") {
                value = "first"
                null
            }
        }
        val second = harness.render {
            produceState(initialValue = "other") {
                value = "second"
                null
            }
        }

        assertSame(first, second)
        assertEquals("first", first.value)
        assertEquals("first", second.value)
    }

    @Test
    fun `produceState reruns producer when key changes`() {
        val harness = ProduceStateHarness()

        harness.render {
            produceState(initialValue = 0, 1) {
                value = 1
                null
            }
        }
        val state = harness.render {
            produceState(initialValue = 0, 2) {
                value = 2
                null
            }
        }

        assertEquals(2, state.value)
    }

    @Test
    fun `produceState keeps previous value when key is unchanged`() {
        val harness = ProduceStateHarness()
        var starts = 0

        harness.render {
            produceState(initialValue = 0, "stable") {
                starts += 1
                value = starts
                null
            }
        }
        val state = harness.render {
            produceState(initialValue = 99, "stable") {
                starts += 1
                value = 999
                null
            }
        }

        assertEquals(1, starts)
        assertEquals(1, state.value)
    }

    private class ProduceStateHarness {
        private val rememberStore = RememberStore()
        private val effectStore = EffectStore()

        fun <T> render(
            block: () -> State<T>,
        ): State<T> {
            lateinit var state: State<T>
            EffectContext.withStore(effectStore) {
                RememberContext.withStore(rememberStore) {
                    state = block()
                }
            }
            effectStore.commit()
            return state
        }
    }
}
