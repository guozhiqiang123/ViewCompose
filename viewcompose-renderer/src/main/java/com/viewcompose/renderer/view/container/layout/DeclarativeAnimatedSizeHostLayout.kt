package com.viewcompose.renderer.view.container

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.viewcompose.renderer.view.tree.LayoutPassTracker
import com.viewcompose.ui.modifier.ContentSizeAnimationSpecModel
import com.viewcompose.ui.modifier.ContentSizeKeyframesSpecModel
import com.viewcompose.ui.modifier.ContentSizeSnapSpecModel
import com.viewcompose.ui.modifier.ContentSizeSpringSpecModel
import com.viewcompose.ui.modifier.ContentSizeTweenSpecModel
import kotlin.math.roundToInt

internal class DeclarativeAnimatedSizeHostLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    var animationSpec: ContentSizeAnimationSpecModel = ContentSizeSnapSpecModel
        set(value) {
            field = value
        }

    private var animatedWidthPx: Float = -1f
    private var animatedHeightPx: Float = -1f
    private var targetWidthPx: Int = 0
    private var targetHeightPx: Int = 0
    private var sizeAnimator: ValueAnimator? = null

    init {
        clipChildren = true
        clipToPadding = true
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        val startNs = System.nanoTime()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredWidth = measuredWidth
        val desiredHeight = measuredHeight
        if (animatedWidthPx < 0f || animatedHeightPx < 0f) {
            animatedWidthPx = desiredWidth.toFloat()
            animatedHeightPx = desiredHeight.toFloat()
            targetWidthPx = desiredWidth
            targetHeightPx = desiredHeight
        } else if (desiredWidth != targetWidthPx || desiredHeight != targetHeightPx) {
            targetWidthPx = desiredWidth
            targetHeightPx = desiredHeight
            startSizeAnimation()
        }
        setMeasuredDimension(
            resolveSize(animatedWidthPx.roundToInt().coerceAtLeast(0), widthMeasureSpec),
            resolveSize(animatedHeightPx.roundToInt().coerceAtLeast(0), heightMeasureSpec),
        )
        LayoutPassTracker.recordMeasure(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val startNs = System.nanoTime()
        super.onLayout(changed, left, top, right, bottom)
        LayoutPassTracker.recordLayout(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sizeAnimator?.cancel()
        sizeAnimator = null
    }

    private fun startSizeAnimation() {
        sizeAnimator?.cancel()
        val startWidth = animatedWidthPx
        val startHeight = animatedHeightPx
        val endWidth = targetWidthPx.toFloat()
        val endHeight = targetHeightPx.toFloat()
        if (startWidth == endWidth && startHeight == endHeight) {
            return
        }
        val config = animationSpec.resolveConfig()
        if (config.durationMillis <= 0L) {
            animatedWidthPx = endWidth
            animatedHeightPx = endHeight
            requestLayout()
            return
        }
        sizeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = config.durationMillis
            startDelay = config.delayMillis
            interpolator = config.interpolator
            addUpdateListener { animator ->
                val fraction = animator.animatedValue as Float
                animatedWidthPx = lerp(startWidth, endWidth, fraction)
                animatedHeightPx = lerp(startHeight, endHeight, fraction)
                requestLayout()
            }
            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animatedWidthPx = endWidth
                        animatedHeightPx = endHeight
                        requestLayout()
                    }
                },
            )
            start()
        }
    }

    private data class AnimationRuntimeConfig(
        val durationMillis: Long,
        val delayMillis: Long,
        val interpolator: android.animation.TimeInterpolator,
    )

    private fun ContentSizeAnimationSpecModel.resolveConfig(): AnimationRuntimeConfig {
        return when (this) {
            is ContentSizeTweenSpecModel -> AnimationRuntimeConfig(
                durationMillis = durationMillis.toLong().coerceAtLeast(1L),
                delayMillis = delayMillis.toLong().coerceAtLeast(0L),
                interpolator = AccelerateDecelerateInterpolator(),
            )

            is ContentSizeSpringSpecModel -> AnimationRuntimeConfig(
                durationMillis = durationMillis.toLong().coerceAtLeast(1L),
                delayMillis = 0L,
                interpolator = DecelerateInterpolator(),
            )

            is ContentSizeKeyframesSpecModel -> AnimationRuntimeConfig(
                durationMillis = durationMillis.toLong().coerceAtLeast(1L),
                delayMillis = 0L,
                interpolator = AccelerateDecelerateInterpolator(),
            )

            ContentSizeSnapSpecModel -> AnimationRuntimeConfig(
                durationMillis = 0L,
                delayMillis = 0L,
                interpolator = AccelerateDecelerateInterpolator(),
            )
        }
    }

    private fun lerp(
        start: Float,
        end: Float,
        fraction: Float,
    ): Float {
        return start + (end - start) * fraction
    }
}
