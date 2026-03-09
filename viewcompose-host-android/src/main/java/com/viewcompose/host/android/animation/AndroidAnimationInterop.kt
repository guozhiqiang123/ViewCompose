package com.viewcompose.host.android.animation

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import com.viewcompose.host.android.AndroidView
import com.viewcompose.host.android.nativeView
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.widget.core.UiTreeBuilder

/**
 * Android-specific animation bridge for cases where business code explicitly opts into View interop.
 *
 * Typical usage:
 * - Use [UiTreeBuilder.MotionLayoutView] for MotionLayout-driven transitions.
 * - Use [Modifier.androidAnimation] to configure a host view and call [AndroidAnimationInterop].
 */
object AndroidAnimationInterop {
    fun beginDelayedTransition(
        targetView: View,
        transition: Transition? = null,
    ): Boolean {
        val sceneRoot = when (targetView) {
            is ViewGroup -> targetView
            else -> targetView.parent as? ViewGroup
        } ?: return false
        if (transition == null) {
            TransitionManager.beginDelayedTransition(sceneRoot)
        } else {
            TransitionManager.beginDelayedTransition(sceneRoot, transition)
        }
        return true
    }

    fun startObjectAnimator(
        target: View,
        propertyName: String,
        vararg values: Float,
        durationMillis: Long = 300L,
        startDelayMillis: Long = 0L,
        interpolator: TimeInterpolator? = null,
        onEnd: (() -> Unit)? = null,
    ): ObjectAnimator {
        return ObjectAnimator.ofFloat(target, propertyName, *values).apply {
            duration = durationMillis
            startDelay = startDelayMillis
            interpolator?.let { this.interpolator = it }
            if (onEnd != null) {
                doOnAnimationEnd(onEnd)
            }
            start()
        }
    }

    fun startValueAnimator(
        from: Float,
        to: Float,
        durationMillis: Long = 300L,
        startDelayMillis: Long = 0L,
        interpolator: TimeInterpolator? = null,
        onUpdate: (Float) -> Unit,
        onEnd: (() -> Unit)? = null,
    ): ValueAnimator {
        return ValueAnimator.ofFloat(from, to).apply {
            duration = durationMillis
            startDelay = startDelayMillis
            interpolator?.let { this.interpolator = it }
            addUpdateListener { animator ->
                onUpdate((animator.animatedValue as? Float) ?: from)
            }
            if (onEnd != null) {
                doOnAnimationEnd(onEnd)
            }
            start()
        }
    }

    fun animateToState(
        motionLayout: MotionLayout,
        endState: Int,
        durationMillis: Int? = null,
    ) {
        durationMillis?.let { motionLayout.setTransitionDuration(it) }
        motionLayout.transitionToState(endState)
    }

    fun animateToStart(
        motionLayout: MotionLayout,
        durationMillis: Int? = null,
    ) {
        durationMillis?.let { motionLayout.setTransitionDuration(it) }
        motionLayout.transitionToStart()
    }

    fun animateToEnd(
        motionLayout: MotionLayout,
        durationMillis: Int? = null,
    ) {
        durationMillis?.let { motionLayout.setTransitionDuration(it) }
        motionLayout.transitionToEnd()
    }
}

fun Modifier.androidAnimation(
    key: Any = Unit,
    configure: (View) -> Unit,
): Modifier {
    return this.nativeView(
        key = key,
        configure = configure,
    )
}

fun UiTreeBuilder.MotionLayoutView(
    factory: (Context) -> MotionLayout,
    update: (MotionLayout) -> Unit = {},
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            factory(context)
        },
        update = { view ->
            update(view as MotionLayout)
        },
        key = key,
        modifier = modifier,
    )
}

private fun ValueAnimator.doOnAnimationEnd(
    onEnd: () -> Unit,
) {
    addListener(
        object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onEnd()
            }
        },
    )
}
