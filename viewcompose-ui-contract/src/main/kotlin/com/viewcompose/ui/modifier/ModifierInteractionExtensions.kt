package com.viewcompose.ui.modifier

fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return then(
        ClickableModifierElement(onClick),
    )
}

fun Modifier.contentDescription(description: String?): Modifier {
    return then(
        ContentDescriptionModifierElement(description),
    )
}

fun Modifier.testTag(tag: String): Modifier {
    return then(
        TestTagModifierElement(tag),
    )
}

fun Modifier.overlayAnchor(anchorId: String): Modifier {
    return then(
        OverlayAnchorModifierElement(anchorId),
    )
}

fun Modifier.lazyContainerReuse(
    sharePool: Boolean = false,
    disableItemAnimator: Boolean = false,
): Modifier {
    return then(
        LazyContainerReuseModifierElement(
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        ),
    )
}

fun Modifier.lazyContainerMotion(
    animateInsert: Boolean = true,
    animateRemove: Boolean = true,
    animateMove: Boolean = true,
    animateChange: Boolean = true,
): Modifier {
    return then(
        LazyContainerMotionModifierElement(
            animateInsert = animateInsert,
            animateRemove = animateRemove,
            animateMove = animateMove,
            animateChange = animateChange,
        ),
    )
}

fun Modifier.focusFollowKeyboard(
    enabled: Boolean = true,
): Modifier {
    return then(
        FocusFollowKeyboardModifierElement(
            enabled = enabled,
        ),
    )
}

