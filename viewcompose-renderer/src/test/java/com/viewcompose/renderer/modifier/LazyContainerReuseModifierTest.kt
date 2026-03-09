package com.viewcompose.renderer.modifier

import com.viewcompose.renderer.view.tree.FocusFollowKeyboardPolicy
import com.viewcompose.renderer.view.tree.LazyContainerReusePolicy
import com.viewcompose.renderer.view.tree.focusFollowKeyboardPolicy
import com.viewcompose.renderer.view.tree.lazyContainerReusePolicy
import com.viewcompose.ui.modifier.FocusFollowKeyboardModifierElement
import com.viewcompose.ui.modifier.LazyContainerReuseModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.focusFollowKeyboard
import com.viewcompose.ui.modifier.lazyContainerReuse
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
    fun `focusFollowKeyboard appends focus element`() {
        val modifier = Modifier.focusFollowKeyboard(enabled = true)

        val element = modifier.elements.single() as FocusFollowKeyboardModifierElement
        assertTrue(element.enabled)
    }

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

}
