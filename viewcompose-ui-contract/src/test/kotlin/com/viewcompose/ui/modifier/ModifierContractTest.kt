package com.viewcompose.ui.modifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModifierContractTest {
    @Test
    fun `then keeps modifier element order`() {
        val modifier = Modifier
            .padding(8)
            .then(Modifier.margin(4))
            .overlayAnchor("anchor-1")

        assertEquals(3, modifier.elements.size)
        assertTrue(modifier.elements[0] is PaddingModifierElement)
        assertTrue(modifier.elements[1] is MarginModifierElement)
        assertEquals("anchor-1", (modifier.elements[2] as OverlayAnchorModifierElement).anchorId)
    }

    @Test
    fun `background drawable resource modifier appends expected element`() {
        val modifier = Modifier
            .backgroundColor(0xFF112233.toInt())
            .backgroundDrawableRes(123)

        assertEquals(2, modifier.elements.size)
        assertTrue(modifier.elements[0] is BackgroundColorModifierElement)
        assertEquals(123, (modifier.elements[1] as BackgroundDrawableResModifierElement).resId)
    }
}
