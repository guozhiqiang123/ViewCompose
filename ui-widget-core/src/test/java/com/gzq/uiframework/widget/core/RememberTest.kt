package com.gzq.uiframework.widget.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class RememberTest {
    @Test
    fun `remember reuses slot values across renders`() {
        val store = RememberStore()
        var first: Any? = null
        var second: Any? = null

        RememberContext.withStore(store) {
            first = remember { Any() }
        }
        RememberContext.withStore(store) {
            second = remember { Any() }
        }

        assertSame(first, second)
    }

    @Test
    fun `remember keeps slot ordering stable`() {
        val store = RememberStore()
        val firstPass = mutableListOf<Any>()
        val secondPass = mutableListOf<Any>()

        RememberContext.withStore(store) {
            firstPass += remember { Any() }
            firstPass += remember { Any() }
        }
        RememberContext.withStore(store) {
            secondPass += remember { Any() }
            secondPass += remember { Any() }
        }

        assertEquals(2, secondPass.size)
        assertSame(firstPass[0], secondPass[0])
        assertSame(firstPass[1], secondPass[1])
    }

    @Test
    fun `remember outside render context does not persist`() {
        val first = remember { Any() }
        val second = remember { Any() }

        assertNotSame(first, second)
    }

    @Test
    fun `remember with same key reuses slot value`() {
        val store = RememberStore()
        var first: Any? = null
        var second: Any? = null

        RememberContext.withStore(store) {
            first = remember("stable-key") { Any() }
        }
        RememberContext.withStore(store) {
            second = remember("stable-key") { Any() }
        }

        assertSame(first, second)
    }

    @Test
    fun `remember with changed key recreates slot value`() {
        val store = RememberStore()
        var first: Any? = null
        var second: Any? = null

        RememberContext.withStore(store) {
            first = remember("first-key") { Any() }
        }
        RememberContext.withStore(store) {
            second = remember("second-key") { Any() }
        }

        assertNotSame(first, second)
    }
}
