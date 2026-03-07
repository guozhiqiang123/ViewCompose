package com.gzq.uiframework.renderer.modifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LazyContainerReuseModifierTest {
    @Test
    fun `lazyContainerReuse appends lazy container reuse element`() {
        val modifier = Modifier.lazyContainerReuse(
            sharePool = true,
            disableItemAnimator = true,
        )

        val element = modifier.elements.single() as LazyContainerReuseModifierElement
        assertTrue(element.sharePool)
        assertTrue(element.disableItemAnimator)
    }

    @Test
    fun `lazyContainerFocusFollowKeyboard appends focus element`() {
        val modifier = Modifier.lazyContainerFocusFollowKeyboard(enabled = true)

        val element = modifier.elements.single() as LazyContainerFocusFollowKeyboardModifierElement
        assertTrue(element.enabled)
    }

    @Test
    fun `lazyContainerReusePolicy defaults to local pool and animator enabled`() {
        val policy = Modifier.lazyContainerReusePolicy()

        assertFalse(policy.sharePool)
        assertFalse(policy.disableItemAnimator)
    }

    @Test
    fun `lazyContainerFocusPolicy defaults to disabled`() {
        val policy = Modifier.lazyContainerFocusPolicy()

        assertFalse(policy.enabled)
    }

    @Test
    fun `lazyContainerReusePolicy uses last element in chain`() {
        val policy = Modifier
            .lazyContainerReuse(sharePool = true, disableItemAnimator = true)
            .lazyContainerReuse(sharePool = false, disableItemAnimator = false)
            .lazyContainerReusePolicy()

        assertEquals(
            LazyContainerReusePolicy(
                sharePool = false,
                disableItemAnimator = false,
            ),
            policy,
        )
    }

    @Test
    fun `lazyContainerFocusPolicy uses last element in chain`() {
        val policy = Modifier
            .lazyContainerFocusFollowKeyboard(enabled = true)
            .lazyContainerFocusFollowKeyboard(enabled = false)
            .lazyContainerFocusPolicy()

        assertEquals(
            LazyContainerFocusPolicy(
                enabled = false,
            ),
            policy,
        )
    }

    @Test
    @Suppress("DEPRECATION")
    fun `deprecated recyclerViewReuse delegates to lazyContainerReuse`() {
        val policy = Modifier
            .recyclerViewReuse(sharePool = true, disableItemAnimator = true)
            .lazyContainerReusePolicy()

        assertTrue(policy.sharePool)
        assertTrue(policy.disableItemAnimator)
    }
}
