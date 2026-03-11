package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationSpec
import com.viewcompose.animation.core.CubicBezierEasing
import com.viewcompose.animation.core.Easing
import com.viewcompose.animation.core.EasingDefaults
import com.viewcompose.animation.core.InfiniteRepeatableSpec
import com.viewcompose.animation.core.KeyframesSpec
import com.viewcompose.animation.core.RepeatMode
import com.viewcompose.animation.core.RepeatableSpec
import com.viewcompose.animation.core.SnapSpec
import com.viewcompose.animation.core.SpringSpec
import com.viewcompose.animation.core.TweenSpec
import com.viewcompose.animation.core.spring
import com.viewcompose.ui.modifier.AnimateContentSizeModifierElement
import com.viewcompose.ui.modifier.ContentSizeAnimationSpecModel
import com.viewcompose.ui.modifier.ContentSizeEasingModel
import com.viewcompose.ui.modifier.ContentSizeInfiniteRepeatableSpecModel
import com.viewcompose.ui.modifier.ContentSizeKeyframeModel
import com.viewcompose.ui.modifier.ContentSizeKeyframesSpecModel
import com.viewcompose.ui.modifier.ContentSizeRepeatModeModel
import com.viewcompose.ui.modifier.ContentSizeRepeatableSpecModel
import com.viewcompose.ui.modifier.ContentSizeSnapSpecModel
import com.viewcompose.ui.modifier.ContentSizeSpringSpecModel
import com.viewcompose.ui.modifier.ContentSizeTweenSpecModel
import com.viewcompose.ui.modifier.Modifier

fun Modifier.animateContentSize(
    animationSpec: AnimationSpec = spring(),
): Modifier {
    return then(
        AnimateContentSizeModifierElement(
            animationSpec = animationSpec.toContentSizeSpecModel(),
        ),
    )
}

private fun AnimationSpec.toContentSizeSpecModel(): ContentSizeAnimationSpecModel {
    return when (this) {
        is TweenSpec -> ContentSizeTweenSpecModel(
            durationMillis = durationMillis,
            delayMillis = delayMillis,
            easing = easing.toContentSizeEasingModel(),
        )

        is SpringSpec -> ContentSizeSpringSpecModel(
            durationMillis = durationMillis,
            dampingRatio = dampingRatio,
            stiffness = stiffness,
        )

        is KeyframesSpec -> ContentSizeKeyframesSpecModel(
            durationMillis = durationMillis,
            keyframes = keyframes.map { keyframe ->
                ContentSizeKeyframeModel(
                    timeMillis = keyframe.timeMillis,
                    valueFraction = keyframe.valueFraction,
                )
            },
        )

        is RepeatableSpec -> ContentSizeRepeatableSpecModel(
            iterations = iterations,
            animation = animation.toContentSizeSpecModel(),
            repeatMode = repeatMode.toContentSizeRepeatMode(),
        )

        is InfiniteRepeatableSpec -> ContentSizeInfiniteRepeatableSpecModel(
            animation = animation.toContentSizeSpecModel(),
            repeatMode = repeatMode.toContentSizeRepeatMode(),
        )
        SnapSpec -> ContentSizeSnapSpecModel
    }
}

private fun RepeatMode.toContentSizeRepeatMode(): ContentSizeRepeatModeModel {
    return when (this) {
        RepeatMode.Restart -> ContentSizeRepeatModeModel.Restart
        RepeatMode.Reverse -> ContentSizeRepeatModeModel.Reverse
    }
}

private fun Easing.toContentSizeEasingModel(): ContentSizeEasingModel {
    return when (this) {
        EasingDefaults.Linear -> ContentSizeEasingModel.Linear
        EasingDefaults.FastOutSlowIn -> ContentSizeEasingModel.FastOutSlowIn
        EasingDefaults.LinearOutSlowIn -> ContentSizeEasingModel.LinearOutSlowIn
        EasingDefaults.FastOutLinearIn -> ContentSizeEasingModel.FastOutLinearIn
        is CubicBezierEasing -> ContentSizeEasingModel.CubicBezier(
            x1 = x1,
            y1 = y1,
            x2 = x2,
            y2 = y2,
        )

        else -> ContentSizeEasingModel.FastOutSlowIn
    }
}
