package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.focusFollowKeyboard
import com.viewcompose.ui.modifier.lazyContainerMotion
import com.viewcompose.ui.modifier.lazyContainerReuse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContainerModifierPolicyResolverTest {
    @Test
    fun `lazyContainerReusePolicy defaults to local pool and animator enabled`() {
        val policy = Modifier.lazyContainerReusePolicy()

        assertFalse(policy.sharePool)
        assertFalse(policy.disableItemAnimator)
    }

    @Test
    fun `focusFollowKeyboardPolicy defaults to disabled`() {
        val policy = Modifier.focusFollowKeyboardPolicy()

        assertFalse(policy.enabled)
    }

    @Test
    fun `lazyContainerMotionPolicy defaults to all motions enabled`() {
        val policy = Modifier.lazyContainerMotionPolicy()

        assertTrue(policy.animateInsert)
        assertTrue(policy.animateRemove)
        assertTrue(policy.animateMove)
        assertTrue(policy.animateChange)
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
    fun `focusFollowKeyboardPolicy uses last element in chain`() {
        val policy = Modifier
            .focusFollowKeyboard(enabled = true)
            .focusFollowKeyboard(enabled = false)
            .focusFollowKeyboardPolicy()

        assertEquals(
            FocusFollowKeyboardPolicy(
                enabled = false,
            ),
            policy,
        )
    }

    @Test
    fun `lazyContainerMotionPolicy uses last element in chain`() {
        val policy = Modifier
            .lazyContainerMotion(
                animateInsert = true,
                animateRemove = true,
                animateMove = true,
                animateChange = true,
            )
            .lazyContainerMotion(
                animateInsert = true,
                animateRemove = false,
                animateMove = true,
                animateChange = false,
            )
            .lazyContainerMotionPolicy()

        assertEquals(
            LazyContainerMotionPolicy(
                animateInsert = true,
                animateRemove = false,
                animateMove = true,
                animateChange = false,
            ),
            policy,
        )
    }
}
