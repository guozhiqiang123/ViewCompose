package com.viewcompose.widget.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class RememberUpdatedStateTest {
    @Test
    fun `reuses same state holder across renders`() {
        val store = RememberStore()
        var first: Any? = null
        var second: Any? = null

        RememberContext.withStore(store) {
            first = rememberUpdatedState("A")
        }
        RememberContext.withStore(store) {
            second = rememberUpdatedState("B")
        }

        assertSame(first, second)
    }

    @Test
    fun `exposes latest value on every render`() {
        val store = RememberStore()
        var latest: String? = null

        RememberContext.withStore(store) {
            latest = rememberUpdatedState("A").value
        }
        RememberContext.withStore(store) {
            latest = rememberUpdatedState("B").value
        }

        assertEquals("B", latest)
    }
}
