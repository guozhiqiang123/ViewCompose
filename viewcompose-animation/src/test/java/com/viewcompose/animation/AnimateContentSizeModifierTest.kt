package com.viewcompose.animation

import com.viewcompose.animation.core.keyframes
import com.viewcompose.animation.core.snap
import com.viewcompose.animation.core.spring
import com.viewcompose.animation.core.tween
import com.viewcompose.ui.modifier.AnimateContentSizeModifierElement
import com.viewcompose.ui.modifier.ContentSizeKeyframesSpecModel
import com.viewcompose.ui.modifier.ContentSizeSnapSpecModel
import com.viewcompose.ui.modifier.ContentSizeSpringSpecModel
import com.viewcompose.ui.modifier.ContentSizeTweenSpecModel
import com.viewcompose.ui.modifier.Modifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimateContentSizeModifierTest {
    @Test
    fun `tween spec converts to tween model`() {
        val modifier = Modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 260,
                delayMillis = 40,
            ),
        )
        val element = modifier.elements.last() as AnimateContentSizeModifierElement
        assertEquals(
            ContentSizeTweenSpecModel(durationMillis = 260, delayMillis = 40),
            element.animationSpec,
        )
    }

    @Test
    fun `spring keyframes and snap map to expected models`() {
        val springModifier = Modifier.animateContentSize(
            animationSpec = spring(durationMillis = 420),
        )
        val keyframesModifier = Modifier.animateContentSize(
            animationSpec = keyframes(durationMillis = 360),
        )
        val snapModifier = Modifier.animateContentSize(
            animationSpec = snap(),
        )
        assertTrue(
            (springModifier.elements.last() as AnimateContentSizeModifierElement).animationSpec is ContentSizeSpringSpecModel,
        )
        assertTrue(
            (keyframesModifier.elements.last() as AnimateContentSizeModifierElement).animationSpec is ContentSizeKeyframesSpecModel,
        )
        assertEquals(
            ContentSizeSnapSpecModel,
            (snapModifier.elements.last() as AnimateContentSizeModifierElement).animationSpec,
        )
    }
}
