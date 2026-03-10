package com.viewcompose.animation

import com.viewcompose.animation.core.AnimationSpec
import com.viewcompose.animation.core.InfiniteRepeatableSpec
import com.viewcompose.animation.core.KeyframesSpec
import com.viewcompose.animation.core.RepeatableSpec
import com.viewcompose.animation.core.SnapSpec
import com.viewcompose.animation.core.SpringSpec
import com.viewcompose.animation.core.TweenSpec
import com.viewcompose.animation.core.spring
import com.viewcompose.ui.modifier.AnimateContentSizeModifierElement
import com.viewcompose.ui.modifier.ContentSizeAnimationSpecModel
import com.viewcompose.ui.modifier.ContentSizeKeyframesSpecModel
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
        )

        is SpringSpec -> ContentSizeSpringSpecModel(
            durationMillis = durationMillis,
        )

        is KeyframesSpec -> ContentSizeKeyframesSpecModel(
            durationMillis = durationMillis,
        )

        is RepeatableSpec -> animation.toContentSizeSpecModel()
        is InfiniteRepeatableSpec -> animation.toContentSizeSpecModel()
        SnapSpec -> ContentSizeSnapSpecModel
    }
}
