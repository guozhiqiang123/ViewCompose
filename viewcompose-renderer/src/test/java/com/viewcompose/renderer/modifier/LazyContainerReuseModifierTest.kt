package com.viewcompose.renderer.modifier

import com.viewcompose.ui.modifier.FocusFollowKeyboardModifierElement
import com.viewcompose.ui.modifier.LazyContainerReuseModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.focusFollowKeyboard
import com.viewcompose.ui.modifier.lazyContainerReuse
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
}
