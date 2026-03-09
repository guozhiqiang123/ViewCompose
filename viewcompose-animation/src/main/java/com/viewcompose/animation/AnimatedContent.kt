package com.viewcompose.animation

import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.alpha
import com.viewcompose.ui.modifier.fillMaxSize
import com.viewcompose.widget.core.Box
import com.viewcompose.widget.core.BoxScope
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.remember

fun <T> UiTreeBuilder.AnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier,
    transitionSpec: () -> AnimationSpec = { tween() },
    content: BoxScope.(T) -> Unit,
) {
    val currentState = remember {
        mutableStateOf(targetState)
    }
    val outgoingState = remember {
        mutableStateOf<T?>(null)
    }
    val toggle = remember {
        mutableStateOf(false)
    }
    if (targetState != currentState.value) {
        outgoingState.value = currentState.value
        currentState.value = targetState
        toggle.value = !toggle.value
    }
    val progress = animateFloatAsState(
        targetValue = if (toggle.value) 1f else 0f,
        animationSpec = transitionSpec(),
    )
    val incomingAlpha = if (toggle.value) {
        progress.value
    } else {
        1f - progress.value
    }.coerceIn(0f, 1f)
    val outgoingAlpha = 1f - incomingAlpha
    if (outgoingState.value != null && outgoingAlpha <= 0.001f) {
        outgoingState.value = null
    }
    Box(
        modifier = modifier,
    ) {
        outgoingState.value?.let { previous ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(outgoingAlpha),
            ) {
                content(previous)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(incomingAlpha),
        ) {
            content(currentState.value)
        }
    }
}

fun <T> UiTreeBuilder.Crossfade(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec = tween(),
    content: BoxScope.(T) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = { animationSpec },
        content = content,
    )
}
