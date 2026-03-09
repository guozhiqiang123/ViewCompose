package com.viewcompose.renderer.view.tree

import com.viewcompose.ui.modifier.FocusFollowKeyboardModifierElement
import com.viewcompose.ui.modifier.LazyContainerReuseModifierElement
import com.viewcompose.ui.modifier.Modifier

internal data class LazyContainerReusePolicy(
    val sharePool: Boolean,
    val disableItemAnimator: Boolean,
)

internal data class FocusFollowKeyboardPolicy(
    val enabled: Boolean,
)

internal fun Modifier.lazyContainerReusePolicy(): LazyContainerReusePolicy {
    val element = elements
        .asReversed()
        .firstOrNull { it is LazyContainerReuseModifierElement } as? LazyContainerReuseModifierElement
    if (element == null) {
        return LazyContainerReusePolicy(
            sharePool = false,
            disableItemAnimator = false,
        )
    }
    return LazyContainerReusePolicy(
        sharePool = element.sharePool,
        disableItemAnimator = element.disableItemAnimator,
    )
}

internal fun Modifier.focusFollowKeyboardPolicy(): FocusFollowKeyboardPolicy {
    val element = elements
        .asReversed()
        .firstOrNull { it is FocusFollowKeyboardModifierElement } as? FocusFollowKeyboardModifierElement
    if (element == null) {
        return FocusFollowKeyboardPolicy(
            enabled = false,
        )
    }
    return FocusFollowKeyboardPolicy(
        enabled = element.enabled,
    )
}
