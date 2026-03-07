package com.viewcompose.renderer.modifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeViewElementTest {
    @Test
    fun `equals compares only stableKey`() {
        val a = NativeViewElement(stableKey = "rotation") { }
        val b = NativeViewElement(stableKey = "rotation") { }
        val c = NativeViewElement(stableKey = "elevation") { }

        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun `hashCode depends only on stableKey`() {
        val a = NativeViewElement(stableKey = 42) { }
        val b = NativeViewElement(stableKey = 42) { }

        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `nativeView extension appends NativeViewElement to modifier chain`() {
        val modifier = Modifier.nativeView(key = "test") { }

        assertTrue(modifier.elements.single() is NativeViewElement)
        assertEquals("test", (modifier.elements.single() as NativeViewElement).stableKey)
    }

    @Test
    fun `modifier with same nativeView key equals another`() {
        val m1 = Modifier.nativeView(key = "a") { }
        val m2 = Modifier.nativeView(key = "a") { }

        assertEquals(m1.elements, m2.elements)
    }

    @Test
    fun `modifier with different nativeView key differs`() {
        val m1 = Modifier.nativeView(key = "a") { }
        val m2 = Modifier.nativeView(key = "b") { }

        assertNotEquals(m1.elements, m2.elements)
    }
}
