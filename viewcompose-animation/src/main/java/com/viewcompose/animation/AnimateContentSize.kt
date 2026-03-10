package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationSpec
import com.viewcompose.animation.core.spring
import com.viewcompose.ui.modifier.Modifier

/**
 * API compatibility entry for Compose-like animateContentSize.
 *
 * Current implementation is opt-in no-op at modifier layer; list/container motion and
 * Android transition interop are handled by dedicated APIs.
 */
fun Modifier.animateContentSize(
    animationSpec: AnimationSpec = spring(),
): Modifier {
    @Suppress("UNUSED_PARAMETER")
    val ignored = animationSpec
    return this
}
