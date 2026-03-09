package com.viewcompose.animation

fun interface Easing {
    fun transform(fraction: Float): Float
}

object EasingDefaults {
    val Linear: Easing = Easing { it }
    val FastOutSlowIn: Easing = Easing { fraction ->
        val t = fraction.coerceIn(0f, 1f)
        (3f * t * t) - (2f * t * t * t)
    }
    val LinearOutSlowIn: Easing = Easing { fraction ->
        val t = fraction.coerceIn(0f, 1f)
        1f - (1f - t) * (1f - t)
    }
    val FastOutLinearIn: Easing = Easing { fraction ->
        val t = fraction.coerceIn(0f, 1f)
        t * t
    }
}
