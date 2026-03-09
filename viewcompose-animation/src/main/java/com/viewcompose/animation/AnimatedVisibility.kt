package com.viewcompose.animation

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.alpha
import com.viewcompose.widget.core.Box
import com.viewcompose.widget.core.BoxScope
import com.viewcompose.widget.core.ColumnScope
import com.viewcompose.widget.core.RowScope
import com.viewcompose.widget.core.UiTreeBuilder

enum class SizeTransformAxis {
    Both,
    Horizontal,
    Vertical,
}

sealed interface EnterTransitionElement {
    data class Fade(
        val animationSpec: AnimationSpec = tween(),
        val initialAlpha: Float = 0f,
    ) : EnterTransitionElement

    data class Expand(
        val animationSpec: AnimationSpec = tween(),
        val initialScale: Float = 0f,
        val axis: SizeTransformAxis = SizeTransformAxis.Both,
    ) : EnterTransitionElement
}

sealed interface ExitTransitionElement {
    data class Fade(
        val animationSpec: AnimationSpec = tween(),
        val targetAlpha: Float = 0f,
    ) : ExitTransitionElement

    data class Shrink(
        val animationSpec: AnimationSpec = tween(),
        val targetScale: Float = 0f,
        val axis: SizeTransformAxis = SizeTransformAxis.Both,
    ) : ExitTransitionElement
}

data class EnterTransition(
    val elements: List<EnterTransitionElement>,
) {
    operator fun plus(other: EnterTransition): EnterTransition {
        return EnterTransition(elements + other.elements)
    }
}

data class ExitTransition(
    val elements: List<ExitTransitionElement>,
) {
    operator fun plus(other: ExitTransition): ExitTransition {
        return ExitTransition(elements + other.elements)
    }
}

fun fadeIn(
    animationSpec: AnimationSpec = tween(),
    initialAlpha: Float = 0f,
): EnterTransition = EnterTransition(
    elements = listOf(
        EnterTransitionElement.Fade(
            animationSpec = animationSpec,
            initialAlpha = initialAlpha,
        ),
    ),
)

fun expandIn(
    animationSpec: AnimationSpec = tween(),
    initialScale: Float = 0f,
): EnterTransition = EnterTransition(
    elements = listOf(
        EnterTransitionElement.Expand(
            animationSpec = animationSpec,
            initialScale = initialScale,
            axis = SizeTransformAxis.Both,
        ),
    ),
)

fun expandHorizontally(
    animationSpec: AnimationSpec = tween(),
    initialScale: Float = 0f,
): EnterTransition = EnterTransition(
    elements = listOf(
        EnterTransitionElement.Expand(
            animationSpec = animationSpec,
            initialScale = initialScale,
            axis = SizeTransformAxis.Horizontal,
        ),
    ),
)

fun expandVertically(
    animationSpec: AnimationSpec = tween(),
    initialScale: Float = 0f,
): EnterTransition = EnterTransition(
    elements = listOf(
        EnterTransitionElement.Expand(
            animationSpec = animationSpec,
            initialScale = initialScale,
            axis = SizeTransformAxis.Vertical,
        ),
    ),
)

fun fadeOut(
    animationSpec: AnimationSpec = tween(),
    targetAlpha: Float = 0f,
): ExitTransition = ExitTransition(
    elements = listOf(
        ExitTransitionElement.Fade(
            animationSpec = animationSpec,
            targetAlpha = targetAlpha,
        ),
    ),
)

fun shrinkOut(
    animationSpec: AnimationSpec = tween(),
    targetScale: Float = 0f,
): ExitTransition = ExitTransition(
    elements = listOf(
        ExitTransitionElement.Shrink(
            animationSpec = animationSpec,
            targetScale = targetScale,
            axis = SizeTransformAxis.Both,
        ),
    ),
)

fun shrinkHorizontally(
    animationSpec: AnimationSpec = tween(),
    targetScale: Float = 0f,
): ExitTransition = ExitTransition(
    elements = listOf(
        ExitTransitionElement.Shrink(
            animationSpec = animationSpec,
            targetScale = targetScale,
            axis = SizeTransformAxis.Horizontal,
        ),
    ),
)

fun shrinkVertically(
    animationSpec: AnimationSpec = tween(),
    targetScale: Float = 0f,
): ExitTransition = ExitTransition(
    elements = listOf(
        ExitTransitionElement.Shrink(
            animationSpec = animationSpec,
            targetScale = targetScale,
            axis = SizeTransformAxis.Vertical,
        ),
    ),
)

fun UiTreeBuilder.AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    content: BoxScope.() -> Unit,
) {
    animatedVisibilityCore(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

fun RowScope.AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandHorizontally(),
    exit: ExitTransition = shrinkHorizontally() + fadeOut(),
    content: BoxScope.() -> Unit,
) {
    animatedVisibilityCore(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

fun ColumnScope.AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = shrinkVertically() + fadeOut(),
    content: BoxScope.() -> Unit,
) {
    animatedVisibilityCore(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

private fun UiTreeBuilder.animatedVisibilityCore(
    visible: Boolean,
    modifier: Modifier,
    enter: EnterTransition,
    exit: ExitTransition,
    content: BoxScope.() -> Unit,
) {
    val enterFade = enter.elements.filterIsInstance<EnterTransitionElement.Fade>().lastOrNull()
    val exitFade = exit.elements.filterIsInstance<ExitTransitionElement.Fade>().lastOrNull()
    val targetAlpha = when {
        visible -> 1f
        exitFade != null -> exitFade.targetAlpha
        else -> 0f
    }
    val alphaSpec = when {
        visible -> enterFade?.animationSpec ?: tween()
        exitFade != null -> exitFade.animationSpec
        else -> tween()
    }
    val alphaState = animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = alphaSpec,
    )
    val shouldRender = visible || alphaState.value > (targetAlpha + 0.001f)
    if (!shouldRender) {
        return
    }
    Box(
        modifier = modifier.alpha(alphaState.value.coerceIn(0f, 1f)),
        content = content,
    )
}
