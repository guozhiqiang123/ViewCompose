package com.viewcompose.gesture

import com.viewcompose.ui.gesture.GestureOrientation
import com.viewcompose.ui.gesture.GesturePriority
import com.viewcompose.ui.gesture.PointerEvent
import com.viewcompose.ui.gesture.PointerEventResult
import com.viewcompose.ui.gesture.SwipeDirection
import com.viewcompose.ui.modifier.CombinedClickableModifierElement
import com.viewcompose.ui.modifier.DraggableModifierElement
import com.viewcompose.ui.modifier.GesturePriorityModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.PointerInputModifierElement
import com.viewcompose.ui.modifier.SwipeableModifierElement
import com.viewcompose.ui.modifier.TransformableModifierElement

fun Modifier.pointerInput(
    key: Any = Unit,
    onEvent: (PointerEvent) -> PointerEventResult,
): Modifier {
    return then(
        PointerInputModifierElement(
            key = key,
            onEvent = onEvent,
        ),
    )
}

fun Modifier.combinedClickable(
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
): Modifier {
    return then(
        CombinedClickableModifierElement(
            enabled = enabled,
            onClick = onClick,
            onDoubleClick = onDoubleClick,
            onLongClick = onLongClick,
        ),
    )
}

fun Modifier.draggable(
    state: DraggableState,
    orientation: GestureOrientation = GestureOrientation.Horizontal,
    enabled: Boolean = true,
    onDragStarted: (() -> Unit)? = null,
    onDragStopped: ((velocity: Float) -> Unit)? = null,
): Modifier {
    return then(
        DraggableModifierElement(
            enabled = enabled,
            orientation = orientation,
            onDragStarted = onDragStarted,
            onDragStopped = onDragStopped,
            onDelta = state::dispatchRawDelta,
        ),
    )
}

fun <T> Modifier.swipeable(
    state: SwipeableState<T>,
    anchors: Map<Float, T>,
    orientation: GestureOrientation = GestureOrientation.Horizontal,
    enabled: Boolean = true,
    onSwipe: ((SwipeDirection) -> Unit)? = null,
): Modifier {
    val sortedAnchors = anchors.toSortedMap()
    val minAnchorPx = sortedAnchors.entries.firstOrNull()?.key
    val maxAnchorPx = sortedAnchors.entries.lastOrNull()?.key
    val minAnchor = sortedAnchors.entries.firstOrNull()?.value
    val maxAnchor = sortedAnchors.entries.lastOrNull()?.value
    val currentAnchorPx = sortedAnchors.entries.firstOrNull { it.value == state.currentValue.value }?.key
    return then(
        SwipeableModifierElement(
            enabled = enabled,
            orientation = orientation,
            minAnchorPx = minAnchorPx,
            maxAnchorPx = maxAnchorPx,
            currentAnchorPx = currentAnchorPx,
            onSwipe = { direction ->
                val target = when (direction) {
                    SwipeDirection.StartToEnd, SwipeDirection.TopToBottom -> maxAnchor
                    SwipeDirection.EndToStart, SwipeDirection.BottomToTop -> minAnchor
                }
                if (target != null) {
                    state.updateCurrent(target)
                }
                onSwipe?.invoke(direction)
            },
            onSettleToMin = {
                if (minAnchor != null) {
                    state.updateCurrent(minAnchor)
                }
            },
            onSettleToMax = {
                if (maxAnchor != null) {
                    state.updateCurrent(maxAnchor)
                }
            },
            onDelta = null,
        ),
    )
}

fun Modifier.transformable(
    state: TransformableState,
    enabled: Boolean = true,
): Modifier {
    return then(
        TransformableModifierElement(
            enabled = enabled,
            onTransform = state::dispatchTransform,
        ),
    )
}

fun Modifier.gesturePriority(
    priority: GesturePriority = GesturePriority.Default,
): Modifier {
    return then(
        GesturePriorityModifierElement(
            priority = priority,
        ),
    )
}
