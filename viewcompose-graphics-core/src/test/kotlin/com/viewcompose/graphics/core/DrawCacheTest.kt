package com.viewcompose.graphics.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class DrawCacheTest {
    @Test
    fun `getOrBuild reuses cached value for same key`() {
        val cache = DrawCache<List<Int>>()
        var buildCount = 0

        val first = cache.getOrBuild("k1") {
            buildCount += 1
            listOf(1, 2, 3)
        }
        val second = cache.getOrBuild("k1") {
            buildCount += 1
            listOf(9, 9, 9)
        }

        assertSame(first, second)
        assertEquals(1, buildCount)
    }

    @Test
    fun `getOrBuild rebuilds for different key`() {
        val cache = DrawCache<Int>()
        var value = 0

        val first = cache.getOrBuild("a") { ++value }
        val second = cache.getOrBuild("b") { ++value }

        assertEquals(1, first)
        assertEquals(2, second)
    }
}
