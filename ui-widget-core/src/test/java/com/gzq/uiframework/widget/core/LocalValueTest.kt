package com.gzq.uiframework.widget.core

import org.junit.Assert.assertEquals
import org.junit.Test

class LocalValueTest {
    @Test
    fun `local uses default outside provider`() {
        val local = LocalValue { 7 }

        assertEquals(7, LocalContext.current(local))
    }

    @Test
    fun `nested providers restore previous values`() {
        val local = LocalValue { "default" }
        var outer = ""
        var inner = ""
        var restored = ""

        LocalContext.provide(local, "outer") {
            outer = LocalContext.current(local)
            LocalContext.provide(local, "inner") {
                inner = LocalContext.current(local)
            }
            restored = LocalContext.current(local)
        }

        assertEquals("outer", outer)
        assertEquals("inner", inner)
        assertEquals("outer", restored)
    }

    @Test
    fun `captured snapshot can restore deferred locals`() {
        val local = LocalValue { "default" }
        var captured: LocalSnapshot? = null
        var restored = ""

        LocalContext.provide(local, "deferred") {
            captured = LocalContext.snapshot()
        }
        LocalContext.withSnapshot(captured!!) {
            restored = LocalContext.current(local)
        }

        assertEquals("deferred", restored)
        assertEquals("default", LocalContext.current(local))
    }
}
