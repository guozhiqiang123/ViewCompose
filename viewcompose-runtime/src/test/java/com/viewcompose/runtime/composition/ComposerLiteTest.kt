package com.viewcompose.runtime.composition

import com.viewcompose.runtime.mutableStateOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ComposerLiteTest {
    @Test
    fun `compacts invalidations to nearest dirty ancestor`() {
        val parentState = mutableStateOf(0)
        val childState = mutableStateOf(0)
        val composer = ComposerLite()
        lateinit var parentScope: RecomposeScope

        composer.composeRoot {
            composer.runGroup(signature = "parent") { scope ->
                parentScope = scope
                parentState.value
                composer.runGroup(signature = "child") {
                    childState.value
                    Unit
                }
                Unit
            }
        }

        childState.value = 1
        parentState.value = 1

        val compacted = composer.drainInvalidations()
        assertEquals(1, compacted.size)
        assertSame(parentScope, compacted.first())
    }

    @Test
    fun `marks group dirty when explicit inputs change`() {
        val composer = ComposerLite()
        var runs = 0
        val counter = mutableStateOf(0)

        val compose = {
            composer.composeRoot {
                composer.runGroup(
                    signature = "node",
                    inputs = listOf(counter.value),
                ) {
                    runs += 1
                    Unit
                }
            }
        }

        compose()
        compose()
        counter.value = 1
        compose()

        assertEquals(2, runs)
    }

    @Test
    fun `recomposes only invalidated group while skipping clean sibling`() {
        val left = mutableStateOf(0)
        val right = mutableStateOf(0)
        val composer = ComposerLite()
        var leftRuns = 0
        var rightRuns = 0

        val compose = {
            composer.composeRoot {
                composer.runGroup(
                    signature = "left",
                    inputs = listOf(left.value),
                ) {
                    leftRuns += 1
                    left.value
                }
                composer.runGroup(
                    signature = "right",
                    inputs = listOf(right.value),
                ) {
                    rightRuns += 1
                    right.value
                }
            }
        }

        compose()
        left.value = 1
        compose()

        assertEquals(2, leftRuns)
        assertEquals(1, rightRuns)
    }

    @Test
    fun `composeRoot reads remain consistent within one snapshot pass`() {
        val state = mutableStateOf(0)
        val composer = ComposerLite()
        var firstRead = -1
        var secondRead = -1

        composer.composeRoot {
            firstRead = state.value
            state.value = 1
            secondRead = state.value
            Unit
        }

        assertEquals(0, firstRead)
        assertEquals(0, secondRead)
        assertEquals(1, state.value)
    }
}
