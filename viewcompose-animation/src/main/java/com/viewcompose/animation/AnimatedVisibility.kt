package com.viewcompose.animation

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.AnimatedVisibilityHostNodeProps
import com.viewcompose.widget.core.Box
import com.viewcompose.widget.core.BoxScope
import com.viewcompose.widget.core.ColumnScope
import com.viewcompose.widget.core.RowScope
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.remember
import kotlin.math.abs

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
    val visibleState = remember {
        MutableTransitionState(visible)
    }
    animatedVisibilityCore(
        visibleState = visibleState,
        targetVisible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

fun UiTreeBuilder.AnimatedVisibility(
    visibleState: MutableTransitionState<Boolean>,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    content: BoxScope.() -> Unit,
) {
    animatedVisibilityCore(
        visibleState = visibleState,
        targetVisible = visibleState.targetState,
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
    val visibleState = remember {
        MutableTransitionState(visible)
    }
    animatedVisibilityCore(
        visibleState = visibleState,
        targetVisible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

fun RowScope.AnimatedVisibility(
    visibleState: MutableTransitionState<Boolean>,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandHorizontally(),
    exit: ExitTransition = shrinkHorizontally() + fadeOut(),
    content: BoxScope.() -> Unit,
) {
    animatedVisibilityCore(
        visibleState = visibleState,
        targetVisible = visibleState.targetState,
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
    val visibleState = remember {
        MutableTransitionState(visible)
    }
    animatedVisibilityCore(
        visibleState = visibleState,
        targetVisible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

fun ColumnScope.AnimatedVisibility(
    visibleState: MutableTransitionState<Boolean>,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = shrinkVertically() + fadeOut(),
    content: BoxScope.() -> Unit,
) {
    animatedVisibilityCore(
        visibleState = visibleState,
        targetVisible = visibleState.targetState,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content,
    )
}

private fun UiTreeBuilder.animatedVisibilityCore(
    visibleState: MutableTransitionState<Boolean>,
    targetVisible: Boolean,
    modifier: Modifier,
    enter: EnterTransition,
    exit: ExitTransition,
    content: BoxScope.() -> Unit,
) {
    val enterFade = enter.elements.filterIsInstance<EnterTransitionElement.Fade>().lastOrNull()
    val exitFade = exit.elements.filterIsInstance<ExitTransitionElement.Fade>().lastOrNull()
    val enterWidthExpand = enter.findExpandForWidthAxis()
    val enterHeightExpand = enter.findExpandForHeightAxis()
    val exitWidthShrink = exit.findShrinkForWidthAxis()
    val exitHeightShrink = exit.findShrinkForHeightAxis()
    val stateMachine = remember(visibleState) {
        AnimatedVisibilityStateMachine(initialVisible = visibleState.currentState)
    }
    val beforeSnapshot = stateMachine.beforeAnimation(targetVisible = targetVisible)
    val targetAlpha = when (beforeSnapshot.phase) {
        AnimatedVisibilityPhase.PreEnter,
        AnimatedVisibilityPhase.Visible,
        -> 1f

        AnimatedVisibilityPhase.PostExit,
        AnimatedVisibilityPhase.Idle,
        -> exitFade?.targetAlpha ?: 1f
    }
    val alphaSpec = when (beforeSnapshot.phase) {
        AnimatedVisibilityPhase.PreEnter,
        AnimatedVisibilityPhase.Visible,
        -> enterFade?.animationSpec ?: snap()

        AnimatedVisibilityPhase.PostExit,
        AnimatedVisibilityPhase.Idle,
        -> exitFade?.animationSpec ?: snap()
    }
    val targetWidthScale = when (beforeSnapshot.phase) {
        AnimatedVisibilityPhase.PreEnter,
        AnimatedVisibilityPhase.Visible,
        -> 1f

        AnimatedVisibilityPhase.PostExit,
        AnimatedVisibilityPhase.Idle,
        -> exitWidthShrink?.targetScale ?: 1f
    }
    val widthSpec = when (beforeSnapshot.phase) {
        AnimatedVisibilityPhase.PreEnter,
        AnimatedVisibilityPhase.Visible,
        -> enterWidthExpand?.animationSpec ?: snap()

        AnimatedVisibilityPhase.PostExit,
        AnimatedVisibilityPhase.Idle,
        -> exitWidthShrink?.animationSpec ?: snap()
    }
    val targetHeightScale = when (beforeSnapshot.phase) {
        AnimatedVisibilityPhase.PreEnter,
        AnimatedVisibilityPhase.Visible,
        -> 1f

        AnimatedVisibilityPhase.PostExit,
        AnimatedVisibilityPhase.Idle,
        -> exitHeightShrink?.targetScale ?: 1f
    }
    val heightSpec = when (beforeSnapshot.phase) {
        AnimatedVisibilityPhase.PreEnter,
        AnimatedVisibilityPhase.Visible,
        -> enterHeightExpand?.animationSpec ?: snap()

        AnimatedVisibilityPhase.PostExit,
        AnimatedVisibilityPhase.Idle,
        -> exitHeightShrink?.animationSpec ?: snap()
    }
    val alphaState = animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = alphaSpec,
    )
    val widthScaleState = animateFloatAsState(
        targetValue = targetWidthScale,
        animationSpec = widthSpec,
    )
    val heightScaleState = animateFloatAsState(
        targetValue = targetHeightScale,
        animationSpec = heightSpec,
    )
    val exitFinished = alphaState.value.isApproximately(targetAlpha) &&
        widthScaleState.value.isApproximately(targetWidthScale) &&
        heightScaleState.value.isApproximately(targetHeightScale)
    val enterFinished = alphaState.value.isApproximately(targetAlpha) &&
        widthScaleState.value.isApproximately(targetWidthScale) &&
        heightScaleState.value.isApproximately(targetHeightScale)
    val afterSnapshot = stateMachine.afterAnimation(
        targetVisible = targetVisible,
        enterFinished = enterFinished,
        exitFinished = exitFinished,
    )
    visibleState.currentState = when (afterSnapshot.phase) {
        AnimatedVisibilityPhase.PreEnter,
        AnimatedVisibilityPhase.Idle,
        -> false

        AnimatedVisibilityPhase.Visible,
        AnimatedVisibilityPhase.PostExit,
        -> true
    }
    visibleState.targetState = targetVisible
    visibleState.isIdle = (afterSnapshot.phase == AnimatedVisibilityPhase.Visible && targetVisible) ||
        (afterSnapshot.phase == AnimatedVisibilityPhase.Idle && !targetVisible)
    if (!afterSnapshot.shouldRender) {
        return
    }
    val hasSizeTransform = enterWidthExpand != null ||
        enterHeightExpand != null ||
        exitWidthShrink != null ||
        exitHeightShrink != null
    emit(
        type = NodeType.AnimatedVisibilityHost,
        spec = AnimatedVisibilityHostNodeProps(
            alpha = alphaState.value.coerceIn(0f, 1f),
            widthScale = widthScaleState.value.coerceAtLeast(0f),
            heightScale = heightScaleState.value.coerceAtLeast(0f),
            clipToBounds = hasSizeTransform,
        ),
        modifier = modifier,
    ) {
        Box(content = content)
    }
}

private fun EnterTransition.findExpandForWidthAxis(): EnterTransitionElement.Expand? {
    return elements
        .asReversed()
        .filterIsInstance<EnterTransitionElement.Expand>()
        .firstOrNull { it.axis == SizeTransformAxis.Both || it.axis == SizeTransformAxis.Horizontal }
}

private fun EnterTransition.findExpandForHeightAxis(): EnterTransitionElement.Expand? {
    return elements
        .asReversed()
        .filterIsInstance<EnterTransitionElement.Expand>()
        .firstOrNull { it.axis == SizeTransformAxis.Both || it.axis == SizeTransformAxis.Vertical }
}

private fun ExitTransition.findShrinkForWidthAxis(): ExitTransitionElement.Shrink? {
    return elements
        .asReversed()
        .filterIsInstance<ExitTransitionElement.Shrink>()
        .firstOrNull { it.axis == SizeTransformAxis.Both || it.axis == SizeTransformAxis.Horizontal }
}

private fun ExitTransition.findShrinkForHeightAxis(): ExitTransitionElement.Shrink? {
    return elements
        .asReversed()
        .filterIsInstance<ExitTransitionElement.Shrink>()
        .firstOrNull { it.axis == SizeTransformAxis.Both || it.axis == SizeTransformAxis.Vertical }
}

private fun Float.isApproximately(target: Float): Boolean {
    return abs(this - target) <= 0.001f
}
