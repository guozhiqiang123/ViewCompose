package com.gzq.uiframework.renderer.modifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecyclerViewReuseModifierTest {
    @Test
    fun `recyclerViewReuse appends recycler reuse element`() {
        val modifier = Modifier.recyclerViewReuse(
            sharePool = true,
            disableItemAnimator = true,
        )

        val element = modifier.elements.single() as RecyclerViewReuseModifierElement
        assertTrue(element.sharePool)
        assertTrue(element.disableItemAnimator)
    }

    @Test
    fun `recyclerViewReusePolicy defaults to local pool and animator enabled`() {
        val policy = Modifier.recyclerViewReusePolicy()

        assertFalse(policy.sharePool)
        assertFalse(policy.disableItemAnimator)
    }

    @Test
    fun `recyclerViewReusePolicy uses last element in chain`() {
        val policy = Modifier
            .recyclerViewReuse(sharePool = true, disableItemAnimator = true)
            .recyclerViewReuse(sharePool = false, disableItemAnimator = false)
            .recyclerViewReusePolicy()

        assertEquals(
            RecyclerViewReusePolicy(
                sharePool = false,
                disableItemAnimator = false,
            ),
            policy,
        )
    }
}
