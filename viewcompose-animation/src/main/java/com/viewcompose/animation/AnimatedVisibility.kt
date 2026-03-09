package com.viewcompose.animation

import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.alpha
import com.viewcompose.widget.core.Box
import com.viewcompose.widget.core.BoxScope
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.remember

data class EnterTransition(
    val animationSpec: AnimationSpec = tween(),
    val initialAlpha: Float = 0f,
)

data class ExitTransition(
    val animationSpec: AnimationSpec = tween(),
    val targetAlpha: Float = 0f,
)

fun fadeIn(
    animationSpec: AnimationSpec = tween(),
    initialAlpha: Float = 0f,
): EnterTransition = EnterTransition(
    animationSpec = animationSpec,
    initialAlpha = initialAlpha,
)

fun fadeOut(
    animationSpec: AnimationSpec = tween(),
    targetAlpha: Float = 0f,
): ExitTransition = ExitTransition(
    animationSpec = animationSpec,
    targetAlpha = targetAlpha,
)

fun UiTreeBuilder.AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    content: BoxScope.() -> Unit,
) {
    val renderState = remember {
        mutableStateOf(visible)
    }
    if (visible && !renderState.value) {
        renderState.value = true
    }
    val alphaState = animateFloatAsState(
        targetValue = if (visible) 1f else exit.targetAlpha,
        animationSpec = if (visible) enter.animationSpec else exit.animationSpec,
    )
    if (!visible && alphaState.value <= 0.001f) {
        renderState.value = false
    }
    if (!renderState.value) {
        return
    }
    Box(
        modifier = modifier
            .alpha(alphaState.value),
        content = content,
    )
}
